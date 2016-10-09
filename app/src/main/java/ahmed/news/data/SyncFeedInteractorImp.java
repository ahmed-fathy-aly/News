package ahmed.news.data;

import java.io.IOException;

import javax.inject.Inject;

import ahmed.news.entity.RSSFeed;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * syncs a local feed source to match a remote one
 * Created by ahmed on 9/28/2016.
 */
public class SyncFeedInteractorImp implements SyncFeedInteractor
{
    private FeedRemoteDataSource mFeedRemoteDataSource;
    private FeedLocalDataSource mFeedLocalDataSource;

    @Inject
    public SyncFeedInteractorImp(FeedRemoteDataSource feedRemoteDataSource, FeedLocalDataSource feedLocalDataSource)
    {
        mFeedLocalDataSource = feedLocalDataSource;
        mFeedRemoteDataSource = feedRemoteDataSource;
    }

    @Override
    public void sync(SyncCallback callback)
    {
        Observable.defer(() -> Observable.just(syncSynchronous()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SyncResult>()
                {
                    @Override
                    public void onCompleted()
                    {
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        callback.error(e.getMessage());
                    }

                    @Override
                    public void onNext(SyncResult syncResult)
                    {
                        if (syncResult.getErrorMessage() != null)
                            callback.error(syncResult.getErrorMessage());
                        else if (syncResult.isNoFeedFound())
                            callback.noFeedFound();
                        else
                            callback.syncDone(syncResult.getChannelTitle(), syncResult.getFeedItemsAfterSync());

                    }
                });
    }


    @Override
    public SyncResult syncSynchronous()
    {
        // download the new data
        RSSFeed newFeed = null;
        try
        {
            newFeed = mFeedRemoteDataSource.getFeed();
        } catch (IOException e)
        {
            return new SyncResult(false, e.getMessage(), null, null);
        }

        // update the local database
        if (newFeed != null)
            mFeedLocalDataSource.storeFeed(newFeed);

        // read the new feed
        RSSFeed feed = mFeedLocalDataSource.getFeed();
        if (feed == null || feed.getChannel() == null || feed.getChannel().getFeedItemList() == null)
            return new SyncResult(true, null, null, null);

        return new SyncResult(false, null, feed.getChannel().getTitle(), feed.getChannel().getFeedItemList());
    }
}
