package devpost.yelp.planfun.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import devpost.yelp.planfun.net.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexb on 3/1/2016.
 */
public class YelpCategory implements Parcelable {
    private int id;
    private String name;
    private String icon_string;
    private List<YelpCategorySearchFilter> search_filters;

    public static List<YelpCategory> SERVER_CATEGORIES;

    static {
        RestClient.getInstance().getCategoryService().getCategories().enqueue(
                new Callback<List<YelpCategory>>()

                {
                    @Override
                    public void onResponse
                            (Call< List < YelpCategory >> call, Response<List<YelpCategory>> response) {
                        if (response.isSuccess()) {
                            SERVER_CATEGORIES = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<YelpCategory>> call, Throwable t) {

                    }
                }
        );
    }

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


    public List<YelpCategorySearchFilter> getSearch_filters() {
        return search_filters;
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
