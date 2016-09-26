package ahmed.news.feed_list;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Mock
    private FeedListContract.View mView;

    @Mock
    private FeedLocalDataSource mFeedLocalDataSource;

    @InjectMocks
    private FeedListPresenter mPresenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    /**
     * replaces the android main thread to enable testing locally
     * mocks the view, and data source and initializes the presenter
     */
    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook()
        {
            @Override
            public Scheduler getMainThreadScheduler()
            {
                return Schedulers.immediate();
            }
        });

        // create the presenter
        mPresenter.registerView(mView);
    }

    /**
     * resets rxJava so it can use Android's main thread again
     */
    @After
    public void tearDown()
    {
        RxAndroidPlugins.getInstance().reset();
        mPresenter.unregisterView();
    }

    /**
     * the feed data source is mocked to return a hard coded rss feed after some delay
     */
    @Test
    public void testGetFeed() throws Exception
    {
        // create dummy feed data
        List<FeedItem> feedList = Arrays.asList(new FeedItem("item1"), new FeedItem("item2"));
        String channelName = "Ahmed's Channel";
        Channel channel = new Channel(channelName, feedList);
        RSSFeed rssFeed = new RSSFeed(channel);
        long requestDelayInMillis = 600;
        when(mFeedLocalDataSource.getFeed()).then(new Answer<RSSFeed>()
        {
            @Override
            public RSSFeed answer(InvocationOnMock invocation) throws Throwable
            {
                Thread.sleep(requestDelayInMillis);
                return rssFeed;
            }
        });

        // ask the presenter to get the feed and check the view
        mPresenter.getFeed();
        verify(mView).showProgress();
        verify(mView, timeout(3000)).showFeedList(feedList);
        verify(mView).hideProgress();
    }

    /**
     * the feed data source is mocked to return null, so presenter should ask the view to launch the sync service
     */
    @Test
    public void testGetFeedFailed() throws Exception
    {
        // create dummy feed data
        when(mFeedLocalDataSource.getFeed()).thenReturn(null);

        // ask the presenter to get the feed and check the view
        mPresenter.getFeed();
        verify(mView).showProgress();
        verify(mView, timeout(300)).launchSyncService();
        verify(mView).hideProgress();
    }

}