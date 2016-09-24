package ahmed.news.feed_list;

import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.entity.FeedItem;
import dagger.Module;
import dagger.Provides;

/**
 * Created by ahmed on 9/24/2016.
 */
@Module
public class FeedListModule
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
