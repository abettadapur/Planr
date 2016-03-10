package devpost.yelp.planfun;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import devpost.yelp.planfun.net.RestClient;

/**
 * Created by Andrey  on 2/7/16.
 */
public class PlanFunApplication extends Application {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("H:mm", Locale.US);
    private static Bus bus;
    @Override
    public void onCreate()
    {
        super.onCreate();
        RestClient.init(this);
        FacebookSdk.sdkInitialize(this);
        Iconify
                .with(new FontAwesomeModule())
                .with(new MaterialModule());
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
