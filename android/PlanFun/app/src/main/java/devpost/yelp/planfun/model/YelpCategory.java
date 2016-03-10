package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexb on 3/1/2016.
 */
public class YelpCategory implements Parcelable
{
    private int id;
    private String name;
    private String icon_string;

    public YelpCategory()
    {

    }
    public YelpCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIcon_string() {
        return icon_string;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return name;
    }

    public YelpCategory(Parcel in)
    {
        id = in.readInt();
        name = in.readString();
        icon_string = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(icon_string);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<YelpCategory> CREATOR = new Parcelable.Creator<YelpCategory>() {
        @Override
        public YelpCategory createFromParcel(Parcel in) {
            return new YelpCategory(in);
        }

        @Override
        public YelpCategory[] newArray(int size) {
            return new YelpCategory[size];
        }
    };
}
