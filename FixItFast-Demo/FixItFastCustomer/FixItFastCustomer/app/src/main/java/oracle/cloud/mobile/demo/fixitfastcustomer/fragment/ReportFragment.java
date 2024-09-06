package oracle.cloud.mobile.demo.fixitfastcustomer.fragment;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Arrays;

import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.model.IncidentReport;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Constants;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;
import oracle.cloud.mobile.exception.ServiceProxyException;
import oracle.cloud.mobile.mobilebackend.MobileBackendManager;
import oracle.cloud.mobile.storage.Storage;
import oracle.cloud.mobile.storage.StorageCollection;
import oracle.cloud.mobile.storage.StorageObject;

public class ReportFragment extends Fragment {

    private Storage storage;

    private int id;
    private IncidentReport ir;
    private ImageView thumbnail;
    private EditText updateText;
    private TextView title, desc;

    private byte[] imageBytes;

    public ReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = Util.storage(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        id = getActivity().getIntent().getIntExtra(Constants.EXTRA_ID, -1);

        thumbnail = (ImageView) rootView.findViewById(R.id.image);
        title = (TextView) rootView.findViewById(R.id.titleTextView);
        desc = (TextView) rootView.findViewById(R.id.descTextView);
        updateText = (EditText) rootView.findViewById(R.id.updateInput);

        Button updateButton = (Button) rootView.findViewById(R.id.sendButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setProgressBarIndeterminateVisibility(true);
                new UpdateIncidentReportTask().execute();
            }
        });


        getActivity().setProgressBarIndeterminateVisibility(true);
        new GetIncidentReportTask().execute();

        return rootView;
    }

    class GetIncidentReportTask extends AsyncTask<Void, Void, IncidentReport> {
        @Override
        protected IncidentReport doInBackground(Void... args) {
            Log.d(Constants.LOG_TAG, "---GetIncidentReport---");

            HttpClient httpClient = Util.getHttpClient();
            HttpResponse response;

            try {
                String baseUrl = MobileBackendManager.getManager().getDefaultMobileBackend(getActivity()).getConfig().getUrl();
                HttpGet httpGet = new HttpGet(baseUrl + Constants.INCIDENT_REPORTS_URL + "/" + id);
                httpGet.addHeader(Constants.X_BACKEND_TOKEN, Constants.MBE_TOKEN);
                httpGet.addHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
                httpGet.addHeader(Constants.AUTHORIZATION, Util.authorization(getActivity()).getHTTPHeaders().get(Constants.AUTHORIZATION));

                response = httpClient.execute(httpGet);

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d(Constants.LOG_TAG, "Success: got Incident Report:");
                    String body = EntityUtils.toString(response.getEntity());
                    Log.d(Constants.LOG_TAG, new JSONObject(body).toString());

                    return new Gson().fromJson(body, IncidentReport.class);
                } else {
                    Util.toastAndLog(getActivity(), "Get Incident Report failed, status " + statusLine.getStatusCode());
                }
            } catch (Exception ex) {
                Util.toastAndLog(getActivity(), "Incident Report GET failed, ", ex);
            }

            return null;
        }

        @Override
        protected void onPostExecute(IncidentReport arg) {
            super.onPostExecute(arg);

            ir = arg;

            title.setText(ir.title);
            desc.setText(ir.notes);

            if (ir.imageLink != null && !ir.imageLink.isEmpty()) {
                new GetImageTask().execute();
            } else {
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        }
    }

    class GetImageTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Log.d(Constants.LOG_TAG, "---GetImage---");

            try {
                String key = ir.imageLink.substring(ir.imageLink.lastIndexOf('/') + 1, ir.imageLink.indexOf("?user="));
                Log.d(Constants.LOG_TAG, "Getting Storage Object " + key);

                StorageCollection imagesCollection = storage.getStorageCollection(Constants.COLLECTION_IMAGES);
                StorageObject image = imagesCollection.get(key);
                imageBytes = image.getPayloadBytes();
                Log.d(Constants.LOG_TAG, "Success: object bytes:");
                Log.d(Constants.LOG_TAG, Arrays.toString(imageBytes));
            } catch (Exception e) {
                Util.toastAndLog(getActivity(), "Image GET failed", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String arg) {
            super.onPostExecute(arg);

            if (imageBytes != null) {
                thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    class UpdateIncidentReportTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg) {
            Log.d(Constants.LOG_TAG, "---UpdateIncidentReport---");

            HttpClient httpClient = Util.getHttpClient();
            HttpResponse response;

            try {
                String baseUrl = MobileBackendManager.getManager().getDefaultMobileBackend(getActivity()).getConfig().getUrl();
                HttpPut put = new HttpPut(baseUrl + Constants.INCIDENT_REPORTS_URL + "/" + ir.id + "/status");
                put.addHeader(Constants.X_BACKEND_TOKEN, Constants.MBE_TOKEN);
                put.addHeader(Constants.AUTHORIZATION, Util.authorization(getActivity()).getHTTPHeaders().get(Constants.AUTHORIZATION));
                put.addHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);

                JSONObject newIncidentReport = new JSONObject();
                newIncidentReport.put("Status", "Customer Update");
                newIncidentReport.put("Notes", updateText.getText().toString());

                Log.d(Constants.LOG_TAG, "Sending JSON:");
                Log.d(Constants.LOG_TAG, newIncidentReport.toString());

                put.setEntity(new StringEntity(newIncidentReport.toString()));
                response = httpClient.execute(put);

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d(Constants.LOG_TAG, "Success: response body:");
                    String body = EntityUtils.toString(response.getEntity());
                    Log.d(Constants.LOG_TAG, body);
                } else {
                    Util.toastAndLog(getActivity(), "Error calling Incident Reports API: Status " + statusLine);
                }
            } catch (Exception e) {
                Util.toastAndLog(getActivity(), "Error calling Incident Reports API: " + e.getClass().getSimpleName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            super.onPostExecute(arg);
            getActivity().setProgressBarIndeterminateVisibility(false);
            getActivity().finish();
        }
    }

}