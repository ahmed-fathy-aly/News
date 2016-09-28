package ahmed.news.data;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.io.IOException;

import javax.inject.Inject;

import ahmed.news.entity.RSSFeed;
import timber.log.Timber;

/**
 * syncs a local feed source to match a remote one
 * Created by ahmed on 9/28/2016.
 */
public class FeedDataSyncImp implements FeedDataSync
{
    private FeedRemoteDataSource mFeedRemoteDataSource;
    private FeedLocalDataSource mFeedLocalDataSource;

    @Inject
    public FeedDataSyncImp(FeedRemoteDataSource feedRemoteDataSource, FeedLocalDataSource feedLocalDataSource)
    {
        mFeedLocalDataSource = feedLocalDataSource;
        mFeedRemoteDataSource = feedRemoteDataSource;
    }

    @Override
    public SyncResult sync()
    {
        // download the new data
        RSSFeed newFeed = null;
        try
        {
            newFeed = mFeedRemoteDataSource.getFeed();
        } catch (IOException e)
        {
            return new SyncResult(false);
        }

        // update the local database
        mFeedLocalDataSource.storeFeed(newFeed);

        return new SyncResult(true);
    }
}
