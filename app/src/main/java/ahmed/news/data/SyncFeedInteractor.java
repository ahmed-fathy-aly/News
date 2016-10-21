package ahmed.news.data;

import java.util.List;

import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;

/**
 * syncs two feed sources(updates one to match the other)
 * Created by ahmed on 9/28/2016.
 */
public interface SyncFeedInteractor
{

    /**
     * uses the data sources to sync them together
     * gets data from the remote source, adds it to the local source
     * asks the local data source to delete the old items(only keep the first few ones)
     */
    void sync(SyncCallback syncCallback);

    SyncResult syncSynchronous();

    interface SyncCallback
    {
        void syncDone(String channelTitle, List<FeedItem>  feedItemsAfterSync);

        void error(String errorMessage);

        void noFeedFound();
    }

    /**
     * a wrapper class for the results returned after a sync
     */
    class SyncResult
    {
        private boolean mNoFeedFound;
        private String mErrorMessage;
        private String mChannelTitle;
        private List<FeedItem> mFeedItemsAfterSync;

        public SyncResult(boolean noFedFoundFeed, String errorMessage, String channelTitle, List<FeedItem> feedItemsAfterSync)
        {
            mNoFeedFound = noFedFoundFeed;
            mErrorMessage = errorMessage;
            mChannelTitle = channelTitle;
            mFeedItemsAfterSync = feedItemsAfterSync;
        }

        public boolean isNoFeedFound()
        {
            return mNoFeedFound;
        }


        public String getErrorMessage()
        {
            return mErrorMessage;
        }

        public String getChannelTitle()
        {
            return mChannelTitle;
        }

        public List<FeedItem> getFeedItemsAfterSync()
        {
            return mFeedItemsAfterSync;
        }

    }
}
