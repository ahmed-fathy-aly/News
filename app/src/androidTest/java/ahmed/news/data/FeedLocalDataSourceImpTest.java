package ahmed.news.data;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;
import ahmed.news.entity.RSSFeed;

import static org.junit.Assert.*;

/**
 * tests inserting, updating and reading feed channels to the local database
 * Created by ahmed on 9/26/2016.
 */
public class FeedLocalDataSourceImpTest
{

    /* constant data */

    final FeedItem FEED_ITEM1 = new FeedItem("date1", "title1", "desc1", "link1", new Image(1, 2, "img1"));
    final FeedItem FEED_ITEM2 = new FeedItem("date2", "title2", "desc2", "link2", new Image(2, 4, "img2"));
    final FeedItem FEED_ITEM3 = new FeedItem("date3", "title3", "desc3", "link3", new Image(3, 6, "img3"));
    final FeedItem FEED_ITEM4 = new FeedItem("date4", "title4", "desc4", "link4", new Image(3, 6, "img4"));

    final String CHANNEL_TITLE = "channel title";
    final String CHANNEL_DESCRIPTION = "channel description";

    private FeedLocalDataSourceImp mFeedLocalDataSourceImp;

    /**
     * clears the contents of the database to make assertion easier in tests
     */
    @Before
    public void clearDatabase()
    {
        mFeedLocalDataSourceImp
                = new FeedLocalDataSourceImp(InstrumentationRegistry.getTargetContext());
        mFeedLocalDataSourceImp.clearTables();
    }

    @After
    public void cleanUp()
    {
        mFeedLocalDataSourceImp.clearTables();
    }

    /**
     * inserts a feed and reads it back
     */
    @Test
    public void testInsertAndRead()
    {
        // store
        List<FeedItem> feedItems = Arrays.asList(FEED_ITEM1, FEED_ITEM2, FEED_ITEM3);
        Channel channel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel));

        // load and assert it's the same as stored
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        assertNotNull(rssFeed);
        assertNotNull(rssFeed.getChannel());
        assertEquals(CHANNEL_TITLE, rssFeed.getChannel().getTitle());
        assertEquals(CHANNEL_DESCRIPTION, rssFeed.getChannel().getDescription());
        assertEquals(3, rssFeed.getChannel().getFeedItemList().size());
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));
    }

    /**
     * inserts inserting twice, the data in the second time is new
     * checks both the data inserted the first time and the second time are there
     */
    @Test
    public void testUpdateWithNewItems()
    {
        mFeedLocalDataSourceImp.getFeed();

        // store
        List<FeedItem> feedItemsOriginal = Arrays.asList(FEED_ITEM1);
        Channel channel1 = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItemsOriginal);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel1));
        List<FeedItem> feedItemsNew = Arrays.asList(FEED_ITEM2, FEED_ITEM3);
        Channel channel2 = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItemsNew);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel2));


        // load and assert it's the same as stored
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        assertNotNull(rssFeed);
        assertNotNull(rssFeed.getChannel());
        assertEquals(CHANNEL_TITLE, rssFeed.getChannel().getTitle());
        assertEquals(CHANNEL_DESCRIPTION, rssFeed.getChannel().getDescription());
        assertEquals(3, rssFeed.getChannel().getFeedItemList().size());
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));
    }

    /**
     * inserts inserting twice, the data in the second time has both duplicated items and new items
     * checks both the data inserted the first time and the second time are there with no duplicates
     */
    @Test
    public void testUpdateWithDuplicatedItems()
    {
        mFeedLocalDataSourceImp.getFeed();

        // store
        List<FeedItem> feedItemsOriginal = Arrays.asList(FEED_ITEM1, FEED_ITEM2);
        Channel channel1 = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItemsOriginal);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel1));
        List<FeedItem> feedItemsNew = Arrays.asList(FEED_ITEM2, FEED_ITEM3, FEED_ITEM4);
        Channel channel2 = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItemsNew);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel2));


        // load and assert it's the same as stored
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        assertNotNull(rssFeed);
        assertNotNull(rssFeed.getChannel());
        assertEquals(CHANNEL_TITLE, rssFeed.getChannel().getTitle());
        assertEquals(CHANNEL_DESCRIPTION, rssFeed.getChannel().getDescription());
        assertEquals(4, rssFeed.getChannel().getFeedItemList().size());
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));
        assertSameFeedItem(FEED_ITEM4, rssFeed.getChannel().getFeedItemList().get(3));

    }

    /**
     * adds some items
     * marks them as read
     * read the items and make sure they are marked as read
     */
    @Test
    public void testMarkManyAsRead()
    {
        // store
        List<FeedItem> feedItems = Arrays.asList(FEED_ITEM1, FEED_ITEM2, FEED_ITEM3);
        Channel channel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel));

        // mark as read
        mFeedLocalDataSourceImp.markAsRead(feedItems);

        // read and assert they are marked as read
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        for (FeedItem feedItem : feedItems)
            feedItem.setRead(true);
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));

    }

    /**
     * adds some items
     * marks one of them as read
     * read the items and make sure they are marked as read
     */
    @Test
    public void testMarkOneAsRead()
    {
        // store
        List<FeedItem> feedItems = Arrays.asList(FEED_ITEM1, FEED_ITEM2, FEED_ITEM3);
        Channel channel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel));

        // mark as read
        mFeedLocalDataSourceImp.markAsRead(FEED_ITEM1.getTitle());

        // read and assert it ise marked as read
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        FEED_ITEM1.setRead(true);
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));
    }

    /**
     * stores some items
     * mark them as read
     * store them again along with other un read items
     * make sure those who were marked as read are still marked as read
     */
    @Test
    public void testMarkManyAsReadThenUpdateDuplicate()
    {
        // store
        List<FeedItem> feedItems = Arrays.asList(FEED_ITEM1, FEED_ITEM2, FEED_ITEM3);
        Channel channel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel));

        // mark as read
        mFeedLocalDataSourceImp.markAsRead(feedItems);

        // store again

        List<FeedItem> newFeedItems = Arrays.asList(FEED_ITEM2, FEED_ITEM3, FEED_ITEM4);
        Channel newChannel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, newFeedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(newChannel));


        // read and assert they are marked as read
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        for (FeedItem feedItem : feedItems)
            feedItem.setRead(true);
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));
        assertSameFeedItem(FEED_ITEM4, rssFeed.getChannel().getFeedItemList().get(3));

    }


    /**
     * stores some items
     * mark one of them as read
     * store them again along with other un read items
     * make sure those who were marked as read are still marked as read
     */
    @Test
    public void testMarkOneAsReadThenUpdateDuplicate()
    {
        // store
        List<FeedItem> feedItems = Arrays.asList(FEED_ITEM1, FEED_ITEM2, FEED_ITEM3);
        Channel channel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel));

        // mark as read
        mFeedLocalDataSourceImp.markAsRead(FEED_ITEM2.getTitle());

        // store again
        List<FeedItem> newFeedItems = Arrays.asList(FEED_ITEM2, FEED_ITEM3, FEED_ITEM4);
        Channel newChannel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, newFeedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(newChannel));


        // read and assert they are marked as read
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        FEED_ITEM2.setRead(true);
        assertSameFeedItem(FEED_ITEM1, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM2, rssFeed.getChannel().getFeedItemList().get(1));
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(2));
        assertSameFeedItem(FEED_ITEM4, rssFeed.getChannel().getFeedItemList().get(3));

    }

    @Test
    public void testDeleteFeedItems()
    {
        // store
        List<FeedItem> feedItems = Arrays.asList(FEED_ITEM1, FEED_ITEM2, FEED_ITEM3, FEED_ITEM4);
        Channel channel = new Channel(CHANNEL_TITLE, CHANNEL_DESCRIPTION, feedItems);
        mFeedLocalDataSourceImp.storeFeed(new RSSFeed(channel));

        // remove
        List<String> titlesToBeRemoved = Arrays.asList(FEED_ITEM1.getTitle(), FEED_ITEM2.getTitle());
        mFeedLocalDataSourceImp.removeItems(titlesToBeRemoved);

        // read again
        RSSFeed rssFeed = mFeedLocalDataSourceImp.getFeed();
        assertEquals(2, rssFeed.getChannel().getFeedItemList().size());
        assertSameFeedItem(FEED_ITEM3, rssFeed.getChannel().getFeedItemList().get(0));
        assertSameFeedItem(FEED_ITEM4, rssFeed.getChannel().getFeedItemList().get(1));
    }

    private void assertSameFeedItem(FeedItem expected, FeedItem found)
    {
        assertEquals(expected.getTitle(), found.getTitle());
        assertEquals(expected.getDescription(), found.getDescription());
        assertEquals(expected.getLink(), found.getLink());
        assertEquals(expected.getPubDate(), found.getPubDate());
        assertEquals(expected.getImage().getUrl(), found.getImage().getUrl());
        assertEquals(expected.getImage().getWidth(), found.getImage().getWidth());
        assertEquals(expected.getImage().getHeight(), found.getImage().getHeight());
        assertEquals(expected.isRead(), found.isRead());
    }
}
