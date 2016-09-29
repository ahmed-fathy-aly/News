package ahmed.news.feed_list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ahmed.news.R;
import ahmed.news.entity.FeedItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

/**
 * binds a feed item to row_feed
 * Created by ahmed on 9/22/2016.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder>
{
    private Context mContext;
    private Listener mListener;
    private List<FeedItem> mData;

    public FeedAdapter(Context context)
    {
        mContext = context;
        mData = new ArrayList<>();
    }

    /**
     * registers to get callbacks when an item is clicked
     */
    public void setListener(Listener listener)
    {
        mListener = listener;
    }

    /**
     * replaces the data and updates the UI
     */
    public void setData(List<FeedItem> newData)
    {
        mData.clear();
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.row_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position)
    {
        FeedItem feedItem = mData.get(position);
        holder.textViewTitle.setText(feedItem.getTitle());
        Calendar calendar = feedItem.getCalendar();
        if (calendar != null)
        {
            String dateStr = android.text.format.DateUtils.getRelativeDateTimeString(mContext,
                    calendar.getTimeInMillis(), android.text.format.DateUtils.MINUTE_IN_MILLIS,
                    android.text.format.DateUtils.WEEK_IN_MILLIS, android.text.format.DateUtils.FORMAT_SHOW_TIME)
                    .toString();
            holder.textViewTime.setText(dateStr);
        }
        if (feedItem.getImage() != null && feedItem.getImage().getUrl().length() > 0)
            Picasso.with(mContext)
                    .load(feedItem.getImage().getUrl())
                    .placeholder(R.drawable.ic_placeholder_small)
                    .into(holder.imageViewThumbnial);
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    /**
     * holds a row_feed
     */
    class FeedViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.image_view_thumbnial)
        ImageView imageViewThumbnial;
        @Bind(R.id.text_view_title)
        TextView textViewTitle;
        @Bind(R.id.text_view_time)
        TextView textViewTime;

        public FeedViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new DebouncingOnClickListener()
            {
                @Override
                public void doClick(View v)
                {
                    if (mListener != null)
                    {
                        int position = getAdapterPosition();
                        FeedItem feedItem = mData.get(position);
                        mListener.onItemClick(position, feedItem, itemView, textViewTitle, imageViewThumbnial);
                    }
                }
            });
        }
    }


    public interface Listener
    {
        /**
         * called when an item in the list is clicked
         */
        void onItemClick(int position, FeedItem feedItem, View view, TextView textViewTitle, ImageView imageViewThumbnail);
    }
}
