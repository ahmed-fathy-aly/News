package ahmed.news;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * a place holder for a fragment
 * any activity that will only be a placeholder to a fragment should extend this
 */
public abstract class BaseActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // add the main fragment if it's not already there
        if (getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container) == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, getFragment())
                    .commit();
    }

    /**
     * the main fragment that will take the whole screen
     */
    abstract protected Fragment getFragment();
}
