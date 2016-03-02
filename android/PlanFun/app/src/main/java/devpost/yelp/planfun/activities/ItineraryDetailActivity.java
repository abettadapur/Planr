package devpost.yelp.planfun.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.fragments.EditItineraryFragment;
import devpost.yelp.planfun.activities.fragments.ItemDetailFragment;
import devpost.yelp.planfun.activities.fragments.ItemListFragment;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.model.PolylineModel;
import devpost.yelp.planfun.net.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItineraryDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private Itinerary currentItinerary;
    private RestClient mRestClient;
    private ItemDetailFragment itemDetailFragment;
    private Map<Marker, Item> marker_to_item;
    private List<Polyline> polylines;
    private Menu mMenu;
    private static String[] colors = {"red", "blue", "cyan", "green", "purple", "orange"};
    private GoogleMap mGoogleMap;
    private final int ADD_ITINERARY = 94801;
    private final int REQUEST_LOCATION = 12;
    private MaterialDialog.Builder loadingProgressDialogBuilder;
    private MaterialDialog loadingProgressDialog;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_detail);
        ButterKnife.bind(this);

        loadingProgressDialogBuilder = new MaterialDialog.Builder(this)
                .title("Loading")
                .content("Loading your plan...")
                .progress(true, 0);

        marker_to_item = new HashMap<>();
        polylines = new ArrayList<>();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        itemDetailFragment = ItemDetailFragment.newInstance();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, itemDetailFragment)
                    .commit();
        }

        int id = getIntent().getIntExtra("itinerary_id", 0);
        mRestClient = RestClient.getInstance();

        loadingProgressDialog = loadingProgressDialogBuilder.show();
        Call<Itinerary> itineraryCall = mRestClient.getItineraryService().getItinerary(id, true);
        itineraryCall.enqueue(new Callback<Itinerary>() {
            @Override
            public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
                if (response.isSuccess()) {
                    currentItinerary = response.body();
                    if (mMenu != null) {
                        if (!currentItinerary.getUser().getFacebook_id().equals(AccessToken.getCurrentAccessToken().getUserId())) {
                            ItineraryDetailActivity.this.runOnUiThread(() -> {
                                mMenu.add(0, ADD_ITINERARY, 0, "Add to your itineraries").setIcon(new IconDrawable(ItineraryDetailActivity.this, Iconify.IconValue.fa_plus).color(0xFFFFFF).actionBarSize());
                                mMenu.removeItem(R.id.action_edit);
                                mMenu.removeItem(R.id.action_randomize);
                                mMenu.removeItem(R.id.action_refresh);
                            });
                        }
                    }
                    updateView();
                }
            }

            @Override
            public void onFailure(Call<Itinerary> call, Throwable t) {

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_itinerary_detail, menu);
        mMenu = menu;
        menu.findItem(R.id.action_edit).setIcon(new IconDrawable(this, Iconify.IconValue.fa_edit)
                .color(0xFFFFFF)
                .actionBarSize());
        menu.findItem(R.id.action_randomize).setIcon(new IconDrawable(this, Iconify.IconValue.fa_magic)
                .color(0xFFFFFF)
                .actionBarSize());
        menu.findItem(R.id.action_refresh).setIcon(new IconDrawable(this, Iconify.IconValue.fa_refresh)
                .color(0xFFFFFF)
                .actionBarSize());
        return true;
    }

    @Override
    //TODO(abettadapur): Update options menu for randomize
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;

            case R.id.action_edit:
                Intent i = new Intent(this, EditItineraryActivity.class);
                i.putExtra("itinerary_id", currentItinerary.getId());
                startActivity(i);
                break;

            case R.id.action_randomize:
                new MaterialDialog.Builder(this)
                        .title("Confirm")
                        .content("Randomizing your itinerary will delete all items you have added and regenerate a new set of items. Are you sure you want to do this?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive((dialog, which) -> {
                                    final MaterialDialog progressDialog = new MaterialDialog.Builder(ItineraryDetailActivity.this)
                                            .title("Randomizing")
                                            .content("Regenerating your itinerary...")
                                            .progress(true, 0)
                                            .show();
                                    Call<Itinerary> refreshCall = mRestClient.getItineraryService().randomizeItinerary(currentItinerary.getId());
                                    refreshCall.enqueue(new Callback<Itinerary>() {
                                        @Override
                                        public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
                                            if (response.isSuccess()) {
                                                progressDialog.dismiss();
                                                currentItinerary = response.body();
                                                updateView();
                                            } else {
                                                progressDialog.dismiss();
                                                //TODO(abettadapur): Show error
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Itinerary> call, Throwable t) {

                                        }
                                    });
                                }
                        )

                        .show();
                break;

            case R.id.action_refresh:
                loadingProgressDialog = loadingProgressDialogBuilder.show();
                Call<Itinerary> getItineraryCall = mRestClient.getItineraryService().getItinerary(currentItinerary.getId(), true);
                getItineraryCall.enqueue(new Callback<Itinerary>() {
                    @Override
                    public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
                        if (response.isSuccess()) {
                            currentItinerary = response.body();
                            updateView();
                        }
                    }

                    @Override
                    public void onFailure(Call<Itinerary> call, Throwable t) {

                    }
                });
                break;

            case ADD_ITINERARY:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        this.runOnUiThread(()-> {
            if (currentItinerary == null || mGoogleMap == null)
                return; //wait for the other async method to call this

            toolbar.setTitle(currentItinerary.getName());

            clearMap();
            zoomToLocation(currentItinerary.getCity());

            Collections.sort(currentItinerary.getItems(), (lhs, rhs) -> (int) (lhs.getStart_time().getTimeInMillis() - rhs.getStart_time().getTimeInMillis()));
            Collections.sort(currentItinerary.getPolylines(), (lhs, rhs) -> lhs.getOrder() - rhs.getOrder());

            for (int j = 0; j < currentItinerary.getItems().size(); j++) {
                Item i = currentItinerary.getItems().get(j);
                String drawableName = "marker_" + colors[j % 6] + "_number_" + j;
                Bitmap b = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
                Bitmap scaled = Bitmap.createScaledBitmap(b, b.getWidth() * 3, b.getHeight() * 3, false);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .title(i.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(scaled))
                        .snippet(i.getLocation().getAddress())
                        .position(i.getLocation().getCoordinate()));

                marker_to_item.put(marker, i);
            }
            for (int j = 0; j < currentItinerary.getPolylines().size(); j++) {
                PolylineModel polylineModel = currentItinerary.getPolylines().get(j);
                final String color_str = "maps_" + colors[j];
                List<LatLng> points = PolyUtil.decode(polylineModel.getPolyline());
                PolylineOptions line = new PolylineOptions().geodesic(true);
                for (LatLng point : points) {
                    line.add(point);
                }
                line.color(getResources().getColor(getResources().getIdentifier(color_str, "color", getPackageName())));
                polylines.add(mGoogleMap.addPolyline(line));
            }
            itemDetailFragment.updateItem(currentItinerary.getItems().get(0));
            if(loadingProgressDialog!=null)
            {
                loadingProgressDialog.dismiss();
                loadingProgressDialog = null;
            }
        });
    }

    private void clearMap() {
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
            Geocoder coder = new Geocoder(this);
            try {
                List<Address> addresses = coder.getFromLocationName(location, 1);
                LatLng city = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 10));
            } catch (IOException ioex) {
            }

        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        updateView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //TODO(abettadapur): check permission
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_LOCATION)
        {
            if(mGoogleMap!=null)
            {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, EditItineraryFragment.class);
        startActivity(i);
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        marker.showInfoWindow();
        Item i = marker_to_item.get(marker);
        itemDetailFragment.updateItem(i);
        return true;
    }

}
