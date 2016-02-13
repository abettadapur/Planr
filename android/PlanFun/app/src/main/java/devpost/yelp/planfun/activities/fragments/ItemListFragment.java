package devpost.yelp.planfun.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.AddItemActivity;
import devpost.yelp.planfun.activities.adapters.ItemAdapter;
import devpost.yelp.planfun.activities.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.model.Itinerary;


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
    private Itinerary mCurrentItinerary;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        mRecycleView = (RecyclerView)rootView.findViewById(R.id.recycle_view);

        mAdapter = new ItemAdapter(mCurrentItinerary.getItems(), getActivity());
        mRecycleView.setAdapter(mAdapter);

        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));

        mFab = (FloatingActionButton)rootView.findViewById(R.id.add_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItemListFragment.this.getActivity(), AddItemActivity.class);
                i.putExtra("itinerary_id", mCurrentItinerary.getId());
                getActivity().startActivity(i);
            }
        });


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



    public void setItinerary(Itinerary itinerary)
    {

        mCurrentItinerary = itinerary;
    }

    public void setItineraryAndUpdate(Itinerary itinerary)
    {
        mCurrentItinerary = itinerary;
        mAdapter = new ItemAdapter(mCurrentItinerary.getItems(), getActivity());
        mRecycleView.setAdapter(mAdapter);
    }


    @Override
    public void onItemClick(View childView, int position) {
    }

    @Override
    public void onItemLongPress(View childView, int position) {

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


}
