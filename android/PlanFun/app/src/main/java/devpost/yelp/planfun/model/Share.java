package devpost.yelp.planfun.model;

/**
 * Created by alexb on 2/29/2016.
 */
public class Share
{
    private String permission;
    private User user;

    public Share()
    {

    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
