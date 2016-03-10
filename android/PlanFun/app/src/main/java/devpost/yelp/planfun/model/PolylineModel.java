package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexb on 2/27/2016.
 */
public class PolylineModel implements Parcelable
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

    public PolylineModel(Parcel in)
    {
        order = in.readInt();
        origin = in.readParcelable(Location.class.getClassLoader());
        destination = in.readParcelable(Location.class.getClassLoader());
        polyline = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(order);
        parcel.writeParcelable(origin, flags);
        parcel.writeParcelable(destination, flags);
        parcel.writeString(polyline);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PolylineModel> CREATOR = new Parcelable.Creator<PolylineModel>() {
        @Override
        public PolylineModel createFromParcel(Parcel in) {
            return new PolylineModel(in);
        }

        @Override
        public PolylineModel[] newArray(int size) {
            return new PolylineModel[size];
        }
    };
}
