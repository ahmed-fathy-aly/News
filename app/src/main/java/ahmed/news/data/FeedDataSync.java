package ahmed.news.data;

/**
 * syncs two feed sources(updates one to match the other)
 * Created by ahmed on 9/28/2016.
 */
public interface FeedDataSync
{

    /**
     * uses the data sources to sync them together
     */
    SyncResult sync();

    /**
     * a wrapper class for the results returned after a sync
     */
    class SyncResult
    {
        private boolean mSuccess;

        public SyncResult(boolean success)
        {
            mSuccess = success;
        }

        public boolean isSuccess()
        {
            return mSuccess;
        }


    }
}
