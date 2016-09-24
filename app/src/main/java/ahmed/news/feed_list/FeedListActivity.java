package ahmed.news.feed_list;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import ahmed.news.BaseActivity;
import ahmed.news.entity.FeedItem;
import ahmed.news.feed_item_details.FeedItemDetailsActivity;

/**
 * holds a FeedListFragment
 */
public class FeedListActivity extends BaseActivity implements FeedListFragment.OnFragmentInteractionListener
{
    @Override
    protected Fragment getFragment()
    {
        return FeedListFragment.newInstance();
    }

    /**
     * use this to get an intent that launches this activity
     */
    public static Intent getIntent(Context context)
    {
        return new Intent(context, FeedListActivity.class);
    }

    @Override
    public void onFeedItemClicked(FeedItem feedItem, View view)
    {
        // open the details activity
        startActivity(FeedItemDetailsActivity.newIntent(this, feedItem));
    }
}
