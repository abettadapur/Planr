package devpost.yelp.planfun.ui.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.net.requests.GeneratePlanRequest;
import devpost.yelp.planfun.ui.events.ItemDetailRequest;
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexb on 3/10/2016.
 */
public class PlanPreviewFragment extends PlanDetailFragment {

    public static PlanPreviewFragment newInstance(Plan plan)
    {
        Bundle args = new Bundle();
        args.putParcelable("plan", plan);
        PlanPreviewFragment fragment = new PlanPreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        currentPlan = args.getParcelable("plan");
        inPreviewMode = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mapFragment.getMapAsync(this);
        mItemFragment = ItemListFragment.newInstance(new ArrayList<>(currentPlan.getItems()));
        mEditFab.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_randomize:
                randomize();
                break;
            case R.id.action_save:
                savePlan();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void updateView()
    {
        super.updateView();
        getActivity().runOnUiThread(()->((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPlan.getName()+" [PREVIEW]"));
    }

    private void randomize()
    {
        new MaterialDialog.Builder(getContext())
                .title("Confirm")
                .content("Are you sure you want to randomize?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive((dialog, which) -> {
                            final MaterialDialog progressDialog = new MaterialDialog.Builder(getContext())
                                    .title("Randomizing")
                                    .content("Regenerating your plan...")
                                    .progress(true, 0)
                                    .show();
                            List<YelpCategory> categories = new ArrayList<YelpCategory>();
                            for(Item i: currentPlan.getItems())
                            {
                                categories.add(i.getYelp_category());
                            }
                            Call<Plan> refreshCall = mRestClient.getItineraryService().generateItinerary(new GeneratePlanRequest(categories, currentPlan));
                            refreshCall.enqueue(new Callback<Plan>() {
                                @Override
                                public void onResponse(Call<Plan> call, Response<Plan> response) {
                                    if (response.isSuccess()) {
                                        progressDialog.dismiss();
                                        currentPlan = response.body();
                                        updateView();
                                    } else {
                                        progressDialog.dismiss();
                                        //TODO(abettadapur): Show error
                                    }
                                }

                                @Override
                                public void onFailure(Call<Plan> call, Throwable t) {

                                }
                            });
                        }
                )

                .show();
    }

    public void savePlan() {
        Dialog fragment = new MaterialDialog.Builder(getContext())
                .title("Saving")
                .content("Saving your plan...")
                .progress(true, 0)
                .build();

        fragment.show();

        Call<Plan> planCall = mRestClient.getItineraryService().createItinerary(currentPlan);
        planCall.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if(response.isSuccess())
                {
                    PlanFunApplication.getBus().post(new OpenPlanRequest(response.body().getId(), true));

                }
                fragment.dismiss();
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {

            }
        });
    }

    @Override
    @Subscribe
    public void onItemDetailFromList(ItemDetailRequest request)
    {
        super.onItemDetailFromList(request);
    }
}