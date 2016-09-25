package ahmed.news.data;

import org.junit.Before;
import org.junit.Test;

import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * tests that feed remote data source can downlod and parse the data
 * (doesn't test retrofit itself but just tests that the xml is parsed without errors)
 * Created by ahmed on 9/20/2016.
 */
public class FeedRemoteDataSourceTest
{

    private FeedRemoteDataSourceImp feedRemoteDataSource;

    @Before
    public void setup()
    {
        feedRemoteDataSource = new FeedRemoteDataSourceImp();
    }

    @Test
    public void testGetFeed() throws Exception
    {
        RSSFeed feed = feedRemoteDataSource.getFeed();
        assertNotNull(feed);
        assertNotNull(feed.getChannel());
        assertNotNull(feed.getChannel().getFeedItemList());
        assertTrue(feed.getChannel().getFeedItemList().size() > 0);
        for (FeedItem feedItem : feed.getChannel().getFeedItemList())
        {
            assertNotNull(feedItem);
            assertNotNull(feedItem.getTitle());
            assertNotNull(feedItem.getDescription());
            assertNotNull(feedItem.getLink());
            assertNotNull(feedItem.getPubDate());
            System.out.println(feedItem.getPubDate());
        }
    }
}