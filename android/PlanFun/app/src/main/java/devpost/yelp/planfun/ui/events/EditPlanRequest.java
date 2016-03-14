package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Plan;

/**
 * Created by Andrey on 3/4/16.
 * Request to start editing either a new or existing plan. Can be based on hitting create
 * floating button in home view or edit button for a given plan.
 */
public class EditPlanRequest {

    public Plan plan;
    public boolean new_plan;

    public EditPlanRequest(boolean make_new)
    {
        this.new_plan = make_new;
    }

    public EditPlanRequest(Plan plan)
    {
        this.plan = plan;
    }
}
