package devpost.yelp.planfun.activities.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.City;
import devpost.yelp.planfun.net.RestClient;
import retrofit2.Call;

/**
 * Created by alexb on 2/29/2016.
 */
public class CityAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<City> cityList = new ArrayList<City>();
    private RestClient mRestClient;

    public CityAutoCompleteAdapter(Context context) {
        mContext = context;
        mRestClient = RestClient.getInstance();
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public City getItem(int position) {
        return cityList.get(position);
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
        ((TextView)convertView.findViewById(android.R.id.text1)).setText(getItem(position).getName()+", "+getItem(position).getState());
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
                    try {
                        Call<List<City>> cityCall = mRestClient.getCityService().listCities(constraint.toString());
                        List<City> cities = cityCall.execute().body();
                        results.values = cities;
                        results.count = cities.size();
                    }
                    catch(IOException ioex)
                    {}
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0)
                {
                    cityList = (List<City>)results.values;
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
}
