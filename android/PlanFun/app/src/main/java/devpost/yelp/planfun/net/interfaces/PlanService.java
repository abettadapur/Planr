package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.net.requests.GeneratePlanRequest;
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
public interface PlanService
{
    @POST("plans")
    Call<Plan> createPlan(@Body Plan plan);

    @POST("plans/generate")
    Call<Plan> generatePlan(@Body GeneratePlanRequest request);

    @GET("plans")
    Call<List<Plan>> listPlans();

    @GET("plans")
    Call<List<Plan>> listPlans(@Query("shared") boolean includeShared);

    @GET("plans/{id}")
    Call<Plan> getPlan(@Path("id") int id);

    @GET("plans/{id}")
    Call<Plan> getPlan(@Path("id") int id, @Query("include_polyline") boolean polyline);

    @PUT("plans/{id}")
    Call<Plan> updatePlan(@Path("id") int id, @Body Plan plan);

    @DELETE("plans/{id}")
    Call<ResponseBody> deletePlan(@Path("id") int id);

    @GET("plans/search")
    Call<List<Plan>> searchPlan(@Query("query") String query);

    @POST("plans/{id}/share")
    Call<Plan> sharePlan(@Path("id") int id, @Body List<ShareRequest> request);

    @POST("plans/{id}/randomize")
    Call<Plan> randomizePlan(@Path("id") int id);

}
