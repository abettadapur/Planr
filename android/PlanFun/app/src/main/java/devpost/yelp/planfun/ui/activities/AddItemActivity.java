package devpost.yelp.planfun.ui.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.IconTextView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.android.iconify.Iconify;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.model.YelpEntry;
import devpost.yelp.planfun.net.RestClient;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 4/2/2015.
 */
public class AddItemActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Toolbar mToolbar;
    private EditText mStartTimeView, mEndTimeView, mCategoryView;

    private TextView mArrowLabel;
    private GoogleMap mGoogleMap;
    private FButton mCallButton, mNavButton, mWebButton, mAddButton;

    //ITEM DETAIL VIEWS
    private TextView mTitleView, mReviewCountView;
    private RatingBar mRatingView;
    private IconTextView mIconView;

    private Itinerary mCurrentItinerary;
    private Item mCurrentItem;

    private RestClient mRestClient;

    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;

    private String[] categories = {"breakfast", "lunch", "attraction", "dinner", "nightlife"};

    private Map<Marker, YelpEntry> markerToItem;
    private Marker prevItemMarker;
    private Item prevItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mStartTimeView = (EditText)findViewById(R.id.startPicker);
        mEndTimeView = (EditText)findViewById(R.id.endPicker);
        mCategoryView = (EditText) findViewById(R.id.categoryView);
        mArrowLabel = (TextView)findViewById(R.id.arrowLabel);
        mTitleView = (TextView)findViewById(R.id.titleView);
        mCallButton = (FButton)findViewById(R.id.callButton);
        mNavButton = (FButton)findViewById(R.id.navButton);
        mWebButton = (FButton)findViewById(R.id.webButton);
        mAddButton = (FButton)findViewById(R.id.addButton);

        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + mCurrentItem.getYelp_item().getLocation().getCoordinate().latitude + "," + mCurrentItem.getYelp_item().getLocation().getCoordinate().longitude));
                startActivity(intent);
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mCurrentItem.getYelp_item().getPhone()));
                startActivity(intent);
            }
        });

        mWebButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mCurrentItem.getYelp_item().getUrl()));
                startActivity(browserIntent);
            }
        });

        mReviewCountView = (TextView)findViewById(R.id.ratingCountView);
        mRatingView = (RatingBar)findViewById(R.id.ratingView);
        mIconView = (IconTextView)findViewById(R.id.iconView);

        Iconify.addIcons(mArrowLabel, mCallButton, mNavButton, mAddButton, mWebButton);

        mToolbar.setTitle("Add an Item");
        setSupportActionBar(mToolbar);

        mCurrentItem = new Item();

        timeSdf = new SimpleDateFormat(timeFormat);

        markerToItem = new HashMap<>();

        mRestClient = RestClient.getInstance();


//        Call<Itinerary> getItineraryCall = mRestClient.getItineraryService().getItinerary(getIntent().getIntExtra("itinerary_id", 0));
//        getItineraryCall.enqueue(new Callback<Itinerary>() {
//            @Override
//            public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
//                if(response.isSuccess())
//                {
//                    mCurrentItinerary = response.body();
//                    Calendar startTime = Calendar.getInstance();
//                    startTime.set(Calendar.HOUR_OF_DAY, 10);
//                    startTime.set(Calendar.MINUTE, 0);
//
//                    Calendar endTime = Calendar.getInstance();
//                    endTime.set(Calendar.HOUR_OF_DAY, 11);
//                    endTime.set(Calendar.MINUTE, 0);
//
//                    mCurrentItem.setStart_time(startTime);
//                    mCurrentItem.setEnd_time(endTime);
//                    mCurrentItem.setYelp_category("breakfast");
//
//                    updateView();
//
//                    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//                    mapFragment.getMapAsync(AddItemActivity.this);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Itinerary> call, Throwable t) {
//
//            }
//        });

//        mCategoryView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(AddItemActivity.this)
//                        .title("Cities")
//                        .items(categories)
//                        .positiveText("Ok")
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                mCurrentItem.setYelp_category(text.toString());
//                                updateView();
//                            }
//                        })
//                        .show();
//            }
//        });


        mStartTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddItemActivity.this, startTimeSetListener, mCurrentItem.getStart_time().get(Calendar.HOUR_OF_DAY), mCurrentItem.getStart_time().get(Calendar.MINUTE), true).show();
            }
        });

        mEndTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddItemActivity.this, endTimeSetListener, mCurrentItem.getEnd_time().get(Calendar.HOUR_OF_DAY), mCurrentItem.getEnd_time().get(Calendar.MINUTE), true).show();
            }
        });

    }

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentItem.getStart_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentItem.getStart_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentItem.getEnd_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentItem.getEnd_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    private void updateView()
    {
        mStartTimeView.setText(timeSdf.format(mCurrentItem.getStart_time().getTime()));
        mEndTimeView.setText(timeSdf.format(mCurrentItem.getEnd_time().getTime()));
        mCategoryView.setText(Character.toUpperCase(mCurrentItem.getYelp_category().getName().charAt(0))+mCurrentItem.getYelp_category().getName().substring(1));
        updateMap();
    }

    private void updateMap()
    {
        if(mGoogleMap == null)
            return;

        //remove previous item
        if(prevItemMarker != null)
            prevItemMarker.remove();

        //search for items
        Call<List<YelpEntry>> getYelpItemCall = mRestClient.getCategoryService().searchCategory(mCurrentItem.getYelp_category(), mCurrentItinerary.getCity());
        getYelpItemCall.enqueue(new Callback<List<YelpEntry>>() {
            @Override
            public void onResponse(Call<List<YelpEntry>> call, Response<List<YelpEntry>> response) {
                if(response.isSuccess()) {
                    for (Marker m : markerToItem.keySet()) {
                        if (m != null)
                            m.remove();
                    }
                    markerToItem.clear();
                    if (prevItemMarker != null)
                        prevItemMarker.remove();

                    Item previousItem = null;
                    int prevIndex = -1;
                    for (int j = mCurrentItinerary.getItems().size() - 1; j >= 0; j--) {
                        Item i = mCurrentItinerary.getItems().get(j);
                        if (i.getStart_time().getTimeInMillis() - mCurrentItem.getStart_time().getTimeInMillis() < 0) {
                            previousItem = i;
                            prevIndex = j;
                        } else {
                            break;
                        }
                    }

                    prevItem = previousItem;
                    //place previous item
                    if (previousItem != null) {
                        String drawableName = "marker_blue_number_" + prevIndex;
                        Bitmap b = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
                        Bitmap scaled = Bitmap.createScaledBitmap(b, b.getWidth() * 3, b.getHeight() * 3, false);

                        prevItemMarker = mGoogleMap.addMarker(new MarkerOptions()
                                .title(previousItem.getName())
                                .snippet(previousItem.getYelp_item().getLocation().getAddress())
                                .icon(BitmapDescriptorFactory.fromBitmap(scaled))
                                .position(previousItem.getYelp_item().getLocation().getCoordinate()));
                    }

                    //add search results
                    for (YelpEntry ye : response.body()) {
                        if (previousItem != null) {
                            if (previousItem.getYelp_item_id().equals(ye.getId())) {
                                continue;
                            }
                        }

                        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                .title(ye.getName())
                                .snippet(ye.getLocation().getAddress())
                                .position(ye.getLocation().getCoordinate()));

                        markerToItem.put(marker, ye);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<YelpEntry>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        Geocoder coder = new Geocoder(this);
        try {
            List<Address> addresses = coder.getFromLocationName(mCurrentItinerary.getCity(), 1);
            LatLng city =  new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 10));
        }
        catch(IOException ioex)
        {}
        mGoogleMap.setMyLocationEnabled(true);
        updateMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        if(prevItem != null && marker.getTitle().equals(prevItem.getName()))
        {
            updateItemView(prevItem);
        }
        else {
            YelpEntry ye = markerToItem.get(marker);
            updateItemView(ye);
        }
        return true;
    }

    private void updateItemView(Item item)
    {
        mTitleView.setText(item.getName());
        mRatingView.setRating(item.getYelp_item().getRating());
        mReviewCountView.setText(" - " + item.getYelp_item().getReview_count() + " reviews");
        mAddButton.setVisibility(View.INVISIBLE);
       // mSubtitleView.setText(PhoneNumberUtils.formatNumber(item.getYelp_item().getPhone()));

        switch(item.getYelp_category().getName())
        {
            case "breakfast":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_coffee);
                break;
            case "lunch":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_cutlery);
                break;
            case "dinner":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_cutlery);
                break;
            case "nightlife":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_glass);
                break;
            case "attraction":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_futbol_o);
                break;
        }

    }

    private void updateItemView(final YelpEntry entry)
    {
        mTitleView.setText(entry.getName());
        mRatingView.setRating(entry.getRating());
        mReviewCountView.setText(" - " + entry.getReview_count() + " reviews");
        mAddButton.setVisibility(View.VISIBLE);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentItem.setName(entry.getName());
                final MaterialDialog progressDialog = new MaterialDialog.Builder(AddItemActivity.this).title("Adding").content("Adding this item to your itinerary").progress(true, 0).show();
                mCurrentItem.setYelp_item(entry);
                mCurrentItem.setYelp_item_id(entry.getId());
                Call<Item> getItemCall = mRestClient.getItemService().createItem(mCurrentItinerary.getId(), mCurrentItem);
                getItemCall.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccess()) {
                            progressDialog.dismiss();
                            finish();
                        } else {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {

                    }
                });
            }
        });
       // mSubtitleView.setText(PhoneNumberUtils.formatNumber(entry.getPhone()));

        switch(mCurrentItem.getYelp_category().getName())
        {
            case "breakfast":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_coffee);
                break;
            case "lunch":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_cutlery);
                break;
            case "dinner":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_cutlery);
                break;
            case "nightlife":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_glass);
                break;
            case "attraction":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_futbol_o);
                break;
        }
    }
}
