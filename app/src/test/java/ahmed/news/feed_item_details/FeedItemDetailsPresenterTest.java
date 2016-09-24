package ahmed.news.feed_item_details;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URLDecoder;

import ahmed.news.entity.FeedItem;
import ahmed.news.entity.Image;

/**
 * mocks the view and sets a dummy feed item to the presenter to test it
 * Created by ahmed on 9/24/2016.
 */
public class FeedItemDetailsPresenterTest
{
    /* constant mock data*/
    final static String TITLE = "Title 1";
    final static String IMAGE = "imigur/a.jpg";
    final static String DESCRIPTION = "Description bla bla bla bla bla bla bla bla bla ";
    final static String DATE = "Sat, 24 Sep 2016 00:33:31 GMT";
    final static String URL = "myFeed.com";
    final static String URI = "http:myFeed.com";
    static FeedItem FEED_ITEM;

    static
    {
        FEED_ITEM = new FeedItem();
        FEED_ITEM.setTitle(TITLE);
        FEED_ITEM.setDescription(DESCRIPTION);
        FEED_ITEM.setImage(new Image(IMAGE));
        FEED_ITEM.setPubDate(DATE);
        FEED_ITEM.setLink(URL);
    }

    @Mock
    private FeedItemDetailsContract.View mView;

    @InjectMocks
    private FeedItemDetailsPresenter mPresenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup()
    {
        mPresenter.register(mView);
        mPresenter.setFeedItem(FEED_ITEM);
    }

    @After
    public void teardDown()
    {
        mPresenter.unregister();
    }

    /**
     * tests presenting an item's details
     */
    @Test
    public void testShowFeedDetails()
    {
        mPresenter.showFeedDetails();

        Mockito.verify(mView).showTitle(TITLE);
        Mockito.verify(mView).showDescription(DESCRIPTION);
        Mockito.verify(mView).showUrlString(URL);
        Mockito.verify(mView).showImage(IMAGE);
        Mockito.verify(mView).showDate(DATE);
    }

    /**
     * tests presenting the URI correctly
     */
    @Test
    public void testOnLinkClicked()
    {
        mPresenter.onLinkClicked();

        Mockito.verify(mView).openUri(URI);
    }
}