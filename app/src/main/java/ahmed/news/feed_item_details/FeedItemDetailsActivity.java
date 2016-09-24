package ahmed.news.feed_item_details;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ahmed.news.BaseActivity;
import ahmed.news.R;
import ahmed.news.entity.FeedItem;
import timber.log.Timber;

/**
 * a host for the details fragment
 */
public class FeedItemDetailsActivity extends BaseActivity
{
    /* cosntants */
    private static final String EXTRA_FEED_ITEM = "extraFeedItem";
    private FeedItem mFeedItem;

    /**
     * only use this to launch the activity
     * @param feedItem the item to be displayed
     */
    public static Intent newIntent(Context context, FeedItem feedItem)
    {
        Intent intent = new Intent(context, FeedItemDetailsActivity.class);
        intent.putExtra(EXTRA_FEED_ITEM, feedItem);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mFeedItem = (FeedItem) getIntent().getSerializableExtra(EXTRA_FEED_ITEM);
        Timber.d("null feeditem %b", (mFeedItem== null));

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment getFragment()
    {
        Timber.d("null feeditem %b", (mFeedItem== null));
        return FeedItemDetailsFragment.newInstance(mFeedItem);
    }
}
