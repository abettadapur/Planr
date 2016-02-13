package devpost.yelp.planfun.net.requests;

/**
 * Created by Alex on 2/19/2015.
 */
public class AuthRequest {

    private String token;
    private String user_id;

    public AuthRequest(String token, String user_id) {
        this.token = token;
        this.user_id = user_id;
    }
}
