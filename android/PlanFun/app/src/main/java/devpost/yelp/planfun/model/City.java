package devpost.yelp.planfun.model;

/**
 * Created by Andrey on 2/28/16.
 */
public class City {

    private String city;
    private String state;
    private float lat;
    private float lng;
    private int zip;

    public City(String name, String state, float lat, float lng, int zip) {
        this.city = name;
        this.state = state;
        this.lat = lat;
        this.lng = lng;
        this.zip = zip;
    }

    public String getName() {
        return city;
    }

    public void setName(String name) {
        this.city = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String toString(){
        return getName()+", "+getState();
    }
}
