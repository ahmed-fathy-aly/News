package ahmed.news.data;

import java.io.IOException;

import ahmed.news.entity.RSSFeed;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;

import static ahmed.news.Constants.BASE_URL;

/**
 * Downloads the news feed from the remote API(fixed to be bbc news feed but most other news feeds will work too)
 * Created by ahmed on 9/20/2016.
 */
public class FeedRemoteDataSourceImp implements FeedRemoteDataSource
{
    /* fields */
    private FeedAPI feedAPI;

    public FeedRemoteDataSourceImp()
    {
        // setup retrofit
        Retrofit retrofit =
                new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        feedAPI = retrofit.create(FeedAPI.class);
    }



    @Override
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
