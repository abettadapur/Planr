package devpost.yelp.planfun.ui.events;

/**
 * Created by ros on 3/4/16.
 */
public class EditPlanRequest {


    public int plan_id;
    public boolean new_plan;

    public EditPlanRequest(boolean make_new)
    {
        this.new_plan = make_new;
    }

    public EditPlanRequest(int id)
    {
        this.plan_id = id;
    }
}
