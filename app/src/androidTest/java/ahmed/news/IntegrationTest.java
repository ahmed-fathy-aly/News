package ahmed.news;

import android.content.ComponentName;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.data.FeedRemoteDataSourceImp;
import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;
import ahmed.news.entity.RSSFeed;
import ahmed.news.feed_item_details.FeedItemDetailsActivity;
import ahmed.news.feed_list.FeedAdapterViewHolderMatcher;
import ahmed.news.feed_list.FeedListActivity;
import ahmed.news.feed_list.FeedListContract;
import ahmed.news.feed_list.FeedListPresenter;
import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * tests the whole app,
 * but only the Feed data source is mocked so that we have hardcoded feeds to compare to what's displayed
 * that is, we know that the data source will give us certain feed items so we can check that those are the ones displayed
 * Created by ahmed on 9/24/2016.
 */
public class IntegrationTest
{
    /* constant mock data*/
    final static String TITLE = "Title 1";
    final static String IMAGE = "imigur/a.jpg";
    final static String DESCRIPTION = "Description bla \n bla \n  bla \n bla \n bla \n bla \n bla \n" +
            " bla \n bla \n bla \n bla \n bla \n bla \n" +
            " bla \n bla \n bla \n bla \n bla \n bla \n";
    final static String DATE = "Sat, 24 Sep 2016 00:33:31 GMT";
    final static String URL = "myFeed.com";
    final static List<FeedItem> FEED_LIST;

    static
    {
        FeedItem feedItem = new FeedItem();
        feedItem.setTitle(TITLE);
        feedItem.setDescription(DESCRIPTION);
        feedItem.setImage(new Image(IMAGE));
        feedItem.setPubDate(DATE);
        feedItem.setLink(URL);
        FEED_LIST = Arrays.asList(
                feedItem
                , new FeedItem("title2", "Fri, 23 Sep 2016 21:10:15 GMT")
                , new FeedItem("title3", "Fri, 23 Sep 2016 21:41:30 GMT")
                , new FeedItem("title4", "Fri, 23 Sep 2016 19:15:43 GMT")
        );

    }

    @Rule
    public ActivityTestRule<FeedListActivity> activityRule =
            new ActivityTestRule<>(FeedListActivity.class, true, false);

    /**
     * lets the application use the test component so it can inject the mocked feed source
     */
    @Before
    public void setup()
    {
        App app = (App) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
        app.setComponent(
                DaggerIntegrationTest_TestComponent
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
     * provides a mock feed data source
     */
    @Module
    class TestModule
    {
        @Provides
        public FeedRemoteDataSource provideFeedRemoteDataSource()
        {
            return new FeedRemoteDataSource()
            {
                @Override
                public RSSFeed getFeed() throws IOException
                {
                    return new RSSFeed(new Channel("my channel", FEED_LIST));
                }
            };
        }

        @Provides
        public FeedListContract.Presenter provideFeedListPresenter(FeedRemoteDataSource feedRemoteDataSource)
        {
            return new FeedListPresenter(feedRemoteDataSource);
        }

    }


    /**
     * open the list of feed
     * check the feed's entry in the list is clicked
     * click on an item from the list
     * make sure we reached the details activity
     * checks the feed item's details are displayed
     * press back
     * makes sure we are back in the list activity
     */
    @Test
    public void testListAndDetails() throws Exception
    {
        // launch the activity
        Intents.init();
        FeedListActivity activity =
                activityRule.launchActivity(
                        FeedListActivity.getIntent(InstrumentationRegistry.getTargetContext()));

        // check the feed items are displayed correctly
        for (int i = 0; i < FEED_LIST.size(); i++)
            onView(withId(R.id.recycler_view_feed))
                    .perform(RecyclerViewActions.scrollToPosition(i))
                    .check(matches(FeedAdapterViewHolderMatcher.atPosition(i, FEED_LIST.get(i))));

        // click on our feed item
        onView(withId(R.id.recycler_view_feed))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // make sure we reached the details activity
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getTargetContext(), FeedItemDetailsActivity.class)));

        // check the feed item's details are shown
        onView(withText(TITLE))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withText(DATE))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withText(DESCRIPTION))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withText(URL))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        // go back and make sure we reached the feed list activity again
        pressBack();
        intended(hasComponent(new ComponentName(InstrumentationRegistry.getTargetContext(), FeedListActivity.class)));

        // check the feed items are displayed correctly
        for (int i = 0; i < FEED_LIST.size(); i++)
            onView(withId(R.id.recycler_view_feed))
                    .perform(RecyclerViewActions.scrollToPosition(i))
                    .check(matches(FeedAdapterViewHolderMatcher.atPosition(i, FEED_LIST.get(i))));
    }
}
