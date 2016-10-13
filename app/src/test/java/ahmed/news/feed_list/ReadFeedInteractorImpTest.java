package ahmed.news.feed_list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by ahmed on 10/7/2016.
 */
public class ReadFeedInteractorImpTest
{

    /**
     * replaces the android main thread to enable testing locally
     * mocks the view, and data source and initializes the presenter
     */
    @Before
    public void setUp() throws Exception
    {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook()
        {
            @Override
            public Scheduler getMainThreadScheduler()
            {
                return Schedulers.immediate();
            }
        });
    }

    /**
     * resets rxJava so it can use Android's main thread again
     */
    @After
    public void tearDown()
    {
        RxAndroidPlugins.getInstance().reset();
    }

    /**
     * when the data source provides a non-empty feed, the interactor should read it and return it
     */
    @Test
    public void testReadFeed() throws InterruptedException
    {
        // mock the data source to return the mock data
        FeedLocalDataSource feedLocalDataSource = Mockito.mock(FeedLocalDataSource.class);
        List<FeedItem> FEED_LIST = Arrays.asList(new FeedItem("item1"), new FeedItem("item2"));
        String CHANNEL_TITLE = "my channel";
        RSSFeed RSS_FEED = new RSSFeed(new Channel(CHANNEL_TITLE, FEED_LIST));
        Mockito.when(feedLocalDataSource.getFeed()).thenReturn(RSS_FEED);

        // setup the interactor
        ReadFeedInteractor readFeedInteractor = new ReadFeedInteractorImp(feedLocalDataSource);

        // ask to read the feed
        ReadFeedInteractor.ReadFeedCallback callback = Mockito.mock(ReadFeedInteractor.ReadFeedCallback.class);
        readFeedInteractor.readFeed(callback);


        // check the feed is returned and the data source is asked to read the items
        Mockito.verify(callback, Mockito.timeout(100)).foundFeed(FEED_LIST, CHANNEL_TITLE);
        Mockito.verify(feedLocalDataSource, Mockito.times(0)).markAsRead(FEED_LIST);
    }

    /**
     * when the data source returns null for the feed, the interactor should invoke the isEmpty in the callback
     */
    @Test
    public void readEmptyFeed()
    {
        // mock the data source to return null
        FeedLocalDataSource feedLocalDataSource = Mockito.mock(FeedLocalDataSource.class);
        Mockito.when(feedLocalDataSource.getFeed()).thenReturn(null);

        // setup the interactor
        ReadFeedInteractor readFeedInteractor = new ReadFeedInteractorImp(feedLocalDataSource);

        // ask to read the feed
        ReadFeedInteractor.ReadFeedCallback callback = Mockito.mock(ReadFeedInteractor.ReadFeedCallback.class);
        readFeedInteractor.readFeed(callback);


        // check the feed is returned and the data source is asked to read the items
        Mockito.verify(callback, Mockito.timeout(100)).emptyFeed();
        Mockito.verify(feedLocalDataSource, Mockito.times(0)).markAsRead(Mockito.anyList());
    }

    /**
     * when an item is marked as read, the data source should mark it as read and the success callback is invoked
     */
    @Test
    public void testMarkAsRead()
    {
        String FEED_TITLE = "feeda";
        // mock the data source and callback
        FeedLocalDataSource feedLocalDataSource = Mockito.mock(FeedLocalDataSource.class);
        ReadFeedInteractor.MarkAsReadCallback callback = Mockito.mock(ReadFeedInteractor.MarkAsReadCallback.class);

        // setup the interactor
        ReadFeedInteractor readFeedInteractor = new ReadFeedInteractorImp(feedLocalDataSource);

        // mark as read
        readFeedInteractor.markAsRead(FEED_TITLE, callback);

        // verify the callback is invoked and the data source marks the item
        Mockito.verify(feedLocalDataSource, Mockito.timeout(100)).markAsRead(FEED_TITLE);
        Mockito.verify(callback, Mockito.timeout(100)).marked();
    }
}