package devpost.yelp.planfun.ui.fragments;

/**
 * Created by alexb on 3/10/2016.
 */
public abstract class BackPressFragment extends BaseFragment
{
    public boolean onBackPressed() {
        if(getChildFragmentManager().getBackStackEntryCount() > 0)
        {
            getChildFragmentManager().popBackStack();
            return true;
        }
        return false;
    }
}
