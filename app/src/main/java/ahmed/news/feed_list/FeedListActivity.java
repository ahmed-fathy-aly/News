package ahmed.news.feed_list;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.Arrays;
import java.util.List;

import ahmed.news.BaseActivity;
import ahmed.news.entity.Feedtem;
import timber.log.Timber;

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

}
