package ahmed.news.feed_list;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import ahmed.news.entity.FeedItem;

/**
 * has custom matcher for the feed adapter when  used inside a recycler view
 * Created by ahmed on 9/24/2016.
 */
public class FeedAdapterViewHolderMatcher
{
    /**
     * checks that the item at this position in the recycler view matches the given feed item
     * checks the title only
     */
    public static Matcher<View> atPosition(final int position, FeedItem feedItem)
    {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class)
        {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("matching at position " + position
                        + " with title " + feedItem.getTitle() + " with date " + feedItem.getPubDate());
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view)
            {
                FeedAdapter.FeedViewHolder item = (FeedAdapter.FeedViewHolder) view.findViewHolderForAdapterPosition(position);
                if (item == null)
                    return false;
                return item.textViewTitle.getText().toString().equals(feedItem.getTitle());
            }
        };
    }


}
