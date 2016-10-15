package ahmed.news.feed_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ahmed.news.R;
import ahmed.news.entity.FeedItem;
import ahmed.news.feed_item_details.FeedItemDetailsActivity;
import ahmed.news.feed_item_details.FeedItemDetailsFragment;

/**
 * holds a FeedListFragment
 */
public class FeedListActivity extends AppCompatActivity implements FeedListFragment.OnFragmentInteractionListener
{

    private FeedListFragment mFeedListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);

        // add the list fragment
        mFeedListFragment = (FeedListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment_container);
        if (mFeedListFragment == null)
        {
            mFeedListFragment = FeedListFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.list_fragment_container, mFeedListFragment)
                    .commit();
        }
    }

    /**
     * use this to get an intent that launches this activity
     */
    public static Intent getIntent(Context context)
    {
        return new Intent(context, FeedListActivity.class);
    }

    @Override
    public void onFeedItemClicked(FeedItem feedItem, View viewContainer, TextView textViewTitle, ImageView imageViewThumbnail, TextView textViewTime)
    {
        if (findViewById(R.id.details_fragment_container) == null)
        {
            // if it's one pane mode then open the details activity with an animation
            Intent intent = FeedItemDetailsActivity.newIntent(this, feedItem);
           // startActivity(intent);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                         //   android.support.v4.util.Pair.create(viewContainer, getString(R.string.transiation_feed_to_details)),
                            android.support.v4.util.Pair.create(textViewTitle, getString(R.string.transiation_feed_to_details_title)),
                            android.support.v4.util.Pair.create(imageViewThumbnail, getString(R.string.transiation_feed_to_details_image)),
                            android.support.v4.util.Pair.create(textViewTime, getString(R.string.transiation_feed_to_details_date))
                            );
            ActivityCompat.startActivity(this, intent, options.toBundle());
        } else
        {
            // if it's two pane then just add the fragment to the details layout
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.details_fragment_container, FeedItemDetailsFragment.newInstance(feedItem))
                    .addToBackStack(null)
                    .commit();
            mFeedListFragment.renderUpdatedIdx();

        }
    }
}
