package ahmed.news.data;

import org.junit.Before;
import org.junit.Test;

import ahmed.news.entity.Feedtem;
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

    private FeedRemoteDataSource feedRemoteDataSource;

    @Before
    public void setup()
    {
        feedRemoteDataSource = new FeedRemoteDataSource();
    }

    @Test
    public void testGetFeed() throws Exception
    {
        RSSFeed feed = feedRemoteDataSource.getFeed();
        assertNotNull(feed);
        assertNotNull(feed.getChannel());
        assertNotNull(feed.getChannel().getFeedtemList());
        assertTrue(feed.getChannel().getFeedtemList().size() > 0);
        for (Feedtem feedtem : feed.getChannel().getFeedtemList())
        {
            assertNotNull(feedtem);
            assertNotNull(feedtem.getTitle());
            assertNotNull(feedtem.getDescription());
            assertNotNull(feedtem.getLink());
            assertNotNull(feedtem.getPubDate());
        }
    }
}