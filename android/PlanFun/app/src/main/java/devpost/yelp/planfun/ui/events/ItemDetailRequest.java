package devpost.yelp.planfun.ui.events;

import devpost.yelp.planfun.model.Item;

/**
 * Created by alexb on 3/10/2016.
 */
public class ItemDetailRequest
{
    public Item item;
    public ItemDetailRequest(Item item)
    {
        this.item = item;
    }
}
