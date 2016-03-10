package devpost.yelp.planfun.ui.events;

import android.support.v4.app.Fragment;

/**
 * Created by alexb on 3/10/2016.
 */
public class CurrentFragmentEvent
{
    public Fragment fragment;

    public CurrentFragmentEvent(Fragment fragment) {
        this.fragment = fragment;
    }
}
