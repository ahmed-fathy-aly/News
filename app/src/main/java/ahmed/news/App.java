package ahmed.news;

import android.app.Application;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import ahmed.news.data.SyncFeedService;
import ahmed.news.data.SyncTaskScheduler;
import timber.log.Timber;

/**
 * Created by ahmed on 9/22/2016.
 */
public class App extends Application
{
    private AppComponent mComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        SyncTaskScheduler.scheduleRepeatingSync(this);
    }

    /**
     * only one component to inject dependencies
     */
    public AppComponent getComponent()
    {
        if (mComponent == null)
            mComponent = DaggerAppComponent
                    .builder()
                    .appModule(new AppModule(getApplicationContext()))
                    .build();
        return mComponent;
    }

    /**
     * only used for testing, to have another component that injects mocked objects
     */
    public void setComponent(AppComponent component)
    {
        mComponent = component;
    }
}

