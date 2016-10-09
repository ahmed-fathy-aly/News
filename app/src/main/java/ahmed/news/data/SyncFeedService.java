package ahmed.news.data;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import ahmed.news.App;
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
    SyncFeedInteractor mSyncFeedInteractor;

    @Override
    public int onRunTask(TaskParams taskParams)
    {
        Timber.d("running feed service %s", taskParams.getTag());

        // inject the feed sources and start updating
        App app = (App) getApplication();
        app.getComponent().inject(this);
        SyncFeedInteractor.SyncResult syncResult = mSyncFeedInteractor.syncSynchronous();

        // if failed then the service should be rescheduled
        if (syncResult.getErrorMessage() != null)
            return GcmNetworkManager.RESULT_RESCHEDULE;

        // if succeeded then notify any one listening for database updated
        EventBus.getDefault().post(new FeedUpdatedEvent());
        return GcmNetworkManager.RESULT_SUCCESS;
    }


}
