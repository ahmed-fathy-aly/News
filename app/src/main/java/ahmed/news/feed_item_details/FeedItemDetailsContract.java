package ahmed.news.feed_item_details;

import ahmed.news.entity.FeedItem;

/**
 * Created by ahmed on 9/24/2016.
 */
public class FeedItemDetailsContract
{
    public interface Presenter
    {
        /**
         * store the feed item to be used in any further operation
         */
        void setFeedItem(FeedItem feedItem);

        /**
         * ask to show the feed's title, description and image
         */
        void showFeedDetails();

        /**
         * asks for the uri so the view can use it to open the feed in the browser for example
         */
        void onLinkClicked();

        /**
         * connects the view that will be presented with data
         * (only one view, multiple calls to registerView will only keep the most recent view passed)
         */
        void register(FeedItemDetailsContract.View view);

        /**
         * clears any reference to the view
         * (to prevent any memory leaks)
         */
        void unregister();
    }

    public interface View
    {
        /**
         * lazy load the image's url
         */
        void showImage(String imageUrl);

        /**
         * show the feed item's title
         */
        void showTitle(String title);

        /**
         * show the feed item's description(probably needs multi lines)
         */
        void showDescription(String description);

        /**
         * the publishing date of the list item
         */
        void showDate(String date);

        /**
         * redirect to the browser with that uri
         */
        void openUri(String uri);

        /**
         * display the url(just display it, not open it)
         */
        void showUrlString(String urlSting);

    }
}
