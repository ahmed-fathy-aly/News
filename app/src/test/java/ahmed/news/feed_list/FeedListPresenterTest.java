package ahmed.news.feed_list;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.data.FeedRemoteDataSourceImp;
import ahmed.news.data.SyncFeedInteractor;
import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.*;

/**
 * mocks the view and data(Feed remote data source) layers to test the feed list presenter
 * Created by ahmed on 9/21/2016.
 */
@RunWith(JUnit4.class)
public class FeedListPresenterTest
{
    /**
     * when the read feed interactor returns a feed, the presenter will show it to the view
     */
    @Test
    public void testGetFeed() throws Exception
    {
        // mock the view and interactor
        List<FeedItem> FEED_LIST = Arrays.asList(new FeedItem("item1"), new FeedItem("item2"));
        String CHANNEL_NAME = "Ahmed's Channel";
        ReadFeedInteractor readFeedInteractor = Mockito.mock(ReadFeedInteractor.class);
        doAnswer((i) ->
                {
                    try
                    {
                        Thread.sleep(400);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ReadFeedInteractor.ReadFeedCallback callback = i.getArgument(0);
                    callback.foundFeed(FEED_LIST, CHANNEL_NAME);
                    return null;
                }
        ).when(readFeedInteractor).readFeed(any());
        FeedListContract.View view = Mockito.mock(FeedListContract.View.class);

        // setup presenter
        FeedListPresenter presenter = new FeedListPresenter(readFeedInteractor, null);
        presenter.registerView(view);

        // ask the presenter to get the feed and check the view
        presenter.getFeed();
        verify(view).showProgress();
        verify(view, timeout(1000)).showFeedList(FEED_LIST);
        verify(view, timeout(100)).showTitle(CHANNEL_NAME);
        verify(view).hideProgress();
    }

    /**
     * when the feed source returns an empty feed,
     * the presenter should ask the sync interactor to sync
     * when the sync interactors finish syncing,
     * the presenter should present its syncing results
     */
    @Test
    public void testGetEmptyFeed()
    {
        // mock the read and sync feed interactors and view
        List<FeedItem> FEED_LIST = Arrays.asList(new FeedItem("item1"), new FeedItem("item2"));
        String CHANNEL_NAME = "Ahmed's Channel";
        ReadFeedInteractor readFeedInteractor = Mockito.mock(ReadFeedInteractor.class);
        doAnswer((i) ->
                {
                    ReadFeedInteractor.ReadFeedCallback callback = i.getArgument(0);
                    callback.emptyFeed();
                    return null;
                }
        ).when(readFeedInteractor).readFeed(any());
        SyncFeedInteractor syncFeedInteractor =
                new SyncFeedInteractor()
                {
                    @Override
                    public void sync(SyncCallback syncCallback)
                    {
                        // simulate a delay
                        try
                        {
                            Thread.sleep(400);
                        } catch (InterruptedException e)
                        {
                        }
                        syncCallback.syncDone(CHANNEL_NAME, FEED_LIST);
                    }

                    @Override
                    public SyncResult syncSynchronous()
                    {
                        return null;
                    }
                };
        FeedListContract.View view = mock(FeedListContract.View.class);

        // setup presenter
        FeedListPresenter presenter = new FeedListPresenter(readFeedInteractor, syncFeedInteractor);
        presenter.registerView(view);

        // ask the presenter to get the feed and check the view
        presenter.getFeed();
        verify(view, timeout(100)).showProgress();
        verify(view, timeout(2000)).showTitle(CHANNEL_NAME);
        verify(view, timeout(100)).showFeedList(FEED_LIST);
        verify(view, timeout(100)).hideProgress();
    }

    /**
     * when the presenter is asked to sync,
     * it will present the sync interactor's result
     */
    @Test
    public void testSync()
    {
        // mock the read and sync feed interactors and view
        List<FeedItem> FEED_LIST = Arrays.asList(new FeedItem("item1"), new FeedItem("item2"));
        String CHANNEL_NAME = "Ahmed's Channel";
        SyncFeedInteractor syncFeedInteractor =
                new SyncFeedInteractor()
                {
                    @Override
                    public void sync(SyncCallback syncCallback)
                    {
                        // simulate a delay
                        try
                        {
                            Thread.sleep(400);
                        } catch (InterruptedException e)
                        {
                        }
                        syncCallback.syncDone(CHANNEL_NAME, FEED_LIST);
                    }

                    @Override
                    public SyncResult syncSynchronous()
                    {
                        return null;
                    }
                };
        FeedListContract.View view = mock(FeedListContract.View.class);

        // setup presenter
        FeedListPresenter presenter = new FeedListPresenter(null, syncFeedInteractor);
        presenter.registerView(view);

        // ask the presenter to get the feed and check the view
        presenter.syncFeed();
        verify(view, timeout(2000)).showTitle(CHANNEL_NAME);
        verify(view, timeout(100)).showFeedList(FEED_LIST);
        verify(view, timeout(100)).hideProgress();
    }

    /**
     * when the presenter is asked to sync and the sync interactors fails,
     * the view should be presented with the error message
     */
    @Test
    public void testSyncFailed()
    {
        // mock the sync feed interactors and view
        String ERROR_MESSAGE = "sync failed";
        SyncFeedInteractor syncFeedInteractor =
                new SyncFeedInteractor()
                {
                    @Override
                    public void sync(SyncCallback syncCallback)
                    {
                        // simulate a delay
                        try
                        {
                            Thread.sleep(400);
                        } catch (InterruptedException e)
                        {
                        }
                        syncCallback.error(ERROR_MESSAGE);
                    }

                    @Override
                    public SyncResult syncSynchronous()
                    {
                        return null;
                    }
                };
        FeedListContract.View view = mock(FeedListContract.View.class);

        // setup presenter
        FeedListPresenter presenter = new FeedListPresenter(null, syncFeedInteractor);
        presenter.registerView(view);

        // ask the presenter to get the feed and check the view
        presenter.syncFeed();
        verify(view, timeout(1000)).showError(ERROR_MESSAGE);
        verify(view, timeout(100)).hideProgress();
    }

    /**
     * when a feed is clicked,
     * the presenter will mark it as read
     */
    @Test
    public void testFeedClicked()
    {
        // mock the view and interactor
        String FEED_TITLE = "feeda";
        FeedListContract.View view = Mockito.mock(FeedListContract.View.class);
        ReadFeedInteractor readFeedInteractor = Mockito.mock(ReadFeedInteractor.class);
        doAnswer((i) ->
                {
                        ReadFeedInteractor.MarkAsReadCallback callback = i.getArgument(1);
                        callback.marked();
                        return null;
                }
        ).when(readFeedInteractor).markAsRead(eq(FEED_TITLE), any());

        // setup the presenter
        FeedListPresenter presenter = new FeedListPresenter(readFeedInteractor, null);
        presenter.registerView(view);

        // click on an item
        FeedItem feedItem = new FeedItem(FEED_TITLE);
        presenter.onFeedClicked(feedItem);

        // check the interactor is asked to mark it as read and the view is asked to show its details
        Mockito.verify(readFeedInteractor, timeout(100)).markAsRead(Mockito.eq(FEED_TITLE), any());
        Mockito.verify(view, timeout(100)).markAsRead(feedItem);
    }
}