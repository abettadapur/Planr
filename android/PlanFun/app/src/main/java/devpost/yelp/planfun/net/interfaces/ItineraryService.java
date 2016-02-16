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
    @POST("/itineraries")
    public void createItinerary(@Body Itinerary itinerary, Callback<Itinerary> callback);


    @GET("/itineraries")
    public void listItineraries(Callback<List<Itinerary>> callback);

    @GET("/itineraries/{id}")
    public void getItinerary(@Path("id") int id, Callback<Itinerary> callback);


    @PUT("/itineraries/{id}")
    public void updateItinerary(@Path("id") int id, @Body Itinerary itinerary, Callback<Itinerary> callback);

    @DELETE("/itineraries/{id}")
    public void deleteItinerary(@Path("id") int id, Callback<Boolean> callback);

    @GET("/itineraries/search")
    public void searchItinerary(@Query("query") String query, Callback<List<Itinerary>> callback);

}
