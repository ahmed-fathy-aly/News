package ahmed.news.entity;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.data.DateUtils;
import timber.log.Timber;

/**
 * A feed item represents one story
 * Created by ahmed on 9/20/2016.
 */
@Root(strict = false, name = "item")
public class FeedItem implements Serializable
{
    @Element(name = "pubDate")
    private String pubDate;

    @Element(name = "title")
    private String title;

    @Element(name = "description")
    private String description;

    @Element(name = "link")
    private String link;


    @Element(name = "thumbnail", required = false)
    @Namespace(prefix = "media")
    private Image image;

    private Calendar mCalendar;


    public FeedItem()
    {
    }

    public FeedItem(String title)
    {
        this.title = title;
    }

    public FeedItem(String title, String pubData)
    {
        this.title = title;
        this.pubDate = pubData;
    }

    public FeedItem(String pubDate, String title, String description, String link, Image image)
    {
        this.pubDate = pubDate;
        this.title = title;
        this.description = description;
        this.link = link;
        this.image = image;
    }

    @Nullable
    public Image getImage()
    {
        return image;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public String getPubDate()
    {
        return pubDate;
    }

    public void setPubDate(String pubDate)
    {
        this.pubDate = pubDate;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }


    /**
     * @return the item's date or null if the publication's date format is wrong
     */
    public Calendar getCalendar()
    {
        if (mCalendar == null)
            try
            {
                mCalendar = DateUtils.parseDate(pubDate);
            } catch (ParseException e)
            {
                Timber.d("error in parsing date %s", e.getMessage());
            }
        return mCalendar;
    }

}
