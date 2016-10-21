package ahmed.news.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.Constants;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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
                        callback.errorDownloadingFeed();
                    }

                    @Override
                    public void onNext(SyncResult syncResult)
                    {
                        if (syncResult.isErrorDownloadingFeed() )
                            callback.errorDownloadingFeed();
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
            Timber.d("exception " + e.getMessage());
            return new SyncResult(false, true, null, null);
        }

        // update the local database
        if (newFeed != null)
            mFeedLocalDataSource.storeFeed(newFeed);

        // read the new feed
        RSSFeed feed = mFeedLocalDataSource.getFeed();
        if (feed == null || feed.getChannel() == null || feed.getChannel().getFeedItemList() == null)
            return new SyncResult(true, false, null, null);

        // keep the most latest feed items and delete the rest
        List<FeedItem> allItems = feed.getChannel().getFeedItemList();
        List<FeedItem> newItems = new ArrayList<>();
        for(int i = 0; i < Math.min(Constants.MAX_KEPT_ITEMS, allItems.size()); i++)
            newItems.add(allItems.get(i));

        // delete the old ones
        if (allItems.size() > Constants.MAX_KEPT_ITEMS)
        {
            List<String> deletedTitles = new ArrayList<>();
            for (int i = Constants.MAX_KEPT_ITEMS; i < allItems.size(); i++)
                deletedTitles.add(allItems.get(i).getTitle());
            mFeedLocalDataSource.removeItems(deletedTitles);
        }

        return new SyncResult(false, false, feed.getChannel().getTitle(), newItems);
    }
}
