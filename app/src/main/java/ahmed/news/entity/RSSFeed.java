package ahmed.news.entity;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Just wraps a channel(the RSS feed is from one channel)
 * Created by ahmed on 9/20/2016.
 */
@Root(strict=false, name = "rss")
public class RSSFeed
{
    @Element(name = "channel")
    private Channel channel;


    public Channel getChannel ()
    {
        return channel;
    }

    public void setChannel (Channel channel)
    {
        this.channel = channel;
    }

}
