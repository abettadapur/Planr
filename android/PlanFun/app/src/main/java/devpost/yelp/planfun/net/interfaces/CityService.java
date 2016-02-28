package devpost.yelp.planfun.net.interfaces;

import android.support.annotation.CallSuper;

import java.util.List;

import devpost.yelp.planfun.model.City;
import devpost.yelp.planfun.model.YelpEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Andrey on 2/28/16.
 */
public interface CityService
{
    @GET("cities")
    Call<List<City>> listCities();

    @GET("cities")
    Call<List<City>> listCities(@Query("prefix") String prefix);
}

