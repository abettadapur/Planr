package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alex on 3/7/2015.
 */
public class User implements Parcelable
{
    private long id;
    private String first_name;
    private String last_name;
    private String email;
    private String facebook_id;

    public User(int id, String first_name, String last_name, String email, String facebook_id) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.facebook_id = facebook_id;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User)
        {
            User u = (User)o;
            return u.id == id;
        }
        return false;
    }
    public User(Parcel in)
    {
        id = in.readLong();
        first_name = in.readString();
        last_name = in.readString();
        first_name = in.readString();
        facebook_id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeLong(id);
        parcel.writeString(first_name);
        parcel.writeString(last_name);
        parcel.writeString(email);
        parcel.writeString(facebook_id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
