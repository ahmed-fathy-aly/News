package ahmed.news.feed_list;

import javax.inject.Inject;

import ahmed.news.data.FeedDataSync;
import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.entity.RSSFeed;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
                    return rx.Observable.just(mFeedLocalDataSource.getFeed());

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
                        if (mView != null)
                        {
                            if (rssFeed == null || rssFeed.getChannel() == null
                                    || rssFeed.getChannel().getFeedItemList() == null)
                                syncFeed();
                            else
                            {
                                mView.showFeedList(rssFeed.getChannel().getFeedItemList());
                                mView.showTitle(rssFeed.getChannel().getTitle());
                            }
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
