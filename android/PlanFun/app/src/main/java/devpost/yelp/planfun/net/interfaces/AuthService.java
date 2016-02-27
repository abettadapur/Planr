package devpost.yelp.planfun.net.interfaces;

import devpost.yelp.planfun.net.requests.AuthRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Alex on 2/19/2015.
 */
public interface AuthService {
    @POST("auth")
    Call<Boolean> verifyAuthentication(@Body AuthRequest postBody);

}
