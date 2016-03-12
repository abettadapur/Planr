package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Item;

/**
 * Created by Andrey on 3/5/16.
 */
public class EditItemRequest {

    public Item item;
    public boolean new_item;

    public EditItemRequest()
    {
        this.new_item = true;
    }

    public EditItemRequest(Item item)
    {
        this.item = item;
    }
}
