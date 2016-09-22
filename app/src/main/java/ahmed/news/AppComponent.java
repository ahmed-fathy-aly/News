package ahmed.news;

import ahmed.news.feed_list.FeedListContract;
import ahmed.news.feed_list.FeedListFragment;
import dagger.Component;

/**
 * Created by ahmed on 9/22/2016.
 */
@Component(modules = {AppModule.class})
public interface AppComponent
{
    void inject(FeedListContract.Presenter presenter);
    void inject(FeedListContract.View view);
}
