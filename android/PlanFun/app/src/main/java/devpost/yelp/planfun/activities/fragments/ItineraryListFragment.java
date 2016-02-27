package devpost.yelp.planfun.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.ItineraryDetailActivity;
import devpost.yelp.planfun.activities.adapters.ItineraryAdapter;
import devpost.yelp.planfun.activities.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.model.Itinerary;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link edu.gatech.daytripper.fragments.ItineraryListFragment.ItineraryListListener}
 * interface.
 */
public class ItineraryListFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recycle_view)
    RecyclerView mRecycleView;

    @Bind(R.id.progress_circle)
    ProgressBar mLoadingCircle;

    @Bind(R.id.add_fab)
    FloatingActionButton mAddButton;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ItineraryListListener mListListener;
    private List<Itinerary> mItineraryList;
    private ItineraryAdapter mAdapter;
    private int mContextIndex;
    private int layout, list_item;

    private final int ITINERARY_CREATE_CODE = 86;
    private final int CONTEXT_DELETE = 87;


    public static ItineraryListFragment newInstance(int layout, int list_item) {
        ItineraryListFragment fragment = new ItineraryListFragment();
        Bundle args = new Bundle();
        args.putInt("layout", layout);
        args.putInt("list_item", list_item);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItineraryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItineraryList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        layout = args.getInt("layout");
        list_item = args.getInt("list_item");

        View rootView = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, rootView);
        mSwipeRefresh.setOnRefreshListener(this);

        mAdapter = new ItineraryAdapter(mItineraryList, getActivity());
        mRecycleView.setAdapter(mAdapter);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));

        mAddButton.attachToRecyclerView(mRecycleView);
        mAddButton.setOnClickListener(this);

        registerForContextMenu(mRecycleView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListListener.refresh_list(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = null;

        if(context instanceof Activity)
            activity = (Activity)context;

        if(activity!=null) {
            try {
                mListListener = (ItineraryListListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListListener = null;
    }

    public void updateItems(List<Itinerary> items)
    {
        if(mItineraryList == null)
            mItineraryList = new ArrayList<>();

        mItineraryList.clear();
        mItineraryList.addAll(items);
        mAdapter.notifyDataSetChanged();

        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        Itinerary itinerary = mItineraryList.get(mContextIndex);
        menu.setHeaderTitle(itinerary.getName());
        menu.add(0, CONTEXT_DELETE, 0, "Delete Itinerary");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == CONTEXT_DELETE)
        {
            mListListener.remove_item(mItineraryList.get(mContextIndex).getId());
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onItemClick(View childView, int position)
    {
        int itinerary_id = mItineraryList.get(position).getId();
        Intent i = new Intent(getActivity(), ItineraryDetailActivity.class);
        i.putExtra("itinerary_id", itinerary_id);
        getActivity().startActivity(i);
    }

    @Override
    public void onItemLongPress(View childView, int position)
    {
        Vibrator vb = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        mContextIndex = position;
        getActivity().openContextMenu(childView);

    }

    /** Open a dialog to add a new itinerary. After collecting info, open edit itinerary activity **/
    @Override
    public void onClick(View v)
    {
        CreateItineraryDialog dialog = new CreateItineraryDialog();
        dialog.setTargetFragment(this, ITINERARY_CREATE_CODE);
        dialog.show(getActivity().getSupportFragmentManager(), "fm");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ITINERARY_CREATE_CODE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                //things

            }
        }
        this.getActivity().runOnUiThread(()-> mListListener.refresh_list(this));
    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(true);
        mListListener.refresh_list(this);
    }

    public void setLoading(boolean loading)
    {
        mRecycleView.setVisibility(loading?View.GONE:View.VISIBLE);
        mLoadingCircle.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ItineraryListListener
    {
        public void refresh_list(ItineraryListFragment fragment);
        public void remove_item(int id);

    }

}
