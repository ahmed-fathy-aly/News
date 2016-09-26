package ahmed.news.data;

import android.app.IntentService;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.App;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;
import ahmed.news.entity.RSSFeed;
import ahmed.news.event.FeedUpdatedEvent;
import timber.log.Timber;


/**
 * - downloads the feed list
 * - updates the database
 * - notify any one who wants to hear about that
 */
public class SyncFeedService extends IntentService
{
    @Inject
    FeedRemoteDataSource feedRemoteDataSource;
    @Inject
    FeedLocalDataSource feedLocalDataSource;

    public SyncFeedService()
    {
        super("SyncFeedService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // inject the feed sources and start updating
        App app = (App) getApplication();
        app.getComponent().inject(this);

        syncFeed(feedRemoteDataSource, feedLocalDataSource);
    }

    public void syncFeed(FeedRemoteDataSource feedRemoteDataSource, FeedLocalDataSource feedLocalDataSource)
    {
        // download the new data
        RSSFeed newFeed = null;
        try
        {
            newFeed = feedRemoteDataSource.getFeed();

        } catch (IOException e)
        {
            Timber.e("error downloading feed %s", e.getMessage());
            return;
        }

        // update the local database
        feedLocalDataSource.storeFeed(newFeed);

        // notify anyone listening
        EventBus.getDefault().post(new FeedUpdatedEvent());
    }


}
