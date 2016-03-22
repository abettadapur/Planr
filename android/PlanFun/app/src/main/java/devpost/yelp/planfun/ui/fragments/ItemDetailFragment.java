package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.text.SimpleDateFormat;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.Item;

/**
 * Created by Alex on 3/30/2015.
 */
public class ItemDetailFragment extends Fragment
{

    private Item currentItem;
    private TextView mTitleView, mReviewCountView, mStartTimeView, mEndTimeView, mRatingValueView, mAddressView, mDescriptionView;
    private View mYelpEntryView;
    private Button mCallButton, mNavButton, mWebButton;
    private RatingBar mRatingView;
    private MaterialIconView mIconView;
    private LinearLayout mLocationLayout;

    public static ItemDetailFragment newInstance(Item i)
    {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", i);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        currentItem = args.getParcelable("item");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_detail, container, false);
        mTitleView = (TextView)v.findViewById(R.id.titleView);
        mTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        v.findViewById(R.id.itemView).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mYelpEntryView = v.findViewById(R.id.yelp_entry_rating_layout);
        mCallButton = (Button)v.findViewById(R.id.callButton);
        mNavButton = (Button)v.findViewById(R.id.navButton);
        mWebButton = (Button)v.findViewById(R.id.webButton);
        mReviewCountView = (TextView)v.findViewById(R.id.ratingCountView);
        mReviewCountView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mRatingView = (RatingBar)v.findViewById(R.id.ratingView);
        mRatingValueView = (TextView)v.findViewById(R.id.ratingValueView);
        mRatingValueView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mIconView = (MaterialIconView)v.findViewById(R.id.iconView);
        mIconView.setColor(ContextCompat.getColor(getContext(), R.color.white));
        mStartTimeView = (TextView)v.findViewById(R.id.startTimeView);
        mStartTimeView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        TextView divView = (TextView) v.findViewById(R.id.timeDivView);
        divView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mEndTimeView = (TextView)v.findViewById(R.id.endTimeView);
        mEndTimeView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mAddressView = (TextView)v.findViewById(R.id.addressView);
        mLocationLayout = (LinearLayout)v.findViewById(R.id.locationLayout);
        mLocationLayout.setOnClickListener((v1)->
        {
            Uri gmmIntentUri = Uri.parse("geo:"+currentItem.getLocation().getCoordinate().latitude+","+currentItem.getLocation().getCoordinate().longitude
                    +"?q=" + Uri.encode(mAddressView.getText().toString()));
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(intent);
        });
        mDescriptionView = (TextView)v.findViewById(R.id.descriptionView);

        mCallButton.setCompoundDrawables(null, MaterialDrawableBuilder
                .with(getActivity())
                .setIcon(MaterialDrawableBuilder.IconValue.PHONE)
                .setColor(Color.WHITE)
                .build()
            ,null,null);

        mNavButton.setCompoundDrawables(null, MaterialDrawableBuilder
                .with(getActivity())
                .setIcon(MaterialDrawableBuilder.IconValue.NAVIGATION)
                .setColor(Color.WHITE)
                .build()
                ,null,null);

        mWebButton.setCompoundDrawables(null, MaterialDrawableBuilder
                .with(getActivity())
                .setIcon(MaterialDrawableBuilder.IconValue.YELP)
                .setColor(Color.WHITE)
                .build()
                , null, null);

        mNavButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=" + currentItem.getYelp_item().getLocation().getCoordinate().latitude + "," + currentItem.getYelp_item().getLocation().getCoordinate().longitude));
            startActivity(intent);
        });

        mCallButton.setOnClickListener(v1 -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + currentItem.getYelp_item().getPhone()));
            startActivity(intent);
        });

        mWebButton.setOnClickListener(v1 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getYelp_item().getUrl()));
            startActivity(browserIntent);
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView();
    }

    public void updateView()
    {
        mTitleView.setText(currentItem.getName());
        //ImageLoader loader = new ImageLoader(mImageView);
        //loader.execute(currentItem.getYelp_item().getImage_url());
        if(currentItem.getYelp_item() != null) {
            mRatingView.setRating(currentItem.getYelp_item().getRating());
            mRatingValueView.setText(currentItem.getYelp_item().getRating() + "");
            mReviewCountView.setText(" - " + currentItem.getYelp_item().getReview_count() + " reviews");
        }
        else
        {
            mYelpEntryView.setVisibility(View.GONE);
        }

        mStartTimeView.setText(PlanFunApplication.TIME_FORMAT.format(currentItem.getStart_time().getTime()));
        mEndTimeView.setText(PlanFunApplication.TIME_FORMAT.format(currentItem.getEnd_time().getTime()));

        mAddressView.setText(currentItem.getLocation().getAddress()+"\n"+
                             currentItem.getLocation().getCity()+", "+
                             currentItem.getLocation().getState_code()+"\n"+
                             currentItem.getLocation().getPostal_code());

        mDescriptionView.setText(currentItem.getDescription() == null || currentItem.getDescription().isEmpty() ? "No notes for this item": currentItem.getDescription());

        if(!currentItem.getYelp_category().getIcon_string().equals(""))
        {
            try {
                mIconView.setIcon(Util.iconFromString(currentItem.getYelp_category().getIcon_string()));
                mIconView.setVisibility(View.VISIBLE);
            }
            catch(IllegalArgumentException iaex)
            {
                try {
                    Drawable d = getContext().getResources().getDrawable(Util.customIconFromString(currentItem.getYelp_category().getIcon_string()).getNumVal());
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

                    float scale = getContext().getResources().getDisplayMetrics().density;
                    Drawable scaled = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, (int)(36*scale), (int)(36*scale), true));
                    mIconView.setImageDrawable(scaled);
                    mIconView.setVisibility(View.VISIBLE);
                }
                catch (IllegalArgumentException iaex2) {
                    Log.e("ICON", "No icon found for " + currentItem.getYelp_category().getIcon_string());
                    mIconView.setVisibility(View.INVISIBLE);
                }
            }
        }
        else
        {
            mIconView.setImageResource(0);
        }
    }

    public void updateItem(Item i)
    {
        currentItem = i;
        updateView();
    }


}
