package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Plan;

/**
 * Created by Andrey on 3/5/16.
 * Issued by edit plan fragment as sign to save and go to list or detail view.
 */
public class SavePlanRequest {

    public Plan to_save;

    public SavePlanRequest(Plan plan)
    {
        this.to_save = plan;
    }
}
