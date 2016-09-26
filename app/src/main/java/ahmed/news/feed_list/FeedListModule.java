package ahmed.news.feed_list;

import android.content.Context;

import ahmed.news.AppModule;
import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.data.FeedLocalDataSourceImp;
import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.data.FeedRemoteDataSourceImp;
import dagger.Module;
import dagger.Provides;

/**
 * Created by ahmed on 9/24/2016.
 */
@Module(includes = {AppModule.class})
public class FeedListModule
{
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
    public FeedListContract.Presenter provideFeedListPresenter(FeedLocalDataSource feedLocalDataSource)
    {
        return new FeedListPresenter(feedLocalDataSource);
    }
}
