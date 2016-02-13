package devpost.yelp.planfun.net.interfaces;

import devpost.yelp.planfun.model.Item;
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
public interface ItemService
{
    @POST("/itinerary/{id}/item/create")
    public void createItem(@Path("id") int itinerary_id, @Body Item item, @Query("token") String token, Callback<Item> callback);

    @GET("/itinerary/{i_id}/item/{id}")
    public void getItem(@Path("i_id") int itinerary_id, @Path("id") int item_id, @Query("token") String token, Callback<Item> callback);

    @PUT("/itinerary/{i_id}/item/{id")
    public void updateItem(@Path("i_id") int itinerary_id, @Path("id") int item_id, @Body Item item, @Query("token") String token, Callback<Item> callback);

    @DELETE("/itinerary/{i_id}/item/{id}")
    public void delteItem(@Path("i_id") int itinerary_id, @Path("id") int item_id, @Query("token") String token, Callback<Boolean> callback);


}
