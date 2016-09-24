package ahmed.news.feed_item_details;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ahmed.news.R;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;
import ahmed.news.feed_list.FeedAdapterViewHolderMatcher;
import ahmed.news.feed_list.FeedListActivity;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.scrollTo;

import static org.junit.Assert.*;

/**
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
        onView(withText(DATE))
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