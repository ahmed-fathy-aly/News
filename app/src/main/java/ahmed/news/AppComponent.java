package ahmed.news;

import javax.inject.Scope;

import ahmed.news.data.SyncFeedService;
import ahmed.news.feed_item_details.FeedItemDetailsFragment;
import ahmed.news.feed_item_details.FeedItemDetailsModule;
import ahmed.news.feed_list.FeedListContract;
import ahmed.news.feed_list.FeedListFragment;
import ahmed.news.feed_list.FeedListModule;
import dagger.Component;

/**
 * Created by ahmed on 9/22/2016.
 */
@Component(modules = {AppModule.class, FeedListModule.class, FeedItemDetailsModule.class})
public interface AppComponent
{
    void inject(FeedListContract.Presenter presenter);
    void inject(FeedListFragment fragment);
    void inject(FeedItemDetailsFragment fragment);
    void inject(SyncFeedService syncFeedService);
}
