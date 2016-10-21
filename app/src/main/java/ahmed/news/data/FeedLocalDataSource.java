package ahmed.news.data;

import java.io.IOException;
import java.util.List;

import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;

/**
 * stores the feed locally
 * Created by ahmed on 9/25/2016.
 */
public interface FeedLocalDataSource
{
    /**
     * synchronous
     * @return the Feed stored locally or null if non exist
     */
    RSSFeed getFeed();

    /**
     * synchronous
     * replaces the feed's info and adds the feed items(or ignore them if they(same title) are already there)
     */
    void storeFeed(RSSFeed newFeed);

    /**
     * synchronous
     * updates the feed items so when they are queried next time they'll be marked as read
     * If the feedItems are not already there, does nothing
     * */
    void markAsRead(List<FeedItem> feedItems);

    /**
     * synchronous
     * update that feeditem so when it's queried next time, it'll be marked as read
     * if the feed item is not already there, does nothing
     */
    void markAsRead(String feedTitle);

    /**
     * synchronous
     * deletes the items with these titles, does nothing if for the titles that are not there
     */
    void removeItems(List<String> titlesToBeRemoved);
}
