package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by abettadapur on 3/17/2015.
 */
public class Location implements Parcelable
{
    private String address;
    private String city;
    private String postal_code;
    private String state;
    private LatLng coordinate;

    public Location()
    {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }


    public Location(Parcel in)
    {
        this.address = in.readString();
        this.city = in.readString();
        this.postal_code = in.readString();
        this.state = in.readString();
        this.coordinate = in.readParcelable(LatLng.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(address);
        parcel.writeString(city);
        parcel.writeString(postal_code);
        parcel.writeString(state);
        parcel.writeParcelable(coordinate, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
