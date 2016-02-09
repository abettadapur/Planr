package thingswithworth.org.funtimes.net.requests;

/**
 * @author Alex
 */
public class AuthRequest {

    private String token;
    private String user_id;

    /**
     * Ask server to store auth token and user_id for future requests.
     * @param token
     * @param user_id
     */
    public AuthRequest(String token, String user_id) {
        this.token = token;
        this.user_id = user_id;
    }
}
