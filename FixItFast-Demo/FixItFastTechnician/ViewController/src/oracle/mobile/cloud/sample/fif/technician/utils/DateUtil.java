package oracle.mobile.cloud.sample.fif.technician.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author supareek
 * @coyright Oracle Corporation, 2015
 */
public class DateUtil {

    public static String getISOTimeStamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static Date parseToDate(String info){
        if (info == null)
            return null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = format.parse(info);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

