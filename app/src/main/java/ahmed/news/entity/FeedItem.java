package ahmed.news.entity;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * A feed item represents one story
 * Created by ahmed on 9/20/2016.
 */
@Root(strict = false, name = "item")
public class FeedItem
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


}
