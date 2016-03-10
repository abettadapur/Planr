package devpost.yelp.planfun.ui.fragments;

import android.support.v4.app.Fragment;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.ui.events.CurrentFragmentEvent;

/**
 * Created by alexb on 3/10/2016.
 */
public abstract class BaseFragment extends Fragment
{
    @Override
    public void onStart() {
        PlanFunApplication.getBus().post(new CurrentFragmentEvent(this));
        super.onStart();
    }
}
