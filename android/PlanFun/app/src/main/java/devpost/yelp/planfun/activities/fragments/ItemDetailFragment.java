package devpost.yelp.planfun.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;

import java.text.SimpleDateFormat;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.views.VerticalProgressBar;
import devpost.yelp.planfun.model.Item;
import info.hoang8f.widget.FButton;

/**
 * Created by Alex on 3/30/2015.
 */
public class ItemDetailFragment extends Fragment
{

    private Item currentItem;
    private TextView mTitleView, mReviewCountView, mStartTimeView, mEndTimeView;
    private FButton mCallButton, mNavButton, mWebButton;
    private VerticalProgressBar mPriceBar;
    private RatingBar mRatingView;
    private IconTextView mIconView;

    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;


    public static ItemDetailFragment newInstance()
    {
        ItemDetailFragment fragment = new ItemDetailFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_item_detail, container, false);
        mTitleView = (TextView)v.findViewById(R.id.titleView);
        //mSubtitleView = (TextView)v.findViewById(R.id.subtitleView);
        mCallButton = (FButton)v.findViewById(R.id.callButton);
        mNavButton = (FButton)v.findViewById(R.id.navButton);
        mWebButton = (FButton)v.findViewById(R.id.webButton);
        mReviewCountView = (TextView)v.findViewById(R.id.ratingCountView);
        mRatingView = (RatingBar)v.findViewById(R.id.ratingView);
        mIconView = (IconTextView)v.findViewById(R.id.iconView);
        mStartTimeView = (TextView)v.findViewById(R.id.startTimeView);
        mEndTimeView = (TextView)v.findViewById(R.id.endTimeView);
        mPriceBar = (VerticalProgressBar)v.findViewById(R.id.priceBar);

        mPriceBar.setMax(4);

        timeSdf = new SimpleDateFormat(timeFormat);

        Iconify.addIcons(mCallButton, mNavButton, mWebButton);

        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + currentItem.getYelp_item().getLocation().getCoordinate().latitude + "," + currentItem.getYelp_item().getLocation().getCoordinate().longitude));
                startActivity(intent);
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + currentItem.getYelp_item().getPhone()));
                startActivity(intent);
            }
        });

        mWebButton.setOnClickListener(v1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getYelp_item().getUrl()));
            startActivity(browserIntent);
        });

        return v;
    }

    public void updateItem(Item item)
    {
        currentItem = item;

        mTitleView.setText(currentItem.getName());
        //ImageLoader loader = new ImageLoader(mImageView);
        //loader.execute(currentItem.getYelp_item().getImage_url());
        mRatingView.setRating(currentItem.getYelp_item().getRating());
        //mSubtitleView.setText(PhoneNumberUtils.formatNumber(currentItem.getYelp_item().getPhone(), "US"));
        mReviewCountView.setText(" - "+currentItem.getYelp_item().getReview_count()+" reviews");

        mStartTimeView.setText(timeSdf.format(currentItem.getStart_time().getTime()));
        mEndTimeView.setText(timeSdf.format(currentItem.getEnd_time().getTime()));

        mPriceBar.setProgress(item.getYelp_item().getPrice());

        switch(item.getYelp_category().getName())
        {
            case "breakfast":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_coffee);
                break;
            case "lunch":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_cutlery);
                break;
            case "dinner":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_cutlery);
                break;
            case "nightlife":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_glass);
                break;
            case "attraction":
                Iconify.setIcon(mIconView, Iconify.IconValue.fa_futbol_o);
                break;
        }


    }


}
