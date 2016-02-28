package devpost.yelp.planfun.model;

/**
 * Created by Alex on 3/7/2015.
 */
public class User
{
    private long id;
    private String first_name;
    private String last_name;
    private String email;
    private String facebook_id;

    public User(int id, String first_name, String last_name, String email, String facebook_id) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.facebook_id = facebook_id;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

}
