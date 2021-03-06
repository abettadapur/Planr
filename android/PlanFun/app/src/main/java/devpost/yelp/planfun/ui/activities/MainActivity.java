package devpost.yelp.planfun.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.dialogs.ShareItineraryDialog;
import devpost.yelp.planfun.ui.events.CurrentFragmentEvent;
import devpost.yelp.planfun.ui.events.EditPlanRequest;
import devpost.yelp.planfun.ui.events.FindPlanRequest;
import devpost.yelp.planfun.ui.events.GeneratePlanRequest;
import devpost.yelp.planfun.ui.events.OpenPlanPreviewRequest;
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import devpost.yelp.planfun.ui.events.SavePlanRequest;
import devpost.yelp.planfun.ui.events.SharePlanRequest;
import devpost.yelp.planfun.ui.fragments.BackPressFragment;
import devpost.yelp.planfun.ui.fragments.EditPlanFragment;
import devpost.yelp.planfun.ui.fragments.EditPlanFragment;
import devpost.yelp.planfun.ui.fragments.GeneratePlanFragment;
import devpost.yelp.planfun.ui.fragments.PlanDetailFragment;
import devpost.yelp.planfun.ui.fragments.PlanListFragment;
import devpost.yelp.planfun.ui.fragments.PlanPreviewFragment;
import devpost.yelp.planfun.ui.fragments.SearchPlanFragment;


/**
 * @author Andrey, Alex
 */
public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private Drawer mDrawer;
    private GoogleApiClient mGoogleApiClient;

    private PlanListFragment planListFragment;
    private PlanListFragment searchItineraryFragment;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public MainActivity()
    {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showIntro();
        setContentView(R.layout.activity_plan);
        ButterKnife.bind(this);
        PlanFunApplication.getBus().register(this);

        planListFragment = PlanListFragment.newInstance(R.layout.fragment_plan_list, R.layout.item_list_plan);
        searchItineraryFragment = SearchPlanFragment.newInstance(R.layout.fragment_plan_list, R.layout.item_list_plan);
        setSupportActionBar(toolbar);
        buildToolbar();

        mDrawer = this.build_drawer();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, planListFragment)
                    .commit();

        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();

    }

    private void showIntro()
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, PlansIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public GoogleApiClient getClient()
    {
        return mGoogleApiClient;
    }

    private Drawer build_drawer() {

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        AccountHeader mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();
        Profile me = Profile.getCurrentProfile();
        mAccountHeader.addProfile(new ProfileDrawerItem().withName(me.getName()).withIcon(me.getProfilePictureUri(100, 100)), 0);

        //TODO(abettadapur): add account header profile
        return new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar)
                .withAccountHeader(mAccountHeader)
                .withOnDrawerNavigationListener(clickedView -> {
                    onBackPressed();
                    return true;
                })
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("My Plans").withIcon(MaterialDrawableBuilder.with(this)
                                .setColor(Color.BLACK)
                                .setToActionbarSize()
                                .setIcon(MaterialDrawableBuilder.IconValue.FORMAT_LIST_BULLETED)
                                .build()),
//                        new PrimaryDrawerItem().withName("Search Plans").withIcon(MaterialDrawableBuilder.with(this)
//                                .setColor(Color.BLACK)
//                                .setToActionbarSize()
//                                .setIcon(MaterialDrawableBuilder.IconValue.MAGNIFY)
//                                .build()),
                        new SectionDrawerItem(),
//                        new SecondaryDrawerItem().withName("Settings").withIcon(MaterialDrawableBuilder.with(this)
//                                .setColor(Color.BLACK)
//                                .setToActionbarSize()
//                                .setIcon(MaterialDrawableBuilder.IconValue.SETTINGS)
//                                .build()),
                        new SecondaryDrawerItem().withName("Logout").withIcon(MaterialDrawableBuilder.with(this)
                                .setColor(Color.BLACK)
                                .setToActionbarSize()
                                .setIcon(MaterialDrawableBuilder.IconValue.LOGOUT)
                                .build())
                )
                .withOnDrawerItemClickListener(drawer_listener)
                .build();
    }

    public void buildToolbar()
    {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                //change to back arrow
                mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                //if you dont want the drawer to be opened in Fragment
                mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                //change to hamburger icon

                mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                //call this method to display hamburger icon
                mDrawer.getActionBarDrawerToggle().syncState();
                mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getSupportActionBar().setTitle("Your Plans");
            }
        });
    }

    private final Drawer.OnDrawerItemClickListener drawer_listener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
            String item = ((Nameable) iDrawerItem).getName().toString();
            switch (item) {
                case "My Plans":
                    if(currentFragment != planListFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, planListFragment)
                                .commit();
                        getSupportActionBar().setTitle("Your Itineraries");
                    }
                    break;
                case "Search Plans":
                    PlanFunApplication.getBus().post(new FindPlanRequest());
                    break;
                case "Settings":
                    break;
                case "Logout":
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    intent.putExtra("delay", false);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    return false;
            }
            mDrawer.closeDrawer();
            return true;
        }
    };
    
    @Subscribe
    public void onOpenPlanRequest(OpenPlanRequest request)
    {
        if(request.fromGenerate) {
            while(getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
        }

        PlanDetailFragment fragment = PlanDetailFragment.newInstance(request.plan_id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }


    @Subscribe
    public void onEditPlanRequest(EditPlanRequest request)
    {
        EditPlanFragment fragment;

        if(!request.new_plan)
            fragment = EditPlanFragment.newInstance(request.plan);
        else
            fragment = EditPlanFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }

    @Subscribe
    public void onFindPlanRequest(FindPlanRequest request){
        new MaterialDialog.Builder(this)
                .title("Coming soon!")
                .content("This app is still in alpha, and this feature is still not quite polished enough. Coming soon though!")
                .positiveText("Sweet")
                .show();
        /* TODO
        if(currentFragment != searchItineraryFragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, searchItineraryFragment)
                    .commit();
            mDrawer.setSelectionAtPosition(1);
            getSupportActionBar().setTitle("Search Results");
        }*/
    }

    @Subscribe
    public void onGeneratePlanRequest(GeneratePlanRequest request)
    {
        GeneratePlanFragment fragment = new GeneratePlanFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }

    @Subscribe
    public void onOpenPlanPreviewRequest(OpenPlanPreviewRequest request)
    {
        PlanPreviewFragment fragment = PlanPreviewFragment.newInstance(request.plan);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }

    @Subscribe
    public void newCurrentFragment(CurrentFragmentEvent event)
    {
        currentFragment = event.fragment;
    }

    @Override
    public void onBackPressed() {
        if(currentFragment instanceof BackPressFragment)
        {
            if(!((BackPressFragment)currentFragment).onBackPressed())
                super.onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }
}
