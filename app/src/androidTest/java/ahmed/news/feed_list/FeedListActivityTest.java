package ahmed.news.feed_list;

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
import ahmed.news.AppComponent;
import ahmed.news.R;
import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.entity.Feedtem;
import dagger.Component;
import dagger.Module;
import dagger.Provides;

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
    List<Feedtem> FEED_LIST = Arrays.asList(
            new Feedtem("title1", "Sat, 24 Sep 2016 00:33:31 GMT"),
            new Feedtem("title2", "Fri, 23 Sep 2016 21:10:15 GMT")
            , new Feedtem("title3", "Fri, 23 Sep 2016 21:41:30 GMT")
            , new Feedtem("title4", "Fri, 23 Sep 2016 19:15:43 GMT")
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
                DaggerFeedListActivityTest_TestComponent
                        .builder()
                        .testModule(new TestModule())
                        .build());
    }

    /**
     * uses the test module instead of the AppModule
     */
    @Component(modules = {TestModule.class})
    interface TestComponent extends AppComponent
    {
    }

    /**
     * provides a mock presenter
     */
    @Module
    class TestModule
    {
        @Provides
        @Nullable
        public FeedRemoteDataSource provideFeedRemoteDataSource()
        {
            return null;
        }

        @Provides
        public FeedListContract.Presenter provideFeedListPresenter(@Nullable FeedRemoteDataSource feedRemoteDataSource)
        {
            return new MockPresenter(null);
        }

    }


    /**
     * when asked to show the feed, it will show a hard coded feed list
     */
    class MockPresenter extends FeedListPresenter
    {

        public MockPresenter(FeedRemoteDataSource feedRemoteDataSource)
        {
            super(feedRemoteDataSource);
        }

        @Override
        public void getFeed()
        {
            getView().showFeedList(FEED_LIST);
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