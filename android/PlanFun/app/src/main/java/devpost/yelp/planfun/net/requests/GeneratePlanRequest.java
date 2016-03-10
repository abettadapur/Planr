package devpost.yelp.planfun.net.requests;

import java.util.List;

import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.YelpCategory;

/**
 * Created by alexb on 3/9/2016.
 */
public class GeneratePlanRequest
{
    public List<YelpCategory> categories;
    public Plan plan;

    public GeneratePlanRequest(List<YelpCategory> categories, Plan plan) {
        this.categories = categories;
        this.plan = plan;
    }
}
