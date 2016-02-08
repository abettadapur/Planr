package thingswithworth.org.funtimes.net.services;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import thingswithworth.org.funtimes.net.requests.AuthRequest;

/**
 * @author  Alex
 */
public interface AuthService {
    @POST("/auth/verify")
    void verifyAuthentication(@Body AuthRequest postBody, Callback<Boolean> callback);

}
