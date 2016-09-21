package ahmed.news.feed_list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.entity.Channel;
import ahmed.news.entity.Feedtem;
import ahmed.news.entity.RSSFeed;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * mocks the view and data(Feed remote data source) layers to test the feed list presenter
 * Created by ahmed on 9/21/2016.
 */
@RunWith(JUnit4.class)
public class FeedListPresenterTest
{
    private FeedListPresenter mPresenter;

    @Mock
    private FeedListContract.View mView;

    @Mock
    private FeedRemoteDataSource mFeedRemoteDataSource;

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
        mPresenter = new FeedListPresenter();
        mPresenter.registerView(mView);
        mPresenter.setFeedRemoteDataSource(mFeedRemoteDataSource);
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
     * the feed data source is mocked to return a hard coded rss feed
     */
    @Test
    public void testGetFeed() throws Exception
    {
        // create dummy feed data
        List<Feedtem> feedList = Arrays.asList(new Feedtem("item1"), new Feedtem("item2"));
        String channelName = "Ahmed's Channel";
        Channel channel = new Channel(channelName, feedList);
        RSSFeed rssFeed = new RSSFeed(channel);
        when(mFeedRemoteDataSource.getFeed()).thenReturn(rssFeed);

        // ask the presenter to get the feed and check the view
        mPresenter.getFeed();
        verify(mView).showProgress();
        verify(mView).showFeedList(feedList);
        verify(mView).hideProgress();
    }

    /**
     * the feed data source is mocked to throw an IOException
     */
    @Test
    public void testGetFeedFailed() throws Exception
    {
        // create dummy feed data
        String errorMessage = "You trusted me, and I failed you";
        when(mFeedRemoteDataSource.getFeed()).thenThrow(new IOException(errorMessage));


        // ask the presenter to get the feed and check the view
        mPresenter.getFeed();
        verify(mView).showProgress();
        // add a delay or else the test would fail if they ran one after the other
        Thread.sleep(100);
        verify(mView).showError(errorMessage);
        verify(mView).hideProgress();
    }

}