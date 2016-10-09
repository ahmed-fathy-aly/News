package ahmed.news.feed_list;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ahmed.news.App;
import ahmed.news.AppModule;
import ahmed.news.DaggerAppComponent;
import ahmed.news.R;
import ahmed.news.data.SyncFeedInteractor;
import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.entity.FeedItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * mocks the feed list presenter to test the feed list view
 * Created by ahmed on 9/22/2016.
 */
public class FeedListActivityTest
{
    /* constant mock data */
    List<FeedItem> FEED_LIST = Arrays.asList(
            new FeedItem("title1", "Sat, 24 Sep 2016 00:33:31 GMT"),
            new FeedItem("title2", "Fri, 23 Sep 2016 21:10:15 GMT")
            , new FeedItem("title3", "Fri, 23 Sep 2016 21:41:30 GMT")
            , new FeedItem("title4", "Fri, 23 Sep 2016 19:15:43 GMT")
    );


    @Rule
    public ActivityTestRule<FeedListActivity> activityRule =
            new ActivityTestRule<>(FeedListActivity.class, true, false);

    /**
     * lets the application use the test component so it can inject the mocked presenter
     */
    @Before
    public void setup()
    {
        App app = (App) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
        app.setComponent(
                DaggerAppComponent
                        .builder()
                        .appModule(new TestModule(InstrumentationRegistry.getTargetContext()))
                        .build());
    }

    /**
     * provides a mock presenter
     */
    class TestModule extends AppModule
    {
        public TestModule(Context context)
        {
            super(context);
        }

        @Override
        public FeedListContract.Presenter provideFeedListPresenter(ReadFeedInteractor readFeedInteractor, SyncFeedInteractor syncFeedInteractor)
        {
            return new MockPresenter();
        }
    }


    /**
     * when asked to show the feed, it will show a hard coded feed list
     */
    class MockPresenter implements FeedListContract.Presenter
    {

        private FeedListContract.View mView;

        @Override
        public void getFeed()
        {
            mView.showFeedList(FEED_LIST);
        }

        @Override
        public void syncFeed()
        {

        }

        @Override
        public void registerView(FeedListContract.View view)
        {
            mView = view;
        }

        @Override
        public void unregisterView()
        {
            mView = null;
        }

    }


    /**
     * checks that the feed list is displayed correctly
     */
    @Test
    public void testShowFeedList() throws Exception
    {
        // launch the activity
        activityRule.launchActivity(
                FeedListActivity.getIntent(InstrumentationRegistry.getTargetContext()));

        // check the feed items are displayed correctly
        for (int i = 0; i < FEED_LIST.size(); i++)
            onView(withId(R.id.recycler_view_feed))
                    .perform(RecyclerViewActions.scrollToPosition(i))
                    .check(matches(FeedAdapterViewHolderMatcher.atPosition(0, FEED_LIST.get(0))));
    }


}