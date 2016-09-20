package ahmed.news.entity;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Created by ahmed on 9/20/2016.
 */
@Root( strict = false)
public class Image
{
    @Attribute(name = "width")
    private int width;

    @Attribute(name = "height")
    private int height;

    @Attribute(name = "url")
    private String url;

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
