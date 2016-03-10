package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Plan;

/**
 * Created by alexb on 3/10/2016.
 */
public class OpenPlanPreviewRequest
{
    public Plan plan;

    public OpenPlanPreviewRequest(Plan plan)
    {
        this.plan = plan;
    }
}
