package ahmed.news.feed_list;

import java.util.List;

import javax.inject.Inject;

import ahmed.news.data.FeedDataSync;
import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmed on 9/21/2016.
 */
public class FeedListPresenter implements FeedListContract.Presenter
{

    private FeedListContract.View mView;
    private FeedLocalDataSource mFeedLocalDataSource;
    private FeedDataSync mFeedDataSync;

    @Inject
    public FeedListPresenter(FeedLocalDataSource feedLocalDataSource, FeedDataSync feedDataSync)
    {
        mFeedLocalDataSource = feedLocalDataSource;
        mFeedDataSync = feedDataSync;
    }

    @Override
    public void getFeed()
    {
        // show progress
        if (mView != null)
            mView.showProgress();

        // fetch data from API synchronously
        Observable
                .defer(() -> {

                    RSSFeed feed = mFeedLocalDataSource.getFeed();
                    Timber.d("defer feed %b ", (feed == null));
                    if (feed != null && feed.getChannel() != null && feed.getChannel().getFeedItemList() != null)
                        mFeedLocalDataSource.markAsRead(feed.getChannel().getFeedItemList());
                    return Observable.just(feed);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RSSFeed>()
                {
                    @Override
                    public void onCompleted()
                    {
                        if (mView != null)
                            mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        if (mView != null)
                        {
                            mView.showError(e.getMessage());
                            mView.hideProgress();
                        }
                    }

                    @Override
                    public void onNext(RSSFeed rssFeed)
                    {

                        if (rssFeed == null || rssFeed.getChannel() == null
                                || rssFeed.getChannel().getFeedItemList() == null)
                            syncFeed();
                        else if (mView != null)
                        {
                            mView.showFeedList(rssFeed.getChannel().getFeedItemList());
                            mView.showTitle(rssFeed.getChannel().getTitle());
                        }
                    }
                });
    }

    @Override
    public void syncFeed()
    {
        // show progress
        if (mView != null)
            mView.showProgress();

        // sync the data
        Observable
                .defer(() -> {
                    return rx.Observable.just(mFeedDataSync.sync());

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FeedDataSync.SyncResult>()
                {
                    @Override
                    public void onCompleted()
                    {
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        if (mView != null)
                        {
                            mView.showError(e.getMessage());
                            mView.hideProgress();
                        }
                    }

                    @Override
                    public void onNext(FeedDataSync.SyncResult syncResult)
                    {
                        if (syncResult.isSuccess())
                            getFeed();
                        else
                        {
                            if (mView != null)
                            {
                                mView.showError("Sync Failed");
                                mView.hideProgress();
                            }
                        }
                    }
                });

    }

    @Override
    public void registerView(FeedListContract.View view)
    {
        mView = view;
    }

    @Override
    public void unregisterView()
    {
        mView = null;
    }

}
