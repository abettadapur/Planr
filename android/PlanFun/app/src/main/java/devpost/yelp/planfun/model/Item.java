package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Alex on 3/7/2015.
 */
public class Item implements Parcelable
{
    private int id;
    private String yelp_item_id;
    private YelpCategory yelp_category;
    private String name;
    private Calendar start_time;
    private Calendar end_time;
    private String description;
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

    public Item(YelpEntry entry){
        this.yelp_item_id = entry.getId();
        this.yelp_item = entry;
        this.type = ItemType.YELP;
        this.name = entry.getName();
        this.yelp_category = entry.getMain_category();
        this.location = entry.getLocation();
    }

    public Item()  {
    }

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

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Item(Parcel in)
    {
        this.id = in.readInt();
        this.yelp_item_id = in.readString();
        this.yelp_category = in.readParcelable(YelpCategory.class.getClassLoader());
        this.name = in.readString();
        this.description = in.readString();
        this.start_time = new GregorianCalendar(TimeZone.getTimeZone(in.readString()));
        this.start_time.setTimeInMillis(in.readLong());
        this.end_time = new GregorianCalendar(TimeZone.getTimeZone(in.readString()));
        this.end_time.setTimeInMillis(in.readLong());
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.yelp_item = in.readParcelable(YelpEntry.class.getClassLoader());
        this.type = (ItemType)in.readSerializable();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(id);
        parcel.writeString(yelp_item_id);
        parcel.writeParcelable(yelp_category, flags);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(start_time.getTimeZone().getID());
        parcel.writeLong(start_time.getTimeInMillis());
        parcel.writeString(end_time.getTimeZone().getID());
        parcel.writeLong(end_time.getTimeInMillis());
        parcel.writeParcelable(location, flags);
        parcel.writeParcelable(yelp_item, flags);
        parcel.writeSerializable(type);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
