package ahmed.news.UI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import ahmed.news.entity.Image;
import timber.log.Timber;

/**
 * scales the image view according to the drawable in it
 * MUST have the width fixed, and it will calculate the required height
 * Created by ahmed on 9/26/2016.
 */
public class FixedWidthImageView extends ImageView
{
    public FixedWidthImageView(Context context)
    {
        super(context);
    }

    public FixedWidthImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FixedWidthImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        final Drawable drawable = getDrawable();
        if (drawable != null)
        {
            // calculate and set the required height to match the drawable's scale
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = (int) Math.ceil(width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
