package ahmed.news;

import ahmed.news.data.FeedRemoteDataSource;
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
    @Provides
    public FeedRemoteDataSource provideFeedRemoteDataSource()
    {
        return new FeedRemoteDataSource();
    }

    @Provides
    public FeedListContract.Presenter provideFeedListPresenter(FeedRemoteDataSource feedRemoteDataSource)
    {
        return new FeedListPresenter(feedRemoteDataSource);
    }

}
