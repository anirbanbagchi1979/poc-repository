package oracle.cloud.mobile.demo.fixitfastcustomer.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import oracle.cloud.mobile.analytics.Analytics;
import oracle.cloud.mobile.authorization.Authorization;
import oracle.cloud.mobile.exception.ServiceProxyException;
import oracle.cloud.mobile.mobilebackend.MobileBackendManager;
import oracle.cloud.mobile.notifications.Notifications;
import oracle.cloud.mobile.storage.Storage;

public class Util {

    private static Authorization authorization = null;
    private static Analytics analyticsProxy = null;
    private static Storage storageProxy = null;
    private static Notifications notificationsProxy = null;

    public static Authorization authorization(Activity activity) {
        if (authorization == null) {
            try {
                authorization = MobileBackendManager.getManager().getDefaultMobileBackend(activity).getAuthorization();
            } catch (ServiceProxyException e) {
                Log.e(Constants.LOG_TAG, "Error obtaining Authorization.", e);
            }
        }
        return authorization;
    }

    public static Analytics analytics(Activity activity) {
        if (analyticsProxy == null) {
            try {
                analyticsProxy = MobileBackendManager.getManager().getDefaultMobileBackend(activity).getServiceProxy(Analytics.class);
            } catch (ServiceProxyException e) {
                Log.e(Constants.LOG_TAG, "Error obtaining Analytics proxy.", e);
            }
        }
        return analyticsProxy;
    }

    public static Storage storage(Activity activity) {
        if (storageProxy == null) {
            try {
                storageProxy = MobileBackendManager.getManager().getDefaultMobileBackend(activity).getServiceProxy(Storage.class);
            } catch (ServiceProxyException e) {
                Log.e(Constants.LOG_TAG, "Error obtaining Storage proxy.", e);
            }
        }
        return storageProxy;
    }

    public static Notifications notifications(Activity activity) {
        if (notificationsProxy == null) {
            try {
                notificationsProxy = MobileBackendManager.getManager().getDefaultMobileBackend(activity).getServiceProxy(Notifications.class);
            } catch (ServiceProxyException e) {
                Log.e(Constants.LOG_TAG, "Error obtaining Notifications proxy.", e);
            }
        }
        return notificationsProxy;
    }

    public static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), Constants.TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), Constants.TIMEOUT);
        return httpClient;
    }

    public static String getCurrentTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(tz);
        String dateString = df.format(new Date());
        return dateString.replace("+0000", "Z");
    }

    public static int randomId() {
        Random r = new Random();
        int low = 1;
        int high = 1000000;
        return r.nextInt(high - low) + low;
    }


    public static void toastAndLog(final Activity a, final String message) {
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(a, message, Toast.LENGTH_SHORT).show();
                }
            });
            Log.d(Constants.LOG_TAG, message);
        }
    }

    public static void toastAndLog(final Activity a, final String message, final Throwable e) {
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(a, message, Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(Constants.LOG_TAG, message, e);
        }
    }

    public static String getLoggedInUser(final Activity a) {
        SharedPreferences prefs = a.getSharedPreferences(Constants.LOG_TAG, Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");
        return username;
    }

}