package thingswithworth.org.funtimes.models;

/**
 * @author Alex
 */
public class User
{
    private long id;
    private String first_name;
    private String last_name;
    private String email;
    private static User logged_in_user;

    public User(int id, String first_name, String last_name, String email) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
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

    /**
     * Set the logged in user for the entire app, to be used across activities.
     * @param set_to the User object
     */
    public static void setLogged_in_user(User set_to){
        logged_in_user = set_to;
    }

    /**
     * Get the logged in user for the entire app, to be used across activities.
     * @return the logged in User
     */
    public static User getLogged_in_user(){
        return logged_in_user;
    }
}
