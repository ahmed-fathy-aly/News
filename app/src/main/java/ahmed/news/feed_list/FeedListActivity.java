package ahmed.news.feed_list;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import ahmed.news.BaseActivity;
import ahmed.news.R;
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

    @Override
    protected boolean showHome()
    {
        return false;
    }


    /**
     * use this to get an intent that launches this activity
     */
    public static Intent getIntent(Context context)
    {
        return new Intent(context, FeedListActivity.class);
    }

    @Override
    public void onFeedItemClicked(FeedItem feedItem, View viewContainer, TextView textViewTitle, ImageView imageViewThumbnail)
    {
        // open the details activity with an animation
        Intent intent = FeedItemDetailsActivity.newIntent(this, feedItem);
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        android.support.v4.util.Pair.create(viewContainer, getString(R.string.transiation_feed_to_details)),
                        android.support.v4.util.Pair.create(textViewTitle, getString(R.string.transiation_feed_to_details_title)),
                        android.support.v4.util.Pair.create(imageViewThumbnail, getString(R.string.transiation_feed_to_details_image))
                        );
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
