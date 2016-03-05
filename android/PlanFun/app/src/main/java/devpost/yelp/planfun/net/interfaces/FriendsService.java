package devpost.yelp.planfun.net.interfaces;

import java.util.List;

import devpost.yelp.planfun.model.User;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by alexb on 3/2/2016.
 */
public interface FriendsService
{
    @GET("registered_friends")
    Call<List<User>> getFriends();
}
