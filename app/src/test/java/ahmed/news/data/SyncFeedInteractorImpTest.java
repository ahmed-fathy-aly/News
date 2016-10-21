package ahmed.news.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ahmed.news.Constants;
import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static net.bytebuddy.description.annotation.AnnotationDescription.AbstractBase.ForPrepared.ERROR_MESSAGE;
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
     * when the total items after the update is larger than a specific constant, delete the old items
     */
    @Test
    public void testRemoveOldItems() throws Exception
    {
        // mock the remote data source to return a large list of items
        List<FeedItem> TO_BE_KEPT_ITEMS = new ArrayList<>();
        for (int i = 0; i < Constants.MAX_KEPT_ITEMS; i++)
            TO_BE_KEPT_ITEMS.add(new FeedItem("kept " + i));
        List<FeedItem> TO_BE_REMOVED_ITEMS = new ArrayList<>();
        List<String> TO_BE_REMOVED_TITLES = new ArrayList<>();
        for (int i = 0; i < 43; i++)
        {
            TO_BE_REMOVED_TITLES.add("removed " + i);
            TO_BE_REMOVED_ITEMS.add(new FeedItem(TO_BE_REMOVED_TITLES.get(i)));
        }
        List<FeedItem> MERGED_ITEMS = new ArrayList<>();
        MERGED_ITEMS.addAll(TO_BE_KEPT_ITEMS);
        MERGED_ITEMS.addAll(TO_BE_REMOVED_ITEMS);
        RSSFeed MERGED_FEED = new RSSFeed(new Channel("", MERGED_ITEMS));
        FeedLocalDataSource feedLocalDataSource = Mockito.mock(FeedLocalDataSource.class);
        Mockito.when(feedLocalDataSource.getFeed()).thenReturn(MERGED_FEED);

        // mock the remote data source to do nothing
        FeedRemoteDataSource feedRemoteDataSource = Mockito.mock(FeedRemoteDataSource.class);

        // setup the interactor
        SyncFeedInteractor syncFeedInteractor = new SyncFeedInteractorImp(feedRemoteDataSource, feedLocalDataSource);

        // ask to sync
        SyncFeedInteractor.SyncCallback callback = Mockito.mock(SyncFeedInteractor.SyncCallback.class);
        syncFeedInteractor.sync(callback);

        // verify the local data source is asked to delete something
        Mockito.verify(feedLocalDataSource, Mockito.timeout(100)).removeItems(TO_BE_REMOVED_TITLES);
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
        FeedRemoteDataSource feedRemoteDataSource = Mockito.mock(FeedRemoteDataSource.class);
        Mockito.when(feedRemoteDataSource.getFeed()).thenThrow(new IOException());

        // setup the interactor
        SyncFeedInteractor syncFeedInteractor = new SyncFeedInteractorImp(feedRemoteDataSource, Mockito.mock(FeedLocalDataSource.class));

        // ask to sync
        SyncFeedInteractor.SyncCallback callback = Mockito.mock(SyncFeedInteractor.SyncCallback.class);
        syncFeedInteractor.sync(callback);

        // verify
        Mockito.verify(callback, Mockito.timeout(100)).errorDownloadingFeed();
    }




}