package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Item;

/**
 * Created by Andrey on 3/5/16.
 * An event to save an item, such as from the creation of a new item or editing of an existing item.
 */
public class SaveItemRequest {

    public Item to_save;

    public SaveItemRequest(Item plan)
    {
        this.to_save = plan;
    }
}
