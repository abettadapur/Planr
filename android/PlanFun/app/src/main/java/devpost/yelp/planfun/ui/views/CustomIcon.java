package devpost.yelp.planfun.ui.views;

import devpost.yelp.planfun.R;

/**
 * Created by alexb on 3/19/2016.
 */
public enum CustomIcon
{
    BAKERY(R.drawable.bakery),
    DANCE(R.drawable.dance),
    BOWLING(R.drawable.bowling),
    BREWERY(R.drawable.brewery),
    GOLF(R.drawable.golf),
    DINNER(R.drawable.dinner),
    SAXOPHONE(R.drawable.jazz),
    MUSEUM(R.drawable.museum),
    SANDWICH(R.drawable.sandwich);

    private int numVal;

    CustomIcon(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
