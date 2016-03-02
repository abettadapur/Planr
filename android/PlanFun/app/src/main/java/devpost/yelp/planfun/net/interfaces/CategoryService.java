package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.model.YelpEntry;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.Call;

/**
 * Created by Alex on 3/7/2015.
 */
public interface CategoryService
{
    @GET("category/{category}/query")
    Call<List<YelpEntry>> searchCategory(@Path("category") YelpCategory category, @Query("location") String city);
}
