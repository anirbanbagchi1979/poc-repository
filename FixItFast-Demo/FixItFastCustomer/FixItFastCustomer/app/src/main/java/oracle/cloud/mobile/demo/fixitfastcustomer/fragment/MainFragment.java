package oracle.cloud.mobile.demo.fixitfastcustomer.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.activity.NewReportActivity;
import oracle.cloud.mobile.demo.fixitfastcustomer.activity.ReportsListActivity;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Constants;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;

public class MainFragment extends Fragment {

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button newButton = (Button) rootView.findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewReportActivity.class);
                startActivity(intent);
            }
        });

        Button reportsButton = (Button) rootView.findViewById(R.id.reportsButton);
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportsListActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

}