package devpost.yelp.planfun.ui.events;

/**
 * Created by alexb on 3/4/2016.
 */
public class OpenPlanRequest
{
    public int plan_id;
    public boolean fromGenerate;

    public OpenPlanRequest(int id, boolean fromGenerate)
    {
        this.plan_id = id;
        this.fromGenerate = fromGenerate;
    }
}
