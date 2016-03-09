package devpost.yelp.planfun.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Alex on 3/7/2015.
 */
public class Plan implements Comparable<Plan>
{
    private int id;
    private String name;
    private Calendar start_time;
    private Calendar end_time;
    private String city;
    private String starting_address;
    private LatLng starting_coordinate;
    @SerializedName("public")
    private boolean isPublic;
    @Expose(serialize = false)
    private List<Item> items;
    @Expose(serialize = false)
    private User user;
    @Expose(serialize = false)
    private List<PolylineModel> polylines;
    @Expose(serialize=false)
    private List<Share> shared_users;


    public Plan(){
        start_time = Calendar.getInstance();
        end_time = Calendar.getInstance();
        items = new ArrayList<Item>();
    }

    public Plan(String name, Calendar start_time, Calendar end_time,  boolean isPublic, List<Item> items)
    {
        this.id = 0;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.items = items;
        this.isPublic = isPublic;
    }

    public Plan(int id, String name, Calendar date, Calendar start_time, Calendar end_time, boolean isPublic, List<Item> items) {

        this.id = id;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.items = items;
        this.isPublic = isPublic;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getStart_time() {
        return start_time;
    }

    public void setStart_time(Calendar start_time) {
        this.start_time = start_time;
    }

    public Calendar getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Calendar end_time) {
        this.end_time = end_time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public int compareTo(Plan another) {
        return this.getStart_time().compareTo(another.getStart_time());
    }

    public List<PolylineModel> getPolylines() {
        return polylines;
    }

    public void setPolylines(List<PolylineModel> polylines) {
        this.polylines = polylines;
    }

    public List<Share> getShared_users() {
        return shared_users;
    }

    public String getStarting_address() {
        return starting_address;
    }

    public void setStarting_address(String starting_address) {
        this.starting_address = starting_address;
    }

    public LatLng getStarting_coordinate() {
        return starting_coordinate;
    }

    public void setStarting_coordinate(LatLng starting_coordinate) {
        this.starting_coordinate = starting_coordinate;
    }
}
