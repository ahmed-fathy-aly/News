package ahmed.news;

import android.content.Context;

import ahmed.news.data.SyncFeedInteractor;
import ahmed.news.data.SyncFeedInteractorImp;
import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.data.FeedLocalDataSourceImp;
import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.data.FeedRemoteDataSourceImp;
import ahmed.news.feed_item_details.FeedItemDetailsContract;
import ahmed.news.feed_item_details.FeedItemDetailsPresenter;
import ahmed.news.feed_list.FeedListContract;
import ahmed.news.feed_list.FeedListPresenter;
import ahmed.news.feed_list.ReadFeedInteractor;
import ahmed.news.feed_list.ReadFeedInteractorImp;
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
    public SyncFeedInteractor provideSyncFeedInteractor(FeedRemoteDataSource feedRemoteDataSource, FeedLocalDataSource feedLocalDataSource)
    {
        return new SyncFeedInteractorImp(feedRemoteDataSource, feedLocalDataSource);
    }

    @Provides
    public ReadFeedInteractor provideReadFeedInteractor(FeedLocalDataSource feedLocalDataSource)
    {
        return new ReadFeedInteractorImp(feedLocalDataSource);
    }

    @Provides
    public FeedItemDetailsContract.Presenter provideFeedItemDetailsPreseneter(Context context)
    {
        return new FeedItemDetailsPresenter(context);
    }

    @Provides
    public FeedListContract.Presenter provideFeedListPresenter(ReadFeedInteractor readFeedInteractor, SyncFeedInteractor syncFeedInteractor)
    {
        return new FeedListPresenter(readFeedInteractor, syncFeedInteractor);
    }
}
