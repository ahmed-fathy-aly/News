package ahmed.news.data;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.App;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;
import ahmed.news.entity.RSSFeed;
import ahmed.news.event.FeedUpdatedEvent;
import timber.log.Timber;


/**
 * - downloads the feed list
 * - updates the database
 * - notify any one who wants to hear about that
 */
public class SyncFeedService extends GcmTaskService
{
    @Inject
    FeedRemoteDataSource feedRemoteDataSource;
    @Inject
    FeedLocalDataSource feedLocalDataSource;


    @Override
    public int onRunTask(TaskParams taskParams)
    {
        Timber.d("running feed service %s", taskParams.getTag());
        // inject the feed sources and start updating
        App app = (App) getApplication();
        app.getComponent().inject(this);

        // download the new data
        RSSFeed newFeed = null;
        try
        {
            newFeed = feedRemoteDataSource.getFeed();
        } catch (IOException e)
        {
            Timber.e("error downloading feed %s", e.getMessage());
            return GcmNetworkManager.RESULT_RESCHEDULE;
        }

        // update the local database
        feedLocalDataSource.storeFeed(newFeed);

        // notify anyone listening
        EventBus.getDefault().post(new FeedUpdatedEvent());

        Timber.d("finished syncing");
        return GcmNetworkManager.RESULT_SUCCESS;
    }


}
