package oracle.cloud.mobile.demo.fixitfastcustomer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import oracle.cloud.mobile.demo.fixitfastcustomer.R;
import oracle.cloud.mobile.demo.fixitfastcustomer.fragment.ReportsListFragment;

public class ReportsListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_reports);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ReportsListFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
