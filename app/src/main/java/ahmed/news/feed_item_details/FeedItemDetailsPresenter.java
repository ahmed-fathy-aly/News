package ahmed.news.feed_item_details;

import javax.inject.Inject;

import ahmed.news.entity.FeedItem;
import timber.log.Timber;

/**
 * presents the details of a single feed item
 * Created by ahmed on 9/24/2016.
 */
public class FeedItemDetailsPresenter implements FeedItemDetailsContract.Presenter
{
    protected FeedItemDetailsContract.View mView;
    private FeedItem mFeedItem;

    @Inject
    public FeedItemDetailsPresenter()
    {

    }

    @Override
    public void setFeedItem(FeedItem feedItem)
    {
        mFeedItem = feedItem;
    }

    @Override
    public void showFeedDetails()
    {
        if (mView != null)
        {
            Timber.d("null feeditem %b", (mFeedItem == null));

            if (mFeedItem.getTitle() != null)
                mView.showTitle(mFeedItem.getTitle());
            if (mFeedItem.getDescription() != null)
                mView.showDescription(mFeedItem.getDescription());
            if (mFeedItem.getImage() != null
                    && mFeedItem.getImage().getUrl() != null
                    && mFeedItem.getImage().getUrl().length() > 0)
                mView.showImage(mFeedItem.getImage().getUrl());
            if (mFeedItem.getPubDate() != null)
                mView.showDate(mFeedItem.getPubDate());
            if (mFeedItem.getLink() != null)
                mView.showUrlString(mFeedItem.getLink());
        }
    }

    @Override
    public void onLinkClicked()
    {
        if (mView != null && mFeedItem != null)
        {
            String url = mFeedItem.getLink();
            if (url != null && url.length() > 0)
            {
                String uri = "http:" + mFeedItem.getLink();
                mView.openUri(uri);
            }
        }
    }

    @Override
    public void register(FeedItemDetailsContract.View view)
    {
        mView = view;
    }

    @Override
    public void unregister()
    {
        mView = null;
    }
}
