package devpost.yelp.planfun.net.interfaces;

import devpost.yelp.planfun.net.requests.AuthRequest;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Alex on 2/19/2015.
 */
public interface AuthService {
    @POST("/auth/login")
    void verifyAuthentication(@Body AuthRequest postBody, Callback<Boolean> callback);

}
