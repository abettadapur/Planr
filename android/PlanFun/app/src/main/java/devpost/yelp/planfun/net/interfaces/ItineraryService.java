package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.net.requests.ShareRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
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
    Call<Plan> createItinerary(@Body Plan plan);

    @GET("itineraries")
    Call<List<Plan>> listItineraries();

    @GET("itineraries")
    Call<List<Plan>> listItineraries(@Query("shared") boolean includeShared);

    @GET("itineraries/{id}")
    Call<Plan> getItinerary(@Path("id") int id);

    @GET("itineraries/{id}")
    Call<Plan> getItinerary(@Path("id") int id, @Query("include_polyline") boolean polyline);

    @PUT("itineraries/{id}")
    Call<Plan> updateItinerary(@Path("id") int id, @Body Plan plan);

    @DELETE("itineraries/{id}")
    Call<ResponseBody> deleteItinerary(@Path("id") int id);

    @GET("itineraries/search")
    Call<List<Plan>> searchItinerary(@Query("query") String query);

    @POST("itineraries/{id}/share")
    Call<Plan> shareItinerary(@Path("id") int id, @Body List<ShareRequest> request);

    @POST("itineraries/{id}/randomize")
    Call<Plan> randomizeItinerary(@Path("id") int id);

}
