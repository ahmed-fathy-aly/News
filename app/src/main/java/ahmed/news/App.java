package ahmed.news;

import android.app.Application;
import android.os.StrictMode;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.squareup.leakcanary.LeakCanary;

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

        // setup leak canary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        // setup the strict policy
        if (BuildConfig.DEBUG)
        {
            Timber.d("enabling strict mode");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                    .penaltyLog()
                    .build());
        }
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

