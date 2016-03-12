package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.YelpEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Andrey on 3/11/16.
 */
public interface SearchService {

    @GET("search")
    Call<List<YelpEntry>> searchItems(@Query("latitude") double lat, @Query("longtitude") double lng);


    @GET("search")
    Call<List<YelpEntry>> searchItems(@Query("latitude") double lat, @Query("longtitude") double lng,
                           @Query("categories") String categories);


    @GET("search")
    Call<List<YelpEntry>> searchItems(@Query("latitude") double lat, @Query("longtitude") double lng,
                           @Query("categories") String categories, @Query("term") String term);
}
