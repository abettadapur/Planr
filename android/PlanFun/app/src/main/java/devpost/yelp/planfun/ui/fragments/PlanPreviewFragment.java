package devpost.yelp.planfun.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.net.RestClient;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mapFragment.getMapAsync(this);
        mItemFragment = ItemListFragment.newInstance(new ArrayList<>(currentPlan.getItems()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
