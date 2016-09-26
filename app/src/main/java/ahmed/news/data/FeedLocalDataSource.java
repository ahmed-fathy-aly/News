package ahmed.news.data;

import java.io.IOException;

import ahmed.news.entity.RSSFeed;

/**
 * stores the feed locally
 * Created by ahmed on 9/25/2016.
 */
public interface FeedLocalDataSource
{
    /**
     * Synchronous
     * @return the Feed stored locallly
     */
    RSSFeed getFeed();

    /**
     * replaces the feed's info and adds the feed items(or replaces if they are already there)
     */
    void storeFeed(RSSFeed newFeed);


}
