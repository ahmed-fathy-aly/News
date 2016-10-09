package ahmed.news.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * Created by ahmed on 10/9/2016.
 */
public class SyncFeedInteractorImpTest
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
     * when the interactor is asked to synced,
     * it should get the new feed from the remote source
     * then store it in the local source
     * and read it back from the local source and return it
     */
    @Test
    public void testSync() throws IOException
    {
        // mock the remote data source to return a fixed feed
        List<FeedItem> REMOTE_FEED_LIST = Arrays.asList(new FeedItem("item1"), new FeedItem("item2"));
        String REMOTE_CHANNEL_TITLE = "remote channel";
        RSSFeed REMOTE_RSS_FEED = new RSSFeed(new Channel(REMOTE_CHANNEL_TITLE, REMOTE_FEED_LIST));
        FeedRemoteDataSource feedRemoteDataSource = Mockito.mock(FeedRemoteDataSource.class);
        Mockito.when(feedRemoteDataSource.getFeed()).thenReturn(REMOTE_RSS_FEED);

        // mock the local data source to always return the remote feed(we'll check if it's asked to store it too)
        FeedLocalDataSource feedLocalDataSource = Mockito.mock(FeedLocalDataSource.class);
        Mockito.when(feedLocalDataSource.getFeed()).thenReturn(REMOTE_RSS_FEED);

        // setup the interactor
        SyncFeedInteractor syncFeedInteractor = new SyncFeedInteractorImp(feedRemoteDataSource, feedLocalDataSource);

        // ask to sync
        SyncFeedInteractor.SyncCallback callback = Mockito.mock(SyncFeedInteractor.SyncCallback.class);
        syncFeedInteractor.sync(callback);

        // verify
        Mockito.verify(feedLocalDataSource, Mockito.timeout(100)).storeFeed(REMOTE_RSS_FEED);
        Mockito.verify(callback, Mockito.timeout(100)).syncDone(REMOTE_CHANNEL_TITLE, REMOTE_FEED_LIST);
    }

    /**
     * when the interactor is asked to sync
     * but the remote data source fails(throw an exception)
     * the callback should be invoked with an error
     */
    @Test
    public void testSyncFailed() throws IOException
    {
        // mock the remote data source to return a fixed feed
        String ERROR_MESSAGE = "failed to bla bla bla";
        FeedRemoteDataSource feedRemoteDataSource = Mockito.mock(FeedRemoteDataSource.class);
        Mockito.when(feedRemoteDataSource.getFeed()).thenThrow(new IOException(ERROR_MESSAGE));

        // setup the interactor
        SyncFeedInteractor syncFeedInteractor = new SyncFeedInteractorImp(feedRemoteDataSource, null);

        // ask to sync
        SyncFeedInteractor.SyncCallback callback = Mockito.mock(SyncFeedInteractor.SyncCallback.class);
        syncFeedInteractor.sync(callback);

        // verify
        Mockito.verify(callback, Mockito.timeout(100)).error(ERROR_MESSAGE);
    }

}