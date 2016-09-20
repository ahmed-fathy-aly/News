package ahmed.news.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * A channel has a list of feed items
 * Created by ahmed on 9/20/2016.
 */
@Root(strict = false, name = "channel")
public class Channel
{
    @Element(name = "title")
    private String title;

    @Element(name = "description")
    private String description;

    @ElementList(entry = "item", inline = true)
    List<Feedtem> feedtemList;

    public List<Feedtem> getFeedtemList()
    {
        return feedtemList;
    }

    public void setFeedtemList(List<Feedtem> feedtemList)
    {
        this.feedtemList = feedtemList;
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


}
