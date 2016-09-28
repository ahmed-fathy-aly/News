package ahmed.news;

import android.content.Context;

import ahmed.news.data.FeedDataSync;
import ahmed.news.data.FeedDataSyncImp;
import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.data.FeedLocalDataSourceImp;
import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.data.FeedRemoteDataSourceImp;
import ahmed.news.feed_item_details.FeedItemDetailsContract;
import ahmed.news.feed_item_details.FeedItemDetailsPresenter;
import ahmed.news.feed_list.FeedListContract;
import ahmed.news.feed_list.FeedListPresenter;
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

    @Provides
    public FeedItemDetailsContract.Presenter provideFeedItemDetailsPreseneter()
    {
        return new FeedItemDetailsPresenter();
    }

    @Provides
    public FeedRemoteDataSource provideFeedRemoteDataSource()
    {
        return new FeedRemoteDataSourceImp();
    }

    @Provides
    public FeedLocalDataSource provideFeedLocalDataSource(Context context)
    {
        return new FeedLocalDataSourceImp(context);
    }

    @Provides
    public FeedDataSync provideFeedDataSync(FeedRemoteDataSource feedRemoteDataSource, FeedLocalDataSource feedLocalDataSource)
    {
        return new FeedDataSyncImp(feedRemoteDataSource, feedLocalDataSource);
    }

    @Provides
    public FeedListContract.Presenter provideFeedListPresenter(FeedLocalDataSource feedLocalDataSource, FeedDataSync feedDataSync)
    {
        return new FeedListPresenter(feedLocalDataSource, feedDataSync);
    }
}
