package devpost.yelp.planfun.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by abettadapur on 3/17/2015.
 */
public class YelpLocation
{
    private String yelp_id;
    private String address;
    private String city;
    private String postal_code;
    private String state;
    private LatLng coordinate;

    public YelpLocation()
    {}

    public String getYelp_id() {
        return yelp_id;
    }

    public void setYelp_id(String yelp_id) {
        this.yelp_id = yelp_id;
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
}
