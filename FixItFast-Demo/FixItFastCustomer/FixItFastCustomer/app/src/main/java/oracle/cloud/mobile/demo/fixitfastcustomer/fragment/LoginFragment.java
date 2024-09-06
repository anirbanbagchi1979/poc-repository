package oracle.cloud.mobile.demo.fixitfastcustomer.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import oracle.cloud.mobile.authorization.AuthorizationCallback;
import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.activity.MainActivity;
import oracle.cloud.mobile.demo.fixitfastcustomer.activity.RegisterActivity;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Constants;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;
import oracle.cloud.mobile.exception.ServiceProxyException;

public class LoginFragment extends Fragment {

    private TextView username, password, error;
    private boolean inProgress;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Button signInButton = (Button) rootView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.INVISIBLE);
                getActivity().setProgressBarIndeterminateVisibility(true);
                signIn();
            }
        });

        Button registerButton = (Button) rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        username = (TextView) rootView.findViewById(R.id.username);
        password = (TextView) rootView.findViewById(R.id.password);
        error = (TextView) rootView.findViewById(R.id.errorText);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.LOG_TAG, Context.MODE_PRIVATE);
        username.setText(prefs.getString("username", ""));
        password.setText(prefs.getString("password", ""));

        return rootView;
    }

    private void signIn() {
        if (!inProgress) {
            Log.d(Constants.LOG_TAG, "---Login---");

            inProgress = true;
            Util.authorization(getActivity()).authenticate(getActivity(), username.getText().toString(), password.getText().toString(), authCallback);
        }
    }

    private AuthorizationCallback authCallback = new AuthorizationCallback() {
        @Override
        public void onCompletion(ServiceProxyException e) {
            inProgress = false;
            getActivity().setProgressBarIndeterminateVisibility(false);

            if (e == null) {
                Log.d(Constants.LOG_TAG, "Success: OAuth token:");
                Log.d(Constants.LOG_TAG, Util.authorization(getActivity()).getHTTPHeaders().get(Constants.AUTHORIZATION));

                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.LOG_TAG, Context.MODE_PRIVATE);
                prefs.edit().putString("username", username.getText().toString()).apply();
                prefs.edit().putString("password", password.getText().toString()).apply();

                Util.toastAndLog(getActivity(), "--registerForNotifications--");
                Util.notifications(getActivity()).initialize(getActivity(), Constants.GOOGLE_PROJECT_NUMBER);
                Util.toastAndLog(getActivity(), "--finished registerForNotifications--");
                Util.toastAndLog(getActivity(), "--Marcelo--");

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else {
                Log.e(Constants.LOG_TAG, "Login Error", e);
                error.setVisibility(View.VISIBLE);
            }
        }
    };

}