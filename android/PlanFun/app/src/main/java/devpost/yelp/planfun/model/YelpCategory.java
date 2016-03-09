package devpost.yelp.planfun.model;

/**
 * Created by alexb on 3/1/2016.
 */
public class YelpCategory
{
    private int id;
    private String name;
    private String icon_string;

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
}
