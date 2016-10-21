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

        /**
         * shows that this feed item is read(a mark to differentiate it from the unread one)
         */
        void markAsRead(FeedItem feedItem);

        /**
         * show that no feed was found
         */
        void showErrorNoFeedFound();

        /**
         * show there's an error while syncing feed
         */
        void showErrorSyncingFeed();
    }

    public interface Presenter
    {
        /**
         * fetches and displays the feed(fixed RSS source)
         * reads from uses the local storage
         * if there are no feed items stored, it will ask the view to launcht the sync service
         * - asks the view to show progress
         * - reads the feedlist and asks the view to show it(or launch the sync service if it's not there)
         * - asks the view to hide progress
         * - marks these feeditems as read in the database(so they only appear as unread one time)
         */
        void getFeed();

        /**
         * asks the presenter to sync the feed
         * the presenter just asks the view to launch the sync service
         */
        void syncFeed();

        /**
         * when a feed is clicked, the presenter will mark it as read and ask the view to open its details
         */
        void onFeedClicked(FeedItem feedItem);

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
