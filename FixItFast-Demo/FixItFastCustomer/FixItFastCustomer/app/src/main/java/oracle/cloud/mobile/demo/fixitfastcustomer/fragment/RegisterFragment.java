package oracle.cloud.mobile.demo.fixitfastcustomer.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import oracle.cloud.mobile.authorization.AuthorizationCallback;
import oracle.cloud.mobile.authorization.User;
import oracle.cloud.mobile.authorization.UserRegistrationCallback;
import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.activity.MainActivity;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Constants;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;
import oracle.cloud.mobile.exception.ServiceProxyException;
import oracle.cloud.mobile.mobilebackend.MobileBackendManager;

public class RegisterFragment extends Fragment {

    private boolean inProgress;

    private TextView username, password, email, firstName, lastName, address, city, postalCode;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        username = (TextView) rootView.findViewById(R.id.username);
        password = (TextView) rootView.findViewById(R.id.password);
        email = (TextView) rootView.findViewById(R.id.email);
        firstName = (TextView) rootView.findViewById(R.id.firstName);
        lastName = (TextView) rootView.findViewById(R.id.lastName);
        address = (TextView) rootView.findViewById(R.id.address);
        city = (TextView) rootView.findViewById(R.id.city);
        postalCode = (TextView) rootView.findViewById(R.id.postalCode);

        Button registerButton = (Button) rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setProgressBarIndeterminateVisibility(true);
                registerUser();
            }
        });

        return rootView;
    }

    private void registerUser() {
        if (!inProgress) {
            Log.d(Constants.LOG_TAG, "---RegisterUser---");
            inProgress = true;

            // First do anonymous authentication
            Util.authorization(getActivity()).authenticateAnonymous(getActivity(), anonAuthCallback);
        }
    }

    private AuthorizationCallback anonAuthCallback = new AuthorizationCallback() {
        @Override
        public void onCompletion(ServiceProxyException e) {
            if (e == null) {
                Log.d(Constants.LOG_TAG, "Success: Anonymous authentication");
                Util.authorization(getActivity()).registerUser(username.getText().toString(), password.getText().toString(), null, registrationCallback);
            } else {
                Log.e(Constants.LOG_TAG, "Anonymous authentication Error", e);
            }
        }
    };

    private UserRegistrationCallback registrationCallback = new UserRegistrationCallback() {
        @Override
        public void onComplete(ServiceProxyException e, User user) {
            inProgress = false;
            if (e == null) {
                Util.toastAndLog(getActivity(), "User " + user.getUsername() + " has been created.");
                getActivity().setProgressBarIndeterminateVisibility(true);
                new CreateContactTask().execute();
            } else {
                Util.toastAndLog(getActivity(), "Registration Error", e);
            }
        }
    };

    class CreateContactTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(Constants.LOG_TAG, "---CreateContactTask---");

            HttpClient httpClient = Util.getHttpClient();
            HttpResponse response;

            try {
                String baseUrl = MobileBackendManager.getManager().getDefaultMobileBackend(getActivity()).getConfig().getUrl();
                HttpPost post = new HttpPost(baseUrl + Constants.CONTACTS_URL);
                post.addHeader(Constants.X_BACKEND_TOKEN, Constants.MBE_TOKEN);
                post.addHeader(Constants.AUTHORIZATION, Util.authorization(getActivity()).getHTTPHeaders().get(Constants.AUTHORIZATION));
                post.addHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);

                JSONObject newContact = new JSONObject();

                //newContact.put("Username", username.getText().toString());
                newContact.put("EmailAddress", username.getText().toString());
                newContact.put("FirstName", firstName.getText().toString());
                newContact.put("LastName", lastName.getText().toString());
                newContact.put("AddressLine", address.getText().toString());
                newContact.put("City", city.getText().toString());
                newContact.put("PostalCode", postalCode.getText().toString());

                Log.d(Constants.LOG_TAG, "Posting JSON:");
                Log.d(Constants.LOG_TAG, post.getURI().toString());
                Log.d(Constants.LOG_TAG, newContact.toString());

                post.setEntity(new StringEntity(newContact.toString()));
                response = httpClient.execute(post);

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    Log.d(Constants.LOG_TAG, "Success: " + statusLine.getStatusCode());
                } else {
                    Util.toastAndLog(getActivity(), "Error calling Contacts API: Status " + statusLine);
                }
            } catch (Exception e) {
                Util.toastAndLog(getActivity(), "Error calling Contacts API", e);
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