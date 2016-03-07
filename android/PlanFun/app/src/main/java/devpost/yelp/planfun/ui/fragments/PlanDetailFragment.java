package devpost.yelp.planfun.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.ui.events.EditPlanRequest;
import devpost.yelp.planfun.ui.fragments.ItemDetailFragment;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.PolylineModel;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.dialogs.ShareItineraryDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexb on 3/4/2016.
 */
public class PlanDetailFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    private RestClient mRestClient;
    private Plan currentPlan;
    private ItemDetailFragment mDetailFragment;
    private GoogleMap mGoogleMap;

    private Map<Marker, Item> marker_to_item;
    private List<Polyline> polylines;
    private Menu mMenu;
    private static String[] colors = {"red", "blue", "cyan", "green", "purple", "orange"};
    private final int ADD_ITINERARY = 94801;
    private final int REQUEST_LOCATION = 12;
    private MaterialDialog.Builder loadingProgressDialogBuilder;
    private MaterialDialog loadingProgressDialog;

    @Bind(R.id.edit_fab)
    FloatingActionButton mEditFab;

    public static PlanDetailFragment newInstance(int plan_id) {
        PlanDetailFragment fragment = new PlanDetailFragment();
        Bundle args = new Bundle();
        args.putInt("plan_id", plan_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan_detail, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        loadingProgressDialogBuilder = new MaterialDialog.Builder(getContext())
                .title("Loading")
                .content("Loading your plan...")
                .progress(true, 0);

        marker_to_item = new HashMap<>();
        polylines = new ArrayList<>();

        mDetailFragment = ItemDetailFragment.newInstance();

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.container, mDetailFragment)
                    .commit();
        }

        mEditFab.setOnClickListener(this);
        mRestClient = RestClient.getInstance();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle args = getArguments();
        int id = args.getInt("plan_id");
        loadingProgressDialog = loadingProgressDialogBuilder.show();
        Call<Plan> itineraryCall = mRestClient.getItineraryService().getItinerary(id, true);
        itineraryCall.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccess()) {
                    currentPlan = response.body();
                    if (mMenu != null) {
                        if (!currentPlan.getUser().getFacebook_id().equals(AccessToken.getCurrentAccessToken().getUserId())) {
                            PlanDetailFragment.this.getActivity().runOnUiThread(() -> {
                                mMenu.add(0, ADD_ITINERARY, 0, "Add to your itineraries").setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_plus).color(0xFFFFFF).actionBarSize());
                                mMenu.removeItem(R.id.action_share);
                                mMenu.removeItem(R.id.action_randomize);
                                mMenu.removeItem(R.id.action_refresh);
                            });
                        }
                    }
                    updateView();
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {

            }
        });
    }

    private void updateView()
    {
        getActivity().runOnUiThread(() -> {
            if (currentPlan == null || mGoogleMap == null)
                return; //wait for the other async method to call this

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPlan.getName());

            clearMap();
            zoomToLocation(currentPlan.getCity());

            Collections.sort(currentPlan.getItems(), (lhs, rhs) -> (int) (lhs.getStart_time().getTimeInMillis() - rhs.getStart_time().getTimeInMillis()));
            Collections.sort(currentPlan.getPolylines(), (lhs, rhs) -> lhs.getOrder() - rhs.getOrder());

            for (int j = 0; j < currentPlan.getItems().size(); j++) {
                Item i = currentPlan.getItems().get(j);
                String drawableName = "marker_" + colors[j % 6] + "_number_" + j;
                Bitmap b = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getActivity().getPackageName()));
                Bitmap scaled = Bitmap.createScaledBitmap(b, b.getWidth() * 3, b.getHeight() * 3, false);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .title(i.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(scaled))
                        .snippet(i.getLocation().getAddress())
                        .position(i.getLocation().getCoordinate()));

                marker_to_item.put(marker, i);
            }
            for (int j = 0; j < currentPlan.getPolylines().size(); j++) {
                PolylineModel polylineModel = currentPlan.getPolylines().get(j);
                final String color_str = "maps_" + colors[j];
                List<LatLng> points = PolyUtil.decode(polylineModel.getPolyline());
                PolylineOptions line = new PolylineOptions().geodesic(true);
                for (LatLng point : points) {
                    line.add(point);
                }
                line.color(getResources().getColor(getResources().getIdentifier(color_str, "color", getActivity().getPackageName())));
                polylines.add(mGoogleMap.addPolyline(line));
            }
            mDetailFragment.updateItem(currentPlan.getItems().get(0));
            if (loadingProgressDialog != null) {
                loadingProgressDialog.dismiss();
                loadingProgressDialog = null;
            }
        });
    }

    private void clearMap()
    {
        for (Marker m : marker_to_item.keySet()) {
            m.remove();
        }
        marker_to_item.clear();

        for (Polyline p : polylines) {
            p.remove();
        }
        polylines.clear();
    }

    private void zoomToLocation(String location)
        {
        if(mGoogleMap!=null) {
            Geocoder coder = new Geocoder(getContext());
            try {
                List<Address> addresses = coder.getFromLocationName(location, 1);
                LatLng city = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 10));
            } catch (IOException ioex) {
            }

        }
    }

    @Override
    public void onClick(View v)
    {
        PlanFunApplication.getBus().post(new EditPlanRequest(currentPlan.getId()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        updateView();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        marker.showInfoWindow();
        Item i = marker_to_item.get(marker);
        mDetailFragment.updateItem(i);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_itinerary_detail, menu);
        mMenu = menu;
        menu.findItem(R.id.action_share).setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_user_plus)
                .color(0xFFFFFF)
                .actionBarSize());
        menu.findItem(R.id.action_randomize).setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_magic)
                .color(0xFFFFFF)
                .actionBarSize());
        menu.findItem(R.id.action_refresh).setIcon(new IconDrawable(getContext(), Iconify.IconValue.fa_refresh)
                .color(0xFFFFFF)
                .actionBarSize());

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void randomize()
    {
        new MaterialDialog.Builder(getContext())
                .title("Confirm")
                .content("Randomizing your itinerary will delete all items you have added and regenerate a new set of items. Are you sure you want to do this?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive((dialog, which) -> {
                            final MaterialDialog progressDialog = new MaterialDialog.Builder(getContext())
                                    .title("Randomizing")
                                    .content("Regenerating your itinerary...")
                                    .progress(true, 0)
                                    .show();
                            Call<Plan> refreshCall = mRestClient.getItineraryService().randomizeItinerary(currentPlan.getId());
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

    private void refreshItinerary()
    {
        loadingProgressDialog = loadingProgressDialogBuilder.show();
        Call<Plan> getItineraryCall = mRestClient.getItineraryService().getItinerary(currentPlan.getId(), true);
        getItineraryCall.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                if (response.isSuccess()) {
                    currentPlan = response.body();
                    updateView();
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_share:
                ShareItineraryDialog shareDialog = new ShareItineraryDialog(currentPlan);
                shareDialog.show(this.getChildFragmentManager(), "fm");
                break;

            case R.id.action_randomize:
                randomize();
                break;

            case R.id.action_refresh:
                refreshItinerary();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}