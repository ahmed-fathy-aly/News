package ahmed.news.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

}
