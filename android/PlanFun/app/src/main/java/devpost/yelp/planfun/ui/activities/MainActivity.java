package devpost.yelp.planfun.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.events.OpenItineraryRequest;
import devpost.yelp.planfun.ui.fragments.ItineraryDetailFragment;
import devpost.yelp.planfun.ui.fragments.ItineraryListFragment;
import devpost.yelp.planfun.ui.fragments.SearchItineraryFragment;


/**
 * @author Andrey, Alex
 */
public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private RestClient mRestClient;
    private Drawer mDrawer;

    private ItineraryListFragment itineraryListFragment;
    private ItineraryListFragment searchItineraryFragment;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);
        ButterKnife.bind(this);
        PlanFunApplication.getBus().register(this);

        itineraryListFragment = ItineraryListFragment.newInstance(R.layout.fragment_itinerary_list, R.layout.itinerary_list_item);
        searchItineraryFragment = SearchItineraryFragment.newInstance(R.layout.fragment_itinerary_list, R.layout.itinerary_list_item);
        setSupportActionBar(toolbar);
        buildToolbar();

        mDrawer = this.build_drawer();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, itineraryListFragment)
                    .commit();
            currentFragment = itineraryListFragment;

        }
        mRestClient = RestClient.getInstance();
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
                        new PrimaryDrawerItem().withName("My Plans").withIcon(new IconDrawable(this, Iconify.IconValue.fa_list).color(0x8A000000)),
                        new PrimaryDrawerItem().withName("Search Plans").withIcon(new IconDrawable(this, Iconify.IconValue.fa_search).color(0x8A000000)),
                        new SectionDrawerItem(),
                        new SecondaryDrawerItem().withName("Settings").withIcon(new IconDrawable(this, Iconify.IconValue.fa_cog).color(0x8A000000)),
                        new SecondaryDrawerItem().withName("Logout").withIcon(new IconDrawable(this, Iconify.IconValue.fa_sign_out).color(0x8A000000))
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
                    if(currentFragment != itineraryListFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, itineraryListFragment)
                                .commit();
                        currentFragment = itineraryListFragment;
                        getSupportActionBar().setTitle("Your Itineraries");
                    }
                    break;
                case "Search Plans":
                    if(currentFragment != searchItineraryFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, searchItineraryFragment)
                                .commit();
                        currentFragment = searchItineraryFragment;
                        getSupportActionBar().setTitle("Search Results");
                    }
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
    public void onOpenItineraryRequest(OpenItineraryRequest request)
    {
        ItineraryDetailFragment fragment = ItineraryDetailFragment.newInstance(request.itinerary_id);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }
}
