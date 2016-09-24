package ahmed.news.feed_list;

import java.util.List;

import ahmed.news.entity.FeedItem;

/**
 * Defines interfaces for the presenter and view that handle showing a list of feed items
 * Created by ahmed on 9/21/2016.
 */
public class FeedListContract
{
    public interface View
    {
        /**
         * shows that something is running in the background
         */
        void showProgress();


        /**
         * shows that what was running in the background has finished
         */
        void hideProgress();

        /**
         * shows an error(like the reason why the list of feed couldn't be downloaded for example)
         */
        void showError(String errorMessage);

        /**
         * shows a listed-display of feed
         */
        void showFeedList(List<FeedItem> feedList);

        /**
         * shows the title of the channel
         */
        void showTitle(String title);
    }

    public interface Presenter
    {
        /**
         * fetches and displays the feed(fixed RSS source)
         * now, it grabs the feed from the internet everytime
         * - asks the view to show progress
         * - downloads the feedlist and asks the view to show it
         * - asks the view to hide progress
         */
        void getFeed();

        /**
         * asks the presenter to sync the feed then present it
         * now, it just relies on getFeed()
         */
        void syncFeed();

        /**
         * connects the view that will be presented with data
         * (only one view, multiple calls to registerView will only keep the most recent view passed)
         */
        void registerView(View view);

        /**
         * clears any reference to the view
         * (to prevent any memory leaks)
         */
        void unregisterView();

    }


}
