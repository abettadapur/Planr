package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.location.Location;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import devpost.yelp.planfun.model.City;
import devpost.yelp.planfun.net.RestClient;
import retrofit2.Call;

/**
 * Created by alexb on 2/29/2016.
 */
public class CityAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<AutocompletePrediction> placesList = new ArrayList<>();
    private RestClient mRestClient;
    private GoogleApiClient mGoogleApiClient;

    public CityAutoCompleteAdapter(Context context, GoogleApiClient client) {
        mContext = context;
        mRestClient = RestClient.getInstance();
        mGoogleApiClient = client;
    }

    @Override
    public int getCount() {
        return placesList.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        ((TextView)convertView.findViewById(android.R.id.text1)).setText(getItem(position).getFullText(null));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint!=null)
                {
                    Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LatLngBounds bounds = toBounds(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 5000);
                    AutocompletePredictionBuffer places = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint.toString(), bounds, new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build()).await();
                    results.values = places;
                    results.count = places.getCount();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0)
                {
                    placesList.clear();
                    for(AutocompletePrediction place: (AutocompletePredictionBuffer)results.values)
                    {
                        placesList.add(place);
                    }
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }
}
