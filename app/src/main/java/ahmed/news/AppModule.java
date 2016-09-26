package ahmed.news;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ahmed on 9/22/2016.
 */
@Module
public class AppModule
{

    private Context mContext;

    public AppModule(Context context)
    {
        mContext = context;
    }

    @Provides
    Context provideContext()
    {
        return mContext;
    }
}
