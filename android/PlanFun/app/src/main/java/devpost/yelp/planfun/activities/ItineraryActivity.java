package devpost.yelp.planfun.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;
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

import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.net.RestClient;

/**
 * @author Andrey, Alex
 */
public class ItineraryActivity extends AppCompatActivity {

    private Fragment currentFragment;
    private RestClient mRestClient;
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;
    private List<Itinerary> itineraries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intinerary);

        //itineraryListFragment = ItineraryListFragment.newInstance(R.layout.fragment_itinerary_list, R.layout.itinerary_list_item);
        // searchItineraryFragment = ItineraryListFragment.newInstance(R.layout.fragment_search_itinerary, R.layout.itinerary_list_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(new IconDrawable(this, Iconify.IconValue.fa_reorder).color(0xFFFFFF).sizeDp(23));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer();
            }
        });

        mDrawer = this.build_drawer();

        Request request = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser graphUser, com.facebook.Response response) {
                mAccountHeader.addProfile(new ProfileDrawerItem().withName(graphUser.getName()), 0);
            }
        });
        request.executeAsync();

        mDrawer = build_drawer();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        /**Create a new itinerary list fragment and add it to the activity **/


        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, itineraryListFragment)
                    .commit();
            currentFragment = itineraryListFragment;

        }*/
        mRestClient = RestClient.getInstance();
    }

    private Drawer build_drawer() {
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();
        //                .addProfiles(
        //new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
        //)

        return new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("My Itineraries").withIcon(new IconDrawable(this, Iconify.IconValue.fa_list).color(0xFFFFFF)),
                        new PrimaryDrawerItem().withName("Search Itineraries").withIcon(new IconDrawable(this, Iconify.IconValue.fa_search).color(0xFFFFFF)),

                        new SectionDrawerItem(),
                        new SecondaryDrawerItem().withName("Settings").withIcon(new IconDrawable(this, Iconify.IconValue.fa_cog).color(0xFFFFFF)),
                        new SecondaryDrawerItem().withName("Logout").withIcon(new IconDrawable(this, Iconify.IconValue.fa_sign_out).color(0xFFFFFF))
                )
                .withOnDrawerItemClickListener(drawer_listener)
                .build();
    }

    private final Drawer.OnDrawerItemClickListener drawer_listener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
            String item = ((Nameable) iDrawerItem).getName().toString();
            switch (item) {
                case "My Itineraries":
                    //   getSupportFragmentManager().beginTransaction().remove(currentFragment).add(R.id.container, itineraryListFragment).commit();
                    //currentFragment = itineraryListFragment;
                    getSupportActionBar().setTitle("Your Itineraries");
                    break;
                case "Search Itineraries":
                    //    getSupportFragmentManager().beginTransaction().remove(currentFragment).add(R.id.container, searchItineraryFragment).commit();
                    //  currentFragment = searchItineraryFragment;
                    getSupportActionBar().setTitle("Search Results");
                    break;
                case "Settings":
                    break;
                case "Logout":
                    Session.getActiveSession().closeAndClearTokenInformation();
                    Intent intent = new Intent(ItineraryActivity.this, SplashActivity.class);
                    intent.putExtra("delay", false);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    return false;
            }
            return true;
        }
    };
}
