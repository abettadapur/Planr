package devpost.yelp.planfun.ui.fragments;

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
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.ItineraryAdapter;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.ui.dialogs.CreateItineraryDialog;
import devpost.yelp.planfun.ui.events.OpenItineraryRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private List<Itinerary> mItineraryList;
    private ItineraryAdapter mAdapter;
    private RestClient mRestClient;

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
        mRestClient = RestClient.getInstance();
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
        refreshList();
    }

    private void refreshList()
    {
        setLoading(true);
        Call<List<Itinerary>> getItinerariesCall = mRestClient.getItineraryService().listItineraries(true);
        getItinerariesCall.enqueue(new Callback<List<Itinerary>>() {
            @Override
            public void onResponse(Call<List<Itinerary>> call, Response<List<Itinerary>> response) {
                if (response.isSuccess()) {
                    ItineraryListFragment.this.getActivity().runOnUiThread(() -> {
                        updateItems(response.body());
                        setLoading(false);
                    });
                } else {
                    try {
                        Log.e("GET ITINERARIES", response.errorBody().string());
                    } catch (IOException ioex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Itinerary>> call, Throwable t) {

            }
        });
    }

    public void updateItems(List<Itinerary> items)
    {
        if(mItineraryList == null)
            mItineraryList = new ArrayList<>();

        mItineraryList.clear();
        mItineraryList.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    public void removeItem(int id)
    {
        Call<ResponseBody> deleteCall = mRestClient.getItineraryService().deleteItinerary(id);
        deleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    refreshList();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("DELETE", "Error");
            }
        });
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
            removeItem(mItineraryList.get(mContextIndex).getId());
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(View childView, int position)
    {
        int itinerary_id = mItineraryList.get(position).getId();
        PlanFunApplication.getBus().post(new OpenItineraryRequest(itinerary_id));
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
        getActivity().runOnUiThread(this::refreshList);
    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(true);
        refreshList();
    }

    public void setLoading(boolean loading) {
        getActivity().runOnUiThread(() -> {
            mRecycleView.setVisibility(loading ? View.GONE : View.VISIBLE);
            mLoadingCircle.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }
}
