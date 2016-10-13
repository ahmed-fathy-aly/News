package ahmed.news.feed_list;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.data.FeedLocalDataSource;
import ahmed.news.data.FeedRemoteDataSource;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.RSSFeed;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static rx.observers.Subscribers.create;

/**
 * uses the local feed data source to read the feed
 * Created by ahmed on 10/6/2016.
 */

public class ReadFeedInteractorImp implements ReadFeedInteractor
{
    private FeedLocalDataSource mFeedLocalDataSource;

    @Inject
    public ReadFeedInteractorImp(FeedLocalDataSource feedLocalDataSource)
    {
        mFeedLocalDataSource = feedLocalDataSource;
    }

    @Override
    public void readFeed(ReadFeedCallback callback)
    {
        Observable
                .defer(()->Observable.just(mFeedLocalDataSource.getFeed()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RSSFeed>()
                {
                    @Override
                    public void onCompleted()
                    {
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        callback.fail(e.getMessage());
                    }

                    @Override
                    public void onNext(RSSFeed object)
                    {
                        RSSFeed rssFeed = (RSSFeed) object;
                        if (rssFeed == null
                                || rssFeed.getChannel() == null
                                || rssFeed.getChannel().getFeedItemList() == null)
                            callback.emptyFeed();
                        else
                            callback.foundFeed(rssFeed.getChannel().getFeedItemList(), rssFeed.getChannel().getTitle());
                    }
                });
    }

    @Override
    public void markAsRead(String feedTitle, MarkAsReadCallback callback)
    {
        Observable
                .create((i)->{mFeedLocalDataSource.markAsRead(feedTitle); i.onCompleted();})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>()
                {
                    @Override
                    public void onCompleted()
                    {
                        callback.marked();
                    }

                    @Override
                    public void onError(Throwable e)
                    {

                    }

                    @Override
                    public void onNext(Object object)
                    {
                    }
                });
    }
}
