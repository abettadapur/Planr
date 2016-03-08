package devpost.yelp.planfun.ui.listutils;

/**
 * Created by alexb on 3/8/2016.
 */
public interface ItemTouchHelperAdapter
{
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
