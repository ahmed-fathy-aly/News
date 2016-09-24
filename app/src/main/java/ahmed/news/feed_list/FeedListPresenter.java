package ahmed.news.feed_list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.entity.FeedItem;
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
    private FeedRemoteDataSource mFeedRemoteDataSource;


    @Inject
    public FeedListPresenter(FeedRemoteDataSource feedRemoteDataSource)
    {
        mFeedRemoteDataSource = feedRemoteDataSource;
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
                    try
                    {
                        return rx.Observable.just(mFeedRemoteDataSource.getFeed());
                    } catch (IOException e)
                    {
                        return rx.Observable.error(e);
                    }
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
                            List<FeedItem> feedList = rssFeed.getChannel().getFeedItemList();
                            mView.showFeedList(feedList != null ? feedList : new ArrayList<FeedItem>());
                            mView.showTitle(rssFeed.getChannel().getTitle());
                        }
                    }
                });
    }

    @Override
    public void syncFeed()
    {
        getFeed();
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
