package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

/**
 * Created by abettadapur on 3/17/2015.
 */
public class Location implements Parcelable
{
    private String address;
    private String city;
    private String postal_code;
    private String state_code;
    private LatLng coordinate;

    @Expose(serialize=false)
    private Place place;

    public Location()
    {}

    public Location(Place place) {
        String[] commaParts = place.getAddress().toString().split(",");
        int startIndex = commaParts.length-4;
        address = "";
        for(int i = 0; i<=startIndex; i++)
        {
            address += commaParts[i].trim();
            if(i!=startIndex) address+=", ";
        }
        startIndex++;
        city = commaParts[startIndex++].trim();
        state_code = commaParts[startIndex++].split(" ")[0];
        postal_code = commaParts[startIndex++].split(" ")[1];

        coordinate = place.getLatLng();
    }

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

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
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
        this.state_code = in.readString();
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
        parcel.writeString(state_code);
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
