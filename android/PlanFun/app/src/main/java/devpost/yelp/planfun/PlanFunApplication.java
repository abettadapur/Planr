package devpost.yelp.planfun;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

import devpost.yelp.planfun.net.RestClient;

/**
 * Created by Andrey  on 2/7/16.
 */
public class PlanFunApplication extends Application {
    public static final boolean FAKE_NET = true;
    @Override
    public void onCreate()
    {
        super.onCreate();
        RestClient.init(this);
        FacebookSdk.sdkInitialize(this);
    }
}
