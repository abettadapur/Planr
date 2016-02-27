package devpost.yelp.planfun.model;

/**
 * Created by alexb on 2/27/2016.
 */
public class PolylineModel
{
    private int order;
    private Location origin;
    private Location destination;
    private String polyline;

    public PolylineModel(){}

    public PolylineModel(int order, Location origin, Location destination, String polyline) {
        this.order = order;
        this.origin = origin;
        this.destination = destination;
        this.polyline = polyline;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }
}
