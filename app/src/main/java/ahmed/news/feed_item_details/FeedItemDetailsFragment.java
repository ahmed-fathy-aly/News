package ahmed.news.feed_item_details;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import javax.inject.Inject;

import ahmed.news.App;
import ahmed.news.R;
import ahmed.news.data.DateUtils;
import ahmed.news.entity.FeedItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * shows the details of a single feed item
 */
public class FeedItemDetailsFragment extends Fragment implements FeedItemDetailsContract.View
{
    /* constants */
    private static final String ARG_FEED_ITEM = "extraFeedItem";

    /* UI */
    @Bind(R.id.text_view_title)
    TextView mTextViewTitle;
    @Bind(R.id.image_view_media)
    ImageView mImageViewMedia;
    @Bind(R.id.text_view_date)
    TextView mTextViewDate;
    @Bind(R.id.text_view_description)
    TextView mTextViewDescription;
    @Bind(R.id.text_view_link)
    TextView mTextViewLink;

    /* fields */
    @Inject
    FeedItemDetailsContract.Presenter mPresenter;

    /**
     * only use this to instantiate this fragment
     *
     * @param feedItem the item to be displayed
     */
    public static Fragment newInstance(FeedItem feedItem)
    {
        FeedItemDetailsFragment fragment = new FeedItemDetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_FEED_ITEM, feedItem);
        fragment.setArguments(arguments);

        return fragment;
    }

    public FeedItemDetailsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // setup the presenter
        App app = (App) getActivity().getApplication();
        app.getComponent().inject(this);
        FeedItem feedItem = (FeedItem) getArguments().getSerializable(ARG_FEED_ITEM);
        mPresenter.setFeedItem(feedItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_item_details, container, false);
        ButterKnife.bind(this, view);

        mPresenter.register(this);
        mPresenter.showFeedDetails();

        return view;
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.unregister();
    }

    @Override
    public void showImage(String imageUrl)
    {
        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder_small)
                .into(mImageViewMedia);

    }

    @Override
    public void showTitle(String title)
    {
        mTextViewTitle.setText(title);
    }

    @Override
    public void showDescription(String description)
    {
        mTextViewDescription.setText(description);
    }

    @Override
    public void showDate(Calendar calendar)
    {
        // show the relative date
        String dateStr = DateUtils.getReadableData(getContext(), calendar);
        mTextViewDate.setText(dateStr);
    }

    @Override
    public void openUri(String uri)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(intent);
    }

    @Override
    public void showUrlString(String urlSting)
    {
        mTextViewLink.setText(urlSting);
    }

    @OnClick(R.id.text_view_link)
    public void onLinkClicked()
    {
        mPresenter.onLinkClicked();
    }
}
