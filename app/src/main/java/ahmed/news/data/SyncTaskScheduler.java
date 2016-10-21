package ahmed.news.data;

import android.content.Context;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import ahmed.news.Constants;

/**
 * manages registering the feed sync service
 * Created by ahmed on 9/27/2016.
 */
public class SyncTaskScheduler
{
    private static final String TASK_TAG_PERIODIC = "periodicFeedSync";

    /**
     * schedules a repeating task that syncs the feed
     * does nothing if the task is already scheduled
     */
    public static void scheduleRepeatingSync(Context context)
    {
        GcmNetworkManager networkManager = GcmNetworkManager.getInstance(context);
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(SyncFeedService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(Constants.SYNC_REPEATING_PERIOD)
                .setPersisted(true)
                .setUpdateCurrent(false)
                .build();
        networkManager.schedule(task);
    }




}
