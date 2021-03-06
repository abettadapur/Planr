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
    @POST("plan/{id}/item/create")
    Call<Item> createItem(@Path("id") int plan_id, @Body Item item);

    @GET("plan/{i_id}/item/{id}")
    Call<Item> getItem(@Path("i_id") int plan_id, @Path("id") int item_id);

    @PUT("plan/{i_id}/item/{id")
    Call<Item> updateItem(@Path("i_id") int plan_id, @Path("id") int item_id, @Body Item item);

    @DELETE("plan/{i_id}/item/{id}")
    Call<Boolean> deleteItem(@Path("i_id") int plan_id, @Path("id") int item_id);


}
