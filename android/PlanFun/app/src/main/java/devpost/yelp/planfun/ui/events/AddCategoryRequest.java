package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.YelpCategory;

/**
 * Created by alexb on 3/8/2016.
 */
public class AddCategoryRequest {
    public YelpCategory category;

    public AddCategoryRequest(YelpCategory category)
    {
        this.category = category;
    }
}
