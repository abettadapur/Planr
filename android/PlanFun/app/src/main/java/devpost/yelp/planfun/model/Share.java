package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexb on 2/29/2016.
 */
public class Share implements Parcelable
{
    private String permission;
    private User user;

    public Share()
    {

    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Share(Parcel in)
    {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.permission = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(user, flags);
        parcel.writeString(permission);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Share> CREATOR = new Parcelable.Creator<Share>() {
        @Override
        public Share createFromParcel(Parcel in) {
            return new Share(in);
        }

        @Override
        public Share[] newArray(int size) {
            return new Share[size];
        }
    };
}
