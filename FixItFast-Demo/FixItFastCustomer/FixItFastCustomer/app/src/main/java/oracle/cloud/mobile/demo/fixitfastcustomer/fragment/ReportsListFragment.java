package oracle.cloud.mobile.demo.fixitfastcustomer.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.activity.ReportActivity;
import oracle.cloud.mobile.demo.fixitfastcustomer.model.IncidentReport;
import oracle.cloud.mobile.demo.fixitfastcustomer.model.IncidentReportsAdapter;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Constants;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;
import oracle.cloud.mobile.mobilebackend.MobileBackendManager;

public class ReportsListFragment extends ListFragment {

    private static final String TAG = "FiF Application";

    private List<IncidentReport> irList = new ArrayList<IncidentReport>();

    public ReportsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setProgressBarIndeterminateVisibility(true);
        new GetIncidentReportsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        IncidentReport ir = (IncidentReport) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(Constants.EXTRA_ID, ir.id);
        startActivity(intent);
    }

    class GetIncidentReportsTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Log.d(Constants.LOG_TAG, "---GetIncidentReports---");

            HttpClient httpclient = Util.getHttpClient();
            HttpResponse response;

            try {
                String baseUrl = MobileBackendManager.getManager().getDefaultMobileBackend(getActivity()).getConfig().getUrl();
                HttpGet get = new HttpGet(baseUrl + Constants.INCIDENT_REPORTS_URL + "?contact=" + Util.getLoggedInUser(getActivity()));

                Log.d(Constants.LOG_TAG, "Calling: " + get.getURI());

                get.addHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
                get.addHeader(Constants.AUTHORIZATION, Util.authorization(getActivity()).getHTTPHeaders().get(Constants.AUTHORIZATION));

                Log.d(Constants.LOG_TAG, "Headers: " + Arrays.toString(get.getAllHeaders()));

                response = httpclient.execute(get);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    if (response.getEntity().getContentType() == null) {
                        Util.toastAndLog(getActivity(), "You have no Incident Reports");
                        return null;
                    }

                    String body = EntityUtils.toString(response.getEntity());

                    Log.d(Constants.LOG_TAG, "Success: got Incident Reports:");
                    JSONArray array = new JSONObject(body).getJSONArray("items");
                    Log.d(Constants.LOG_TAG, array.toString());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = (JSONObject) array.get(i);

                        IncidentReport ir = new Gson().fromJson(o.toString(), IncidentReport.class);

                        irList.add(ir);
                    }
                } else {
                    Util.toastAndLog(getActivity(), "Get Incident Reports failed, status " + statusLine.getStatusCode());
                }
            } catch (Exception e) {
                Util.toastAndLog(getActivity(), "Incident Reports GET failed, ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String arg) {
            super.onPostExecute(arg);
            setListAdapter(new IncidentReportsAdapter(getActivity(), R.layout.row_report, irList));
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

}