package oracle.cloud.mobile.demo.fixitfastcustomer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import oracle.cloud.mobile.analytics.Analytics;
import oracle.cloud.mobile.analytics.Event;
import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Constants;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;
import oracle.cloud.mobile.exception.ServiceProxyException;
import oracle.cloud.mobile.mobilebackend.MobileBackendManager;
import oracle.cloud.mobile.storage.Storage;
import oracle.cloud.mobile.storage.StorageCollection;
import oracle.cloud.mobile.storage.StorageObject;

public class NewReportFragment extends Fragment {

    private Storage storage;
    private Analytics analytics;

    private ImageView thumbnail;
    private EditText title, desc;
    private TextView tapToUploadMessage;

    private byte[] imageBytes = new byte[0];

    public NewReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = Util.storage(getActivity());
        analytics = Util.analytics(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_report, container, false);

        // Turn on controls for editing
        rootView.findViewById(R.id.instruction).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.sendButton).setVisibility(View.VISIBLE);

        FrameLayout imageFrame = (FrameLayout) rootView.findViewById(R.id.imageFrame);
        imageFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });

        Button sendButton = (Button) rootView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submitIncidentReport();
                } catch (Exception e) {
                    Util.toastAndLog(getActivity(), "Error Submitting Incident Report: " + e.getClass().getSimpleName());
                }
            }
        });

        thumbnail = (ImageView) rootView.findViewById(R.id.image);
        title = (EditText) rootView.findViewById(R.id.titleInput);
        desc = (EditText) rootView.findViewById(R.id.descInput);
        tapToUploadMessage = (TextView) rootView.findViewById(R.id.instruction);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap imageReturned = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageReturned.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();
            thumbnail.setImageBitmap(imageReturned);
            tapToUploadMessage.setVisibility(View.GONE);
        }
    }

    private void submitIncidentReport() throws Exception {
        // Start by uploading the image - when UploadImageTask completes, it will
        // submit the Incident Report, including image link
        getActivity().setProgressBarIndeterminateVisibility(true);
        new UploadImageTask().execute();
    }

    class UploadImageTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... args) {
            Log.d(Constants.LOG_TAG, "---UploadImage---");

            String canonicalLink = null;

            if (imageBytes.length > 0) {
                try {
                    StorageCollection imagesCollection = storage.getStorageCollection(Constants.COLLECTION_IMAGES);
                    StorageObject imageToUpload = new StorageObject(null, imageBytes, Constants.IMAGE_JPEG);
                    StorageObject uploadedImage = imagesCollection.post(imageToUpload);
                    canonicalLink = uploadedImage.getCanonicalLink();
                    Log.d(Constants.LOG_TAG, "Success: image link: " + canonicalLink);
                } catch (ServiceProxyException e) {
                    Util.toastAndLog(getActivity(), "Error calling Storage API: Status " + e.getErrorCode(), e);
                }
            }

            Log.d(Constants.LOG_TAG, "---LogAnalytics---");
            Event customEvent = new Event("AppSubmission", new Date(), null);
            customEvent.addProperty("Username", Util.getLoggedInUser(getActivity()));
            customEvent.addProperty("ImageAttached", new Boolean(imageBytes.length > 0).toString());
            analytics.logEvent(customEvent);
            analytics.flush();

            return canonicalLink;
        }

        @Override
        protected void onPostExecute(String canonicalLink) {
            super.onPostExecute(canonicalLink);
            new SubmitIncidentReportTask().execute(canonicalLink);
        }
    }

    class SubmitIncidentReportTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... canonicalLink) {
            Log.d(Constants.LOG_TAG, "---SubmitIncidentReport---");

            HttpClient httpClient = Util.getHttpClient();
            HttpResponse response;

            try {
                String baseUrl = MobileBackendManager.getManager().getDefaultMobileBackend(getActivity()).getConfig().getUrl();
                HttpPost post = new HttpPost(baseUrl + Constants.INCIDENT_REPORTS_URL);
                post.addHeader(Constants.X_BACKEND_TOKEN, Constants.MBE_TOKEN);
                post.addHeader(Constants.AUTHORIZATION, Util.authorization(getActivity()).getHTTPHeaders().get(Constants.AUTHORIZATION));
                post.addHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);

                JSONObject newIncidentReport = new JSONObject();
                newIncidentReport.put("Title", title.getText().toString());
                newIncidentReport.put("UserName", Util.getLoggedInUser(getActivity()));
                newIncidentReport.put("ImageLink", canonicalLink[0]);
                newIncidentReport.put("Notes", desc.getText().toString());

                Log.d(Constants.LOG_TAG, "Posting JSON:");
                Log.d(Constants.LOG_TAG, post.getURI().toString());
                Log.d(Constants.LOG_TAG, newIncidentReport.toString());

                post.setEntity(new StringEntity(newIncidentReport.toString()));
                response = httpClient.execute(post);

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d(Constants.LOG_TAG, "Success: response JSON:");
                    String body = EntityUtils.toString(response.getEntity());
                    Log.d(Constants.LOG_TAG, new JSONObject(body).toString());
                } else {
                    Util.toastAndLog(getActivity(), "Error calling Incident Reports API: Status " + statusLine);
                }
            } catch (Exception e) {
                Util.toastAndLog(getActivity(), "Error calling Incident Reports API", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            super.onPostExecute(arg);
            getActivity().finish();
        }
    }

}