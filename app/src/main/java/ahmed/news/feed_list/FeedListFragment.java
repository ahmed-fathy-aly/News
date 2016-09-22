package ahmed.news.feed_list;

import android.content.Context;
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

import java.util.List;

import ahmed.news.R;
import ahmed.news.entity.Feedtem;
import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * shows a list of the feed
 * clicking on a feed item will open an activity that shows the details of that feed item
 */
public class FeedListFragment extends Fragment implements FeedListContract.View, FeedAdapter.Listener, SwipeRefreshLayout.OnRefreshListener
{
    /* UI */
    @Bind(R.id.recycler_view_feed)
    RecyclerView mRecyclerViewFeed;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @Bind(R.id.view_container)
    FrameLayout mViewContainer;

    /* fields */
    private OnFragmentInteractionListener mListener;
    private FeedListContract.Presenter mPresenter;
    private FeedAdapter mAdapterFeed;

    public FeedListFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     */
    public static FeedListFragment newInstance()
    {
        FeedListFragment fragment = new FeedListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);
        ButterKnife.bind(this, view);

        // setup the presenter
        mPresenter = createPresenter();
        mPresenter.registerView(this);
        mPresenter.getFeed();

        // setup swipe to refresh
        mSwipeRefresh.setOnRefreshListener(this);

        // setup the recycler view
        mAdapterFeed = new FeedAdapter(getContext());
        mAdapterFeed.setListener(this);
        mRecyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewFeed.setAdapter(mAdapterFeed);

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.unregisterView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }


    /**
     * made public for testing(but the presenter is setup in the onCreateView)
     */
    public void setPresenter(FeedListPresenter presenter)
    {
        Timber.d("set presenter " + (mPresenter == null));
        if (mPresenter != null)
            mPresenter.unregisterView();
        mPresenter = presenter;
        mPresenter.registerView(this);
    }

    @Override
    public void onItemClick(int position, Feedtem feedItem, View view)
    {
        // TODO notify activity of the click
    }

    @Override
    public void showProgress()
    {
        mSwipeRefresh.post(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefresh.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideProgress()
    {
        mSwipeRefresh.post(new Runnable()
        {
            @Override
            public void run()
            {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void showError(String errorMessage)
    {
        Timber.d("error %s", errorMessage);
        Snackbar.make(mViewContainer, errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showFeedList(List<Feedtem> feedList)
    {
        mAdapterFeed.setData(feedList);
    }

    @Override
    public void showTitle(String title)
    {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onRefresh()
    {
        mPresenter.syncFeed();
    }

    /**
     * only used for making the fragment use a mock presenter
     */
    public FeedListContract.Presenter createPresenter()
    {
        return new FeedListPresenter();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener
    {
    }
}
