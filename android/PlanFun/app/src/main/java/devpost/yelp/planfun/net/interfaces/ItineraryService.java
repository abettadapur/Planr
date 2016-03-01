package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.net.requests.ShareRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Alex on 3/7/2015.
 */
public interface ItineraryService
{
    @POST("itineraries")
    Call<Itinerary> createItinerary(@Body Itinerary itinerary);

    @GET("itineraries")
    Call<List<Itinerary>> listItineraries();

    @GET("itineraries")
    Call<List<Itinerary>> listItineraries(@Query("shared") boolean includeShared);

    @GET("itineraries/{id}")
    Call<Itinerary> getItinerary(@Path("id") int id);

    @GET("itineraries/{id}")
    Call<Itinerary> getItinerary(@Path("id") int id, @Query("include_polyline") boolean polyline);

    @PUT("itineraries/{id}")
    Call<Itinerary> updateItinerary(@Path("id") int id, @Body Itinerary itinerary);

    @DELETE("itineraries/{id}")
    Call<Boolean> deleteItinerary(@Path("id") int id);

    @GET("itineraries/search")
    Call<List<Itinerary>> searchItinerary(@Query("query") String query);

    @POST("itineraries/{id}/share")
    Call<Itinerary> shareItinerary(@Path("id") int id, @Body ShareRequest request);

}
