package ahmed.news.data;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.security.AccessController.getContext;

/**
 * date parsing utils
 * Created by ahmed on 9/29/2016.
 */
public class DateUtils
{
    /**
     * parses a string formatted like this for example
     * Tue, 27 Sep 2016 12:30:10 GMT
     */
    public static Calendar parseDate(String str) throws ParseException
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        simpleDateFormat.parse(str);
        return simpleDateFormat.getCalendar();
    }

    /**
     * returns a date string formatted like "8 minutes ago" or "3 days aho"
     */
    public static String getReadableData(Context mContext, Calendar calendar)
    {
        String dateStr = android.text.format.DateUtils.getRelativeDateTimeString(mContext,
                calendar.getTimeInMillis(), android.text.format.DateUtils.MINUTE_IN_MILLIS,
                android.text.format.DateUtils.WEEK_IN_MILLIS, android.text.format.DateUtils.FORMAT_SHOW_TIME)
                .toString();
        if (dateStr.contains(","))
            dateStr = dateStr.substring(0, dateStr.indexOf(","));
        return dateStr;
    }
}
