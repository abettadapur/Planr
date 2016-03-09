package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.ui.activities.AddItemActivity;
import devpost.yelp.planfun.ui.adapters.ItemAdapter;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.ui.events.EditItemRequest;
import devpost.yelp.planfun.ui.events.EditPlanRequest;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemListFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener{

    private RecyclerView mRecycleView;
    private ItemAdapter mAdapter;
    private Plan mCurrentPlan;

    private FloatingActionButton mFab;


    public static ItemListFragment newInstance() {
        ItemListFragment fragment = new ItemListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        mRecycleView = (RecyclerView)rootView.findViewById(R.id.recycle_view);

        mAdapter = new ItemAdapter(mCurrentPlan.getItems(), getActivity());
        mRecycleView.setAdapter(mAdapter);

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));

        mFab = (FloatingActionButton)rootView.findViewById(R.id.add_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItemListFragment.this.getActivity(), AddItemActivity.class);
                i.putExtra("plan_id", mCurrentPlan.getId());
                getActivity().startActivity(i);
            }
        });


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



    public void setItinerary(Plan plan)
    {

        mCurrentPlan = plan;
    }

    public void setItineraryAndUpdate(Plan plan)
    {
        mCurrentPlan = plan;
        mAdapter = new ItemAdapter(mCurrentPlan.getItems(), getActivity());
        mRecycleView.setAdapter(mAdapter);
    }


    @Override
    public void onItemClick(View childView, int position) {
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }



    @Subscribe
    public void onEditItemRequest(EditItemRequest request)
    {
        EditItemFragment fragment;

        if(!request.new_plan)
            fragment = EditPlanFragment.newInstance(request.plan_id);
        else
            fragment = EditPlanFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }

}
