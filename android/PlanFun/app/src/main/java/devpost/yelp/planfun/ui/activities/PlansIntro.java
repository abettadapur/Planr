package devpost.yelp.planfun.ui.activities;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import devpost.yelp.planfun.R;

/**
 * Created by alexb on 3/11/2016.
 */
public class PlansIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Welcome to Planr", "Planr is a new way to find things to do in your city", R.drawable.planfun, R.color.primaryColor));
        addSlide(AppIntroFragment.newInstance("Choose a city and go!", "Choose any starting location and have Planr find things to do around you", R.drawable.city, R.color.primaryColor));
        addSlide(AppIntroFragment.newInstance("The more the merrier", "Share your created plan with your Facebook friends to make coordinating a no brainer!", R.drawable.share, R.color.primaryColor));
        addSlide(AppIntroFragment.newInstance("Stay Flexible", "Movie night at a friend's? Birthday dinner? Add your own custom events to make a plan truly yours", R.drawable.map_search, R.color.primaryColor));

        setFlowAnimation();
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }
}
