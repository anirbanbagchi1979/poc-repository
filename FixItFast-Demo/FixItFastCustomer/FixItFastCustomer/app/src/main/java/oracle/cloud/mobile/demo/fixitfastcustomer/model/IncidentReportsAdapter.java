package oracle.cloud.mobile.demo.fixitfastcustomer.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.util.Util;

public class IncidentReportsAdapter extends ArrayAdapter<IncidentReport> {

    public IncidentReportsAdapter(Context context, int resource, List<IncidentReport> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IncidentReport ir = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_report, parent, false);
        }

        TextView id = (TextView) convertView.findViewById(R.id.idLabel);
        id.setText(String.valueOf(ir.id));

        TextView title = (TextView) convertView.findViewById(R.id.titleLabel);
        title.setText(ir.title);

        return convertView;
    }

}
