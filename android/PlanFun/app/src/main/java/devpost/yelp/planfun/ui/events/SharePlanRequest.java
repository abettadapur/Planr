package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Plan;

/**
 * Created by ros on 3/18/16.
 */
public class SharePlanRequest {
    public Plan toShare;

    public SharePlanRequest(Plan toShare) {
        this.toShare = toShare;
    }
}
