package ahmed.news.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import ahmed.news.entity.Channel;
import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;
import ahmed.news.entity.RSSFeed;
import timber.log.Timber;

/**
 * uses SQLite to store the feed data
 * Created by ahmed on 9/26/2016.
 */
public class FeedLocalDataSourceImp implements FeedLocalDataSource
{
    private Context mContext;

    @Inject
    public FeedLocalDataSourceImp(Context context)
    {
        mContext = context;
    }

    @Override
    public RSSFeed getFeed()
    {
        // open the database
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // get the channel's title(if the channel exists)
        Cursor channelCursor = database.query(DatabaseContract.ChannelEntry.TABLE_NAME
                , null, null, null, null, null, null);
        if (!channelCursor.moveToFirst())
            return null;
        Channel channel = new Channel();
        channel.setTitle(
                channelCursor.getString(channelCursor.getColumnIndex(
                        DatabaseContract.ChannelEntry.COLOUMN_NAME)));
        channel.setDescription(
                channelCursor.getString(channelCursor.getColumnIndex(
                        DatabaseContract.ChannelEntry.COLOUMN_DESRIPTION)));

        // get the feed items
        List<FeedItem> feedItemList = new ArrayList<>();
        Cursor feedCursor = database.query(DatabaseContract.FeedItemEntry.TABLE_NAME
                , null, null, null, null, null, null);
        if (feedCursor.moveToFirst());
        do
        {
            FeedItem feedItem = new FeedItem();
            feedItem.setTitle(
                    feedCursor.getString(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_TITLE
                    )));
            feedItem.setDescription(
                    feedCursor.getString(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_DESRIPTION
                    )));
            feedItem.setLink(
                    feedCursor.getString(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_LINK
                    )));
            feedItem.setPubDate(
                    feedCursor.getString(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_PUB_DATE
                    )));
            Image image = new Image();
            image.setWidth(
                    feedCursor.getInt(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_WIDTH
                    )));
            image.setHeight(
                    feedCursor.getInt(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_HEIGHT
                    )));
            image.setUrl(
                    feedCursor.getString(feedCursor.getColumnIndex(
                            DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_URL
                    )));
            feedItem.setImage(image);
            feedItem.setRead(feedCursor.getInt(feedCursor.getColumnIndex(
                    DatabaseContract.FeedItemEntry.COLOUMN_IS_READ
            ))== 1);
            feedItemList.add(feedItem);

        } while (feedCursor.moveToNext());

        // sort by date, the latest first
        Collections.sort(feedItemList,
                (l, r) ->(l.getCalendar() == null || r.getCalendar() == null)?
                        0 : r.getCalendar().compareTo(l.getCalendar()));

        // create the feed
        channel.setFeedItemList(feedItemList);
        RSSFeed rssFeed = new RSSFeed();
        rssFeed.setChannel(channel);
        return rssFeed;
    }

    @Override
    public void storeFeed(RSSFeed newFeed)
    {
        // open the database
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // first, insert the channel's info
        database.beginTransaction();
        ContentValues channelContentValues = new ContentValues();
        channelContentValues.put(DatabaseContract.ChannelEntry.COLOUMN_NAME, newFeed.getChannel().getTitle());
        channelContentValues.put(DatabaseContract.ChannelEntry.COLOUMN_DESRIPTION, newFeed.getChannel().getDescription());
        database.insert(DatabaseContract.ChannelEntry.TABLE_NAME, null, channelContentValues);

        // insert the feed items
        for (FeedItem feedItem : newFeed.getChannel().getFeedItemList())
        {
            ContentValues feedContentValues = new ContentValues();
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_TITLE, feedItem.getTitle());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_DESRIPTION, feedItem.getDescription());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_LINK, feedItem.getLink());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_PUB_DATE, feedItem.getPubDate());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_URL, feedItem.getImage().getUrl());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_WIDTH, feedItem.getImage().getWidth());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_HEIGHT, feedItem.getImage().getHeight());
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_IS_READ, feedItem.isRead()? 1: 0);
            database.insert(DatabaseContract.FeedItemEntry.TABLE_NAME, null, feedContentValues);
        }

        // close the database
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    @Override
    public void markAsRead(List<FeedItem> feedItems)
    {
        // open the database
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();

        // insert the feed items
        for (FeedItem feedItem : feedItems)
        {
            ContentValues feedContentValues = new ContentValues();
            feedContentValues.put(DatabaseContract.FeedItemEntry.COLOUMN_IS_READ, 1);
            database.update(DatabaseContract.FeedItemEntry.TABLE_NAME,
                    feedContentValues,
                    DatabaseContract.FeedItemEntry.COLOUMN_TITLE + " =?",
                    new String[]{feedItem.getTitle()});
        }

        // close the database
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    /**
     * removes the content all both tables
     */
    public void clearTables()
    {
        // open the database
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // delete the tables
        database.delete(DatabaseContract.ChannelEntry.TABLE_NAME,
                null,
                null
        );
        database.delete(DatabaseContract.FeedItemEntry.TABLE_NAME,
                null,
                null
        );

        // close the database
        database.close();
    }

    /**
     * meta data for the table
     */
    private class DatabaseContract
    {
        final static String DATABASE_NAME = "ahmed.news.data";
        final static int DATABASE_VERSION = 4;

        class ChannelEntry
        {
            final static String TABLE_NAME = "Channel";
            final static String COLOUMN_NAME = "name";
            final static String COLOUMN_DESRIPTION = "description";
        }


        class FeedItemEntry
        {
            final static String TABLE_NAME = "Feed";
            final static String COLOUMN_TITLE = "title";
            final static String COLOUMN_DESRIPTION = "description";
            final static String COLOUMN_LINK = "link";
            final static String COLOUMN_IMAGE_URL = "image_url";
            final static String COLOUMN_IMAGE_WIDTH = "image_width";
            final static String COLOUMN_IMAGE_HEIGHT = "image_height";
            final static String COLOUMN_PUB_DATE = "pub_date";
            final static String COLOUMN_IS_READ = "is_read";

        }

    }

    /**
     * only handles creation and update of the tables
     */
    private class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            // create 2 tables, channel and feed
            String createChannelTable =
                    "CREATE TABLE " + DatabaseContract.ChannelEntry.TABLE_NAME + "(\n" +
                            DatabaseContract.ChannelEntry.COLOUMN_NAME + " TEXT PRIMARY KEY\n" +
                            ", " + DatabaseContract.ChannelEntry.COLOUMN_DESRIPTION + " TEXT \n" +
                            ", UNIQUE (" + DatabaseContract.ChannelEntry.COLOUMN_NAME + ") ON CONFLICT REPLACE)";
            String createFeedItemTable =
                    "CREATE TABLE " + DatabaseContract.FeedItemEntry.TABLE_NAME + "(\n" +
                            DatabaseContract.FeedItemEntry.COLOUMN_TITLE + " TEXT PRIMARY KEY\n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_DESRIPTION + " TEXT \n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_LINK + " TEXT \n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_URL + " TEXT \n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_WIDTH + " INTEGER \n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_IMAGE_HEIGHT + " INTEGER \n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_PUB_DATE + " TEXT \n" +
                            ", " + DatabaseContract.FeedItemEntry.COLOUMN_IS_READ+ " INTEGER \n" +
                            ", UNIQUE (" + DatabaseContract.FeedItemEntry.COLOUMN_TITLE + ") ON CONFLICT IGNORE)";
            sqLiteDatabase.execSQL(createChannelTable);
            sqLiteDatabase.execSQL(createFeedItemTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
        {
            // drop the tables and create them again(we don't mind losing the data"
            String dropChannel= "DROP TABLE IF EXISTS " + DatabaseContract.ChannelEntry.TABLE_NAME;
            String dropFeed= "DROP TABLE IF EXISTS " + DatabaseContract.FeedItemEntry.TABLE_NAME;
            sqLiteDatabase.execSQL(dropChannel);
            sqLiteDatabase.execSQL(dropFeed);
            onCreate(sqLiteDatabase);
        }
    }
}
