package ahmed.news.data;

import java.io.IOException;

import ahmed.news.entity.RSSFeed;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;

/**
 * Downloads the news feed from the remote API(fixed to be bbc news feed but most other news feeds will work too)
 * Created by ahmed on 9/20/2016.
 */
public class FeedRemoteDataSource
{
    /* constants */
    public static final String BASE_URL = "http://feeds.bbci.co.uk/";

    /* fields */
    private FeedAPI feedAPI;

    public FeedRemoteDataSource()
    {
        // setup retrofit
        Retrofit retrofit =
                new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        feedAPI = retrofit.create(FeedAPI.class);
    }


    /**
     * Synchronous
     * @return the downloaded Feed
     * @throws IOException
     */
    public RSSFeed getFeed() throws IOException
    {
        return feedAPI.getFeed().execute().body();
    }

    private interface FeedAPI
    {
        @GET("news/rss.xml")
        Call<RSSFeed> getFeed();
    }
}
