package devpost.yelp.planfun.model;

import com.google.gson.annotations.Expose;

import java.util.Calendar;

/**
 * Created by Alex on 3/7/2015.
 */
public class Item
{
    private int id;
    private String yelp_item_id;
    private YelpCategory yelp_category;
    private String name;
    private Calendar start_time;
    private Calendar end_time;
    private Location location;
    private YelpEntry yelp_item;
    private ItemType type;

    public Item(int id, String yelp_item_id, YelpCategory yelp_category, String name, Calendar start_time, Calendar end_time, YelpEntry yelp_entry) {
        this.id = id;
        this.yelp_item_id = yelp_item_id;
        this.yelp_category = yelp_category;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.yelp_item = yelp_entry;
    }

    public Item(int id, String yelp_item_id, YelpCategory yelp_category, String name, Calendar start_time, Calendar end_time) {
        this.id = id;
        this.yelp_item_id = yelp_item_id;
        this.yelp_category = yelp_category;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.yelp_item = null;
    }

    public Item(String yelp_item_id, YelpCategory yelp_category, String name, Calendar start_time, Calendar end_time) {
        this.yelp_item_id = yelp_item_id;
        this.yelp_category = yelp_category;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.yelp_item = null;
        this.id=-1;
    }

    public Item()  {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYelp_item_id() {
        return yelp_item_id;
    }

    public void setYelp_item_id(String yelp_item_id) {
        this.yelp_item_id = yelp_item_id;
    }

    public YelpCategory getYelp_category() {
        return yelp_category;
    }

    public void setYelp_category(YelpCategory yelp_category) {
        this.yelp_category = yelp_category;
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

    public YelpEntry getYelp_item() {
        return yelp_item;
    }

    public void setYelp_item(YelpEntry yelp_item) {
        this.yelp_item = yelp_item;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
