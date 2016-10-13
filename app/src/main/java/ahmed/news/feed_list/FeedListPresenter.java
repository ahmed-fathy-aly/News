package ahmed.news.feed_list;

import java.util.List;

import javax.inject.Inject;

import ahmed.news.data.SyncFeedInteractor;
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
    private SyncFeedInteractor mSyncFeedInteractor;
    private ReadFeedInteractor mReadFeedInteractor;

    @Inject
    public FeedListPresenter(ReadFeedInteractor readFeedInteractor, SyncFeedInteractor syncFeedInteractor)
    {
        mReadFeedInteractor = readFeedInteractor;
        mSyncFeedInteractor = syncFeedInteractor;
    }

    @Override
    public void getFeed()
    {
        // show progress
        if (mView != null)
            mView.showProgress();

        // read the feed
        mReadFeedInteractor.readFeed(new ReadFeedInteractor.ReadFeedCallback()
        {
            @Override
            public void foundFeed(List<FeedItem> feedItems, String channelTitle)
            {
                if (mView != null)
                {
                    mView.showFeedList(feedItems);
                    mView.showTitle(channelTitle);
                    mView.hideProgress();
                }
            }

            @Override
            public void emptyFeed()
            {
                syncFeed();
            }

            @Override
            public void fail(String error)
            {
                if (mView != null)
                {
                    mView.showError(error);
                    mView.hideProgress();
                }
            }
        });

    }

    @Override
    public void syncFeed()
    {
        mSyncFeedInteractor.sync(new SyncFeedInteractor.SyncCallback()
        {
            @Override
            public void syncDone(String channelTitle, List<FeedItem> feedItemsAfterSync)
            {
                if (mView != null)
                {
                    mView.showTitle(channelTitle);
                    mView.showFeedList(feedItemsAfterSync);
                    mView.hideProgress();
                }
            }

            @Override
            public void error(String errorMessage)
            {
                if (mView != null)
                {
                    mView.showError(errorMessage);
                    mView.hideProgress();
                }
            }

            @Override
            public void noFeedFound()
            {
                // do nothing for now
            }
        });
    }

    @Override
    public void onFeedClicked(FeedItem feedItem)
    {
        mReadFeedInteractor.markAsRead(feedItem.getTitle(), new ReadFeedInteractor.MarkAsReadCallback()
        {
            @Override
            public void marked()
            {
                if (mView != null)
                    mView.markAsRead(feedItem);
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
