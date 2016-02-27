package devpost.yelp.planfun.model;

import com.google.gson.annotations.Expose;

import java.util.Calendar;

/**
 * Created by Alex on 3/7/2015.
 */
public class Item
{
    private int id;
    private String yelp_id;
    private String category;
    private String name;
    private Calendar start_time;
    private Calendar end_time;

    @Expose(serialize=false)
    private Location location;

    @Expose(serialize = false)
    private YelpEntry yelp_entry;

    public Item(int id, String yelp_id, String category, String name, Calendar start_time, Calendar end_time, YelpEntry yelp_entry) {
        this.id = id;
        this.yelp_id = yelp_id;
        this.category = category;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.yelp_entry = yelp_entry;
    }

    public Item(int id, String yelp_id, String category, String name, Calendar start_time, Calendar end_time) {
        this.id = id;
        this.yelp_id = yelp_id;
        this.category = category;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.yelp_entry = null;
    }

    public Item(String yelp_id, String category, String name, Calendar start_time, Calendar end_time) {
        this.yelp_id = yelp_id;
        this.category = category;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.yelp_entry = null;
        this.id=-1;
    }

    public Item()  {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYelp_id() {
        return yelp_id;
    }

    public void setYelp_id(String yelp_id) {
        this.yelp_id = yelp_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public YelpEntry getYelp_entry() {
        return yelp_entry;
    }

    public void setYelp_entry(YelpEntry yelp_entry) {
        this.yelp_entry = yelp_entry;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
