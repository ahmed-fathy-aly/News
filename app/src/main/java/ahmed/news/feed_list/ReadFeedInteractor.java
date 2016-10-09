package ahmed.news.feed_list;

import java.util.List;

import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;

/**
 * uses a data source to fetch and read the feed
 * Created by ahmed on 10/6/2016.
 */

public interface ReadFeedInteractor
{
    /**
     * fetches the feed from the data source and mark it as read
     * operates asynchronously
     */
    void readFeed(ReadFeedCallback callback);

    interface ReadFeedCallback
    {
        void foundFeed(List<FeedItem> feedItems, String channelTitle);
        void emptyFeed();
        void fail(String error);
    }


}
