package ahmed.news.data;

import java.io.IOException;

import ahmed.news.entity.RSSFeed;

/**
 * Created by ahmed on 9/25/2016.
 */
public interface FeedRemoteDataSource
{
    /**
     * Synchronous
     * @return the downloaded Feed
     * @throws IOException
     */
    public RSSFeed getFeed() throws IOException;

}
