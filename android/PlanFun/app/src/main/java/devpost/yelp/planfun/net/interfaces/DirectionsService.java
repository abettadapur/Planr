package devpost.yelp.planfun.net.interfaces;

import devpost.yelp.planfun.model.Directions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Alex on 3/30/2015.
 */
public interface DirectionsService
{
    @GET("maps/directions")
    Call<Directions> getDirections(@Query("origin") String origin, @Query("destination") String destination);

    @GET("maps/polyline")
    Call<String> getPolyline(@Query("origin") String origin, @Query("destination") String destination);
}
