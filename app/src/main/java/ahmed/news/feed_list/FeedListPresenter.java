package ahmed.news.feed_list;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.entity.Feedtem;
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

    public FeedListPresenter()
    {
        setFeedRemoteDataSource(new FeedRemoteDataSource());
    }

    /**
     * only uses for testing, but the datasource will always be initialized in the constructor
     */
    public void setFeedRemoteDataSource(FeedRemoteDataSource feedRemoteDataSource)
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
                            List<Feedtem> feedList = rssFeed.getChannel().getFeedtemList();
                            mView.showFeedList(feedList != null ? feedList : new ArrayList<Feedtem>());
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
