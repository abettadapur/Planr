package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by abettadapur on 3/17/2015.
 */
public class YelpEntry implements Parcelable
{
    private String id;
    private String name;
    private String phone;
    private String image_url;
    private String url;
    private float rating;
    private int review_count;
    private int price;
    private Location location;
    private List<YelpCategory> categories;
    private YelpCategory main_category;

    public YelpEntry()
    {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public YelpEntry(Parcel in)
    {
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        image_url = in.readString();
        url = in.readString();
        rating = in.readFloat();
        review_count = in.readInt();
        price = in.readInt();
        location = in.readParcelable(Location.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public List<YelpCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<YelpCategory> categories) {
        this.categories = categories;
    }

    public YelpCategory getMain_category() {
        if(main_category != null)
            return main_category;
        if(categories.size()>0)
            return categories.get(0);
        return null;
    }

    public void setMain_category(YelpCategory main_category) {
        this.main_category = main_category;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(image_url);
        parcel.writeString(url);
        parcel.writeFloat(rating);
        parcel.writeInt(review_count);
        parcel.writeInt(price);
        parcel.writeParcelable(location, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<YelpEntry> CREATOR = new Parcelable.Creator<YelpEntry>() {
        @Override
        public YelpEntry createFromParcel(Parcel in) {
            return new YelpEntry(in);
        }

        @Override
        public YelpEntry[] newArray(int size) {
            return new YelpEntry[size];
        }
    };
}
