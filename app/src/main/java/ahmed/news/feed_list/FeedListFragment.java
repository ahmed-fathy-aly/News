package ahmed.news.feed_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.logging.Handler;

import javax.inject.Inject;

import ahmed.news.App;
import ahmed.news.R;
import ahmed.news.UI.DividerItemDecoration;
import ahmed.news.data.SyncFeedService;
import ahmed.news.data.SyncTaskScheduler;
import ahmed.news.entity.FeedItem;
import ahmed.news.event.FeedUpdatedEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * shows a list of the feed
 * clicking on a feed item will open an activity that shows the details of that feed item
 */
public class FeedListFragment extends Fragment implements FeedListContract.View, FeedAdapter.Listener, SwipeRefreshLayout.OnRefreshListener {
    /* UI */
    @Bind(R.id.recycler_view_feed)
    RecyclerView mRecyclerViewFeed;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.view_container)
    FrameLayout mViewContainer;

    /* fields */
    private OnFragmentInteractionListener mListener;
    @Inject
    FeedListContract.Presenter mPresenter;
    private FeedAdapter mAdapterFeed;
    private int mUpdatedIdx;

    public FeedListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     */
    public static FeedListFragment newInstance() {
        FeedListFragment fragment = new FeedListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        renderUpdatedIdx();
    }

    /**
     * when an item is marked as read, the adapter won't reRender until this is called
     */
    public void renderUpdatedIdx()
    {
            mRecyclerViewFeed.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mUpdatedIdx != -1)
                    {
                        mAdapterFeed.notifyItemChanged(mUpdatedIdx);
                        mUpdatedIdx = -1;
                    }
                }
            }, 700);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        ButterKnife.bind(this, view);

        // setup the presenter
        App app = (App) getActivity().getApplication();
        app.getComponent().inject(FeedListFragment.this);
        mPresenter.registerView(FeedListFragment.this);
        EventBus.getDefault().register(this);

        // setup swipe to refresh
        mSwipeRefresh.setOnRefreshListener(this);

        // setup the recycler view
        mAdapterFeed = new FeedAdapter(getContext());
        mAdapterFeed.setListener(this);
        mRecyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        mRecyclerViewFeed.addItemDecoration(new DividerItemDecoration(getContext().getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerViewFeed.setAdapter(mAdapterFeed);

        mPresenter.getFeed();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.unregisterView();
        mAdapterFeed.clearContext();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * made public for testing(but the presenter is setup in the onCreateView)
     */
    public void setPresenter(FeedListPresenter presenter) {
        if (mPresenter != null)
            mPresenter.unregisterView();
        mPresenter = presenter;
        mPresenter.registerView(this);
    }

    @Override
    public void onItemClick(int position, FeedItem feedItem, View view,
                            TextView textViewTitle, ImageView imageViewThumbnail) {
        mPresenter.onFeedClicked(feedItem);
        if (mListener != null)
            mListener.onFeedItemClicked(feedItem, view, textViewTitle, imageViewThumbnail);
    }

    @Override
    public void showProgress() {
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideProgress() {
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void showError(String errorMessage) {
        Timber.d("error %s", errorMessage);
        Snackbar.make(mViewContainer, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showFeedList(List<FeedItem> feedList) {
        mAdapterFeed.setData(feedList);
    }

    @Override
    public void showTitle(String title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(title);
    }


    @Override
    public void markAsRead(FeedItem feedItem) {

        // check the item is not already read
        int idx = mAdapterFeed.getIdx(feedItem.getTitle());
        if (idx == -1)
            return;
        boolean alreadyRead = mAdapterFeed.getItem(idx).isRead();
        if (alreadyRead)
            return;

        // mark it as read in the adapter and store is index to later reRender it
        mAdapterFeed.markAsRead(feedItem.getTitle());
        mUpdatedIdx = idx;
    }


    @Override
    public void onRefresh() {
        mPresenter.syncFeed();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFeedItemClicked(FeedItem feedItem, View view,
                               TextView textViewTitle, ImageView imageViewThumbnail);
    }

    @Subscribe
    public void onEvent(FeedUpdatedEvent feedUpdatedEvent) {
        mPresenter.getFeed();
    }
}
