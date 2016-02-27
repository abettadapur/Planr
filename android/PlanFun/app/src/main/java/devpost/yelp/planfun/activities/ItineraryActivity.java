package devpost.yelp.planfun.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;

import com.facebook.AccessToken;
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

import java.io.IOException;
import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.fragments.ItineraryListFragment;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.net.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author Andrey, Alex
 */
public class ItineraryActivity extends AppCompatActivity implements ItineraryListFragment.ItineraryListListener, SearchView.OnQueryTextListener {

    private Fragment currentFragment;
    private RestClient mRestClient;
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;
    private List<Itinerary> itineraries;
    private ItineraryListFragment itineraryListFragment;
    private ItineraryListFragment searchItineraryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        itineraryListFragment = ItineraryListFragment.newInstance(R.layout.fragment_itinerary_list, R.layout.itinerary_list_item);
        searchItineraryFragment = ItineraryListFragment.newInstance(R.layout.fragment_search_itinerary, R.layout.itinerary_list_item);

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
//TODO(abettadapur): Profile pictures

//        Request request = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
//            @Override
//            public void onCompleted(GraphUser graphUser, com.facebook.Response response) {
//                mAccountHeader.addProfile(new ProfileDrawerItem().withName(graphUser.getName()), 0);
//            }
//        });
//        request.executeAsync();

        mDrawer = build_drawer();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        /**Create a new itinerary list fragment and add it to the activity **/


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, itineraryListFragment)
                    .commit();
            currentFragment = itineraryListFragment;

        }
        mRestClient = RestClient.getInstance();
    }

    private Drawer build_drawer() {
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

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
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).add(R.id.container, itineraryListFragment).commit();
                    currentFragment = itineraryListFragment;
                    getSupportActionBar().setTitle("Your Itineraries");
                    break;
                case "Search Itineraries":
                    getSupportFragmentManager().beginTransaction().remove(currentFragment).add(R.id.container, searchItineraryFragment).commit();
                    currentFragment = searchItineraryFragment;
                    getSupportActionBar().setTitle("Search Results");
                    break;
                case "Settings":
                    break;
                case "Logout":
                    LoginManager.getInstance().logOut();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intinerary, menu);
        menu.findItem(R.id.search).setIcon(
                new IconDrawable(this, Iconify.IconValue.fa_search)
                        .color(0xFFFFFF)
                        .actionBarSize());

        SearchView view = (SearchView)menu.findItem(R.id.search).getActionView();
        if(view!=null)
        {
            view.setOnQueryTextListener(this);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh_list(final ItineraryListFragment fragment) {
        /** Get a listing of the itineraries for the current user and update the fragment with the items **/

        Call<List<Itinerary>> getItinerariesCall = mRestClient.getItineraryService().listItineraries();
        getItinerariesCall.enqueue(new Callback<List<Itinerary>>() {
            @Override
            public void onResponse(Call<List<Itinerary>> call, Response<List<Itinerary>> response) {
                if (response.isSuccess()) {
                    ItineraryActivity.this.itineraries = itineraries;
                    ItineraryActivity.this.runOnUiThread(() -> {
                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                        fragment.updateItems(ItineraryActivity.this.itineraries);
                    });
                } else {
                    try {
                        Log.e("GET ITINERARIES", response.errorBody().string());
                    } catch (IOException ioex) {
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Itinerary>> call, Throwable t) {

            }
        });
    }

    @Override
    public void remove_item(int id) {

        Call<Boolean> deleteCall = mRestClient.getItineraryService().deleteItinerary(id);
        deleteCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccess())
                {
                    refresh_list(itineraryListFragment);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if(!s.equals("")) {
            if (currentFragment == itineraryListFragment) {
                mDrawer.setSelection(1);
            }
            Call<List<Itinerary>> itineraryCall = mRestClient.getItineraryService().searchItinerary(s);
            itineraryCall.enqueue(new Callback<List<Itinerary>>() {
                @Override
                public void onResponse(Call<List<Itinerary>> call, Response<List<Itinerary>> response) {
                    if(response.isSuccess())
                    {
                        ItineraryActivity.this.runOnUiThread(() -> searchItineraryFragment.updateItems(itineraries));
                    }
                }

                @Override
                public void onFailure(Call<List<Itinerary>> call, Throwable t) {

                }
            });
        }
        return true;
    }


    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
    }
}
