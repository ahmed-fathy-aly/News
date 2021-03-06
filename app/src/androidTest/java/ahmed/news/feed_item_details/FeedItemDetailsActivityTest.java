package ahmed.news.feed_item_details;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import ahmed.news.App;
import ahmed.news.AppModule;
import ahmed.news.DaggerAppComponent;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * tests the feed item details UI and mocks the presenter
 * Created by ahmed on 9/24/2016.
 */
public class FeedItemDetailsActivityTest
{
    /* constant mock data*/
    final static String TITLE = "Title 1";
    final static String IMAGE = "imigur/a.jpg";
    final static String DESCRIPTION = "Description bla \n bla \n  bla \n bla \n bla \n bla \n bla \n" +
            " bla \n bla \n bla \n bla \n bla \n bla \n" +
            " bla \n bla \n bla \n bla \n bla \n bla \n";
    final static String DATE = "Sat, 24 Sep 2016 00:33:31 GMT";
    final static String URL = "myFeed.com";
    final static String URI = "http:myFeed.com";
    static FeedItem FEED_ITEM;

    static
    {
        FEED_ITEM = new FeedItem();
        FEED_ITEM.setTitle(TITLE);
        FEED_ITEM.setDescription(DESCRIPTION);
        FEED_ITEM.setImage(new Image(IMAGE));
        FEED_ITEM.setPubDate(DATE);
        FEED_ITEM.setLink(URL);
    }

    @Rule
    public ActivityTestRule<FeedItemDetailsActivity> activityRule =
            new ActivityTestRule<>(FeedItemDetailsActivity.class, true, false);


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
                        .build()
        );

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
        public FeedItemDetailsContract.Presenter provideFeedItemDetailsPreseneter(Context context)
        {
            return new FeedItemDetailsContract.Presenter()
            {
                FeedItemDetailsContract.View mView;

                @Override
                public void setFeedItem(FeedItem feedItem)
                {

                }

                @Override
                public void showFeedDetails()
                {
                    mView.showTitle(TITLE);
                    mView.showDescription(DESCRIPTION);
                    mView.showImage(IMAGE);
                    mView.showDate(Calendar.getInstance());
                    mView.showUrlString(URL);
                }

                @Override
                public void onLinkClicked()
                {
                    mView.openUri(URI);
                }

                @Override
                public void register(FeedItemDetailsContract.View view)
                {
                    mView = view;
                }

                @Override
                public void unregister()
                {
                    mView = null;
                }
            };
        }
    }

    /**
     * checks that the feed list is displayed correctly
     */
    @Test
    public void testShowFeedDetails() throws Exception
    {
        // launch the activity
        Intent intent = FeedItemDetailsActivity.newIntent(
                InstrumentationRegistry.getTargetContext(), FEED_ITEM);
        activityRule.launchActivity(intent);

        // check everything is shown
        onView(withText(TITLE))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withText(DESCRIPTION))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
        onView(withText(URL))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }


    /**
     * tries clicking on the link and it doesn't crash
     */
    @Test
    public void testClickLink()
    {
        // launch the activity
        Intent intent = FeedItemDetailsActivity.newIntent(
                InstrumentationRegistry.getTargetContext(), FEED_ITEM);
        activityRule.launchActivity(intent);

        // click on the link
        onView(withText(URL))
                .perform(scrollTo(), click());
    }
}