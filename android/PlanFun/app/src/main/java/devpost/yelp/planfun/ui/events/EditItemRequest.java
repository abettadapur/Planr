package devpost.yelp.planfun.ui.events;

/**
 * Created by Andrey on 3/5/16.
 */
public class EditItemRequest {

    public int item_id;
    public boolean new_plan;

    public EditItemRequest(boolean make_new)
    {
        this.new_plan = make_new;
    }

    public EditItemRequest(int id)
    {
        this.item_id = id;
    }
}
