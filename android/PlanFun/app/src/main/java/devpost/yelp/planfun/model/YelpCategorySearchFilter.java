package devpost.yelp.planfun.model;

/**
 * Created by ros on 3/12/16.
 */
public class YelpCategorySearchFilter {
    private int id;
    private String filter;

    public YelpCategorySearchFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
