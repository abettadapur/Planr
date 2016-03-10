package devpost.yelp.planfun.ui.fragments;

import android.content.Context;
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


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.ItineraryAdapter;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.ui.events.EditPlanRequest;
import devpost.yelp.planfun.ui.events.FindPlanRequest;
import devpost.yelp.planfun.ui.events.GeneratePlanRequest;
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * interface.
 */
public class PlanListFragment extends BaseFragment implements RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recycle_view)
    RecyclerView mRecycleView;

    @Bind(R.id.progress_circle)
    ProgressBar mLoadingCircle;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    private List<Plan> mPlanList;
    private ItineraryAdapter mAdapter;
    protected RestClient mRestClient;
    protected boolean refreshOnStart = true;

    private int mContextIndex;
    private int layout, list_item;

    private final int ITINERARY_CREATE_CODE = 86;
    private final int CONTEXT_DELETE = 87;

    @Bind(R.id.add_fab)
    FloatingActionMenu mAddMenu;

    @Bind(R.id.create_fab)
    FloatingActionButton mCreateFab;
    @Bind(R.id.find_fab)
    FloatingActionButton mFindFab;
    @Bind(R.id.gen_fab)
    FloatingActionButton mGenFab;

    public static PlanListFragment newInstance(int layout, int list_item) {
        PlanListFragment fragment = new PlanListFragment();
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
    public PlanListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlanList = new ArrayList<>();
        mRestClient = RestClient.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        layout = args.getInt("layout");
        list_item = args.getInt("list_item");

        View rootView = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, rootView);
        mSwipeRefresh.setOnRefreshListener(this);

        mAdapter = new ItineraryAdapter(mPlanList, getActivity());
        mRecycleView.setAdapter(mAdapter);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        registerForContextMenu(mRecycleView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(refreshOnStart)
            refreshList();
    }

    private void refreshList()
    {
        setLoading(true);
        Call<List<Plan>> getItinerariesCall = mRestClient.getItineraryService().listItineraries(true);
        getItinerariesCall.enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                if (response.isSuccess()) {
                    PlanListFragment.this.getActivity().runOnUiThread(() -> {
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
            public void onFailure(Call<List<Plan>> call, Throwable t) {

            }
        });
    }

    public void updateItems(List<Plan> items)
    {
        if(mPlanList == null)
            mPlanList = new ArrayList<>();

        mPlanList.clear();
        mPlanList.addAll(items);
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
        Plan plan = mPlanList.get(mContextIndex);
        menu.setHeaderTitle(plan.getName());
        menu.add(0, CONTEXT_DELETE, 0, "Delete Plan");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == CONTEXT_DELETE)
        {
            removeItem(mPlanList.get(mContextIndex).getId());
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(View childView, int position)
    {
        int itinerary_id = mPlanList.get(position).getId();
        PlanFunApplication.getBus().post(new OpenPlanRequest(itinerary_id, false));
    }

    @Override
    public void onItemLongPress(View childView, int position)
    {
        Vibrator vb = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        mContextIndex = position;
        getActivity().openContextMenu(childView);
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

    @OnClick(R.id.find_fab)
    public void onFindClick(View button){
        PlanFunApplication.getBus().post(new FindPlanRequest());
    }

    @OnClick(R.id.create_fab)
    public void onCreateClick(View button){
        PlanFunApplication.getBus().post(new EditPlanRequest(true));
    }

    @OnClick(R.id.gen_fab)
    public void onGenClick(View button){
        PlanFunApplication.getBus().post(new GeneratePlanRequest());
    }

    
}
