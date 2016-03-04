package devpost.yelp.planfun.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;

import java.util.Collections;
import java.util.Comparator;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.ui.fragments.EditItineraryFragment;
import devpost.yelp.planfun.ui.fragments.ItemListFragment;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.net.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 4/1/2015.
 */
public class EditItineraryActivity extends ActionBarActivity
{
    private Toolbar mToolbar;
    private PagerSlidingTabStrip mTabStrip;
    private ViewPager mViewPager;
    private Itinerary mCurrentItinerary;
    private RestClient mRestClient;

    private MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_itinerary);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);


        mRestClient = RestClient.getInstance();
        Call<Itinerary> getItineraryCall = mRestClient.getItineraryService().getItinerary(getIntent().getIntExtra("itinerary_id", 0));
        getItineraryCall.enqueue(new Callback<Itinerary>() {
            @Override
            public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
                if (response.isSuccess()) {
                    mCurrentItinerary = response.body();

                    Collections.sort(mCurrentItinerary.getItems(), new Comparator<Item>() {
                        @Override
                        public int compare(Item lhs, Item rhs) {
                            return (int) (lhs.getStart_time().getTimeInMillis() - rhs.getStart_time().getTimeInMillis());
                        }
                    });

                    getSupportActionBar().setTitle("Editing " + response.body().getName());

                    mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

                    mViewPager.setAdapter(mPagerAdapter);

                    mTabStrip.setViewPager(mViewPager);
                    mViewPager.setCurrentItem(0);
                }
            }

            @Override
            public void onFailure(Call<Itinerary> call, Throwable t) {

            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Details", "Items"};
        private Fragment[] fragments;

        public MyPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
            fragments = new Fragment[2];

            EditItineraryFragment itineraryFragment = EditItineraryFragment.newInstance();
            itineraryFragment.setItinerary(mCurrentItinerary);
            fragments[0] = itineraryFragment;

            ItemListFragment itemListFragment = ItemListFragment.newInstance();
            itemListFragment.setItinerary(mCurrentItinerary);
            fragments[1] = itemListFragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        public void updateItinerary(Itinerary itinerary)
        {
            ((EditItineraryFragment)fragments[0]).setItineraryAndUpdate(itinerary);
            ((ItemListFragment)fragments[1]).setItineraryAndUpdate(itinerary);
        }
    }
}
