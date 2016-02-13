package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.Itinerary;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Alex on 3/7/2015.
 */
public interface ItineraryService
{
    @POST("/itinerary/create")
    public void createItinerary(@Body Itinerary itinerary, @Query("token") String token, Callback<Itinerary> callback);

    @GET("/itinerary/{id}")
    public void getItinerary(@Path("id") int id, @Query("token") String token, Callback<Itinerary> callback);

    @GET("/itinerary/list")
    public void listItineraries(@Query("token") String token, Callback<List<Itinerary>> callback);

    @PUT("/itinerary/{id}")
    public void updateItinerary(@Path("id") int id, @Body Itinerary itinerary, @Query("token") String token, Callback<Itinerary> callback);

    @DELETE("/itinerary/{id}")
    public void deleteItinerary(@Path("id") int id, @Query("token") String token, Callback<Boolean> callback);

    @GET("/itinerary/search")
    public void searchItinerary(@Query("query") String query, @Query("token") String token, Callback<List<Itinerary>> callback);

    @GET("/itinerary/{id}/randomize")
    public void randomizeItinerary(@Path("id") int id, @Query("token") String token, Callback<Itinerary> callback);

    @POST("/itinerary/{id}/copy")
    public void copyItinerary(@Path("id") int id, @Query("token") String token, Callback<Itinerary> callback);
}
