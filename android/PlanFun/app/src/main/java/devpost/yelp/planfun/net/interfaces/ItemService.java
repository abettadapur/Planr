package devpost.yelp.planfun.net.interfaces;

import devpost.yelp.planfun.model.Item;
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
public interface ItemService
{
    @POST("itinerary/{id}/item/create")
    Call<Item> createItem(@Path("id") int itinerary_id, @Body Item item);

    @GET("itinerary/{i_id}/item/{id}")
    Call<Item> getItem(@Path("i_id") int itinerary_id, @Path("id") int item_id);

    @PUT("itinerary/{i_id}/item/{id")
    Call<Item> updateItem(@Path("i_id") int itinerary_id, @Path("id") int item_id, @Body Item item);

    @DELETE("itinerary/{i_id}/item/{id}")
    Call<Boolean> delteItem(@Path("i_id") int itinerary_id, @Path("id") int item_id);


}
