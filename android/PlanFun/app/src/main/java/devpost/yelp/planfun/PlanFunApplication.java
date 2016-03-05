package devpost.yelp.planfun;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import devpost.yelp.planfun.net.RestClient;

/**
 * Created by Andrey  on 2/7/16.
 */
public class PlanFunApplication extends Application {
    public static final boolean FAKE_NET = true;
    private static Bus bus;
    @Override
    public void onCreate()
    {
        super.onCreate();
        RestClient.init(this);
        FacebookSdk.sdkInitialize(this);
    }

    public static Bus getBus()
    {
        if(bus==null)
        {
            bus = new Bus(ThreadEnforcer.ANY);
        }
        return bus;
    }

}
