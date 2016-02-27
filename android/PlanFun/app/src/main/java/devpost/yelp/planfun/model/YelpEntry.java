package devpost.yelp.planfun.model;

/**
 * Created by abettadapur on 3/17/2015.
 */
public class YelpEntry
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
}
