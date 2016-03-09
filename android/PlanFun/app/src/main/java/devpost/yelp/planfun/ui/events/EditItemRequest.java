package devpost.yelp.planfun.ui.events;

/**
 * Created by Andrey on 3/5/16.
 */
public class EditItemRequest {

    public int item_id;
    public boolean new_item;

    public EditItemRequest(boolean make_new)
    {
        this.new_item = make_new;
    }

    public EditItemRequest(int id)
    {
        this.item_id = id;
    }
}
