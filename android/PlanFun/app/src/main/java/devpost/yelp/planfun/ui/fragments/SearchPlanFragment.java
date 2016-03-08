package devpost.yelp.planfun.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Plan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * interface.
 */
public class SearchPlanFragment extends PlanListFragment implements  SearchView.OnQueryTextListener
{

    public static SearchPlanFragment newInstance(int layout, int list_item) {
        SearchPlanFragment fragment = new SearchPlanFragment();
        Bundle args = new Bundle();
        args.putInt("layout", layout);
        args.putInt("list_item", list_item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        refreshOnStart = false;
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.search).setIcon(MaterialDrawableBuilder.with(getContext())
                .setColor(Color.WHITE)
                .setToActionbarSize()
                .setIcon(MaterialDrawableBuilder.IconValue.MAGNIFY)
                .build());

        SearchView view = (SearchView)menu.findItem(R.id.search).getActionView();
        if(view!=null)
        {
            view.setOnQueryTextListener(this);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(!query.equals("")) {
            setLoading(true);
            Call<List<Plan>> itineraryCall = mRestClient.getItineraryService().searchItinerary(query);
            itineraryCall.enqueue(new Callback<List<Plan>>() {
                @Override
                public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                    if(response.isSuccess())
                    {
                        getActivity().runOnUiThread(() -> {
                            updateItems(response.body());
                            setLoading(false);
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<Plan>> call, Throwable t) {

                }
            });
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
