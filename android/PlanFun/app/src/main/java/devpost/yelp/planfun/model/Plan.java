package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Alex on 3/7/2015.
 */
public class Plan implements Comparable<Plan>, Parcelable
{
    private int id;
    private String name;
    private String city;
    private Calendar start_time;
    private Calendar end_time;
    private String starting_address;
    private LatLng starting_coordinate;
    private String description;
    @SerializedName("public")
    private boolean isPublic;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Plan(Parcel in)
    {
        this.id = in.readInt();
        this.city = in.readString();
        this.start_time = new GregorianCalendar(TimeZone.getTimeZone(in.readString()));
        this.start_time.setTimeInMillis(in.readLong());
        this.end_time = new GregorianCalendar(TimeZone.getTimeZone(in.readString()));
        this.end_time.setTimeInMillis(in.readLong());
        this.description = in.readString();
        this.starting_address = in.readString();
        this.starting_coordinate = in.readParcelable(LatLng.class.getClassLoader());
        this.isPublic = in.readByte()!=0;
        this.items = new ArrayList<>(Arrays.asList(in.createTypedArray(Item.CREATOR)));
        this.user = in.readParcelable(User.class.getClassLoader());
        this.polylines = new ArrayList<>(Arrays.asList(in.createTypedArray(PolylineModel.CREATOR)));
        this.shared_users = new ArrayList<>(Arrays.asList(in.createTypedArray(Share.CREATOR)));

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(id);
        parcel.writeString(city);
        parcel.writeString(start_time.getTimeZone().getID());
        parcel.writeLong(start_time.getTimeInMillis());
        parcel.writeString(end_time.getTimeZone().getID());
        parcel.writeLong(end_time.getTimeInMillis());
        parcel.writeString(description);
        parcel.writeString(starting_address);
        parcel.writeParcelable(starting_coordinate, flags);
        parcel.writeByte((byte)(isPublic?1:0));
        parcel.writeTypedArray((items.toArray(new Item[items.size()])), flags);
        parcel.writeParcelable(user, flags);
        parcel.writeTypedArray(polylines.toArray(new PolylineModel[polylines.size()]), flags);
        parcel.writeTypedArray(shared_users.toArray(new Share[shared_users.size()]), flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Plan> CREATOR = new Parcelable.Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };
}
