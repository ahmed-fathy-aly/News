package ahmed.news.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by ahmed on 9/29/2016.
 */
public class DateUtilsTest
{

    @Test
    public void testParseDate() throws Exception
    {
        String dateStr = "Tue, 27 Sep 2016 12:30:10 GMT";
        Calendar result = DateUtils.parseDate(dateStr);

        Assert.assertEquals(27, result.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(8, result.get(Calendar.MONTH));
        Assert.assertEquals(2016, result.get(Calendar.YEAR));
        Assert.assertEquals(12, result.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(30, result.get(Calendar.MINUTE));
        Assert.assertEquals(10, result.get(Calendar.SECOND));

    }
}