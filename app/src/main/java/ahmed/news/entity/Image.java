package ahmed.news.entity;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by ahmed on 9/20/2016.
 */
@Root( strict = false)
public class Image implements Serializable
{
    @Attribute(name = "width")
    private int width;

    @Attribute(name = "height")
    private int height;

    @Attribute(name = "url")
    private String url;

    public Image()
    {
    }

    public Image(int width, int height, String url)
    {
        this.width = width;
        this.height = height;
        this.url = url;
    }

    public Image(String url)
    {
        this.url = url;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

}
