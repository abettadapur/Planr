package devpost.yelp.planfun.net.requests;

/**
 * Created by alexb on 2/28/2016.
 */
public class ShareRequest {
    private String user_id;
    private String permission;

    public ShareRequest(String user_id, String permission)
    {
        this.user_id = user_id;
        this.permission = permission;
    }
}
