package ahmed.news.feed_item_details;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ahmed on 9/24/2016.
 */
@Module
public class FeedItemDetailsModule
{
    @Provides
    public FeedItemDetailsContract.Presenter provideFeedItemDetailsPreseneter()
    {
        return new FeedItemDetailsPresenter();
    }
}
