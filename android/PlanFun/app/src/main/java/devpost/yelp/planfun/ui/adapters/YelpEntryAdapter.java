package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.model.YelpEntry;
import devpost.yelp.planfun.ui.events.EditItemRequest;
import devpost.yelp.planfun.ui.events.FindItemRequest;

/**
 * Created by Andrey on 3/11/16.
 */
public class YelpEntryAdapter extends RecyclerView.Adapter<YelpEntryAdapter.YelpEntryViewHolder>
{
    public static class YelpEntryViewHolder extends RecyclerView.ViewHolder
    {
        public MaterialIconView mIconView;
        public TextView mTitleView;
        public TextView mRatingTextView;
        public RatingBar mRatingView;
        public TextView mReviewCountView;
        private Context mContext;

        public YelpEntryViewHolder(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mIconView = (MaterialIconView) itemView.findViewById(R.id.iconView);
            mTitleView = (TextView) itemView.findViewById(R.id.titleView);
            mRatingTextView = (TextView) itemView.findViewById(R.id.ratingValueView);
            mRatingView = (RatingBar) itemView.findViewById(R.id.ratingView);
            mReviewCountView = (TextView) itemView.findViewById(R.id.ratingCountView);
        }

        public void fillIn(YelpEntry entry){
            mTitleView.setText(entry.getName());
            mRatingView.setRating(entry.getRating());
            mRatingView.setProgressTintList(mContext.getResources().getColorStateList(R.color.progress_bar, null));
            mRatingTextView.setText(entry.getRating() + "");
            mReviewCountView.setText(" - "+entry.getReview_count()+" reviews");

            if(entry.getCategories().size()>0 && !entry.getCategories().get(0).getIcon_string().equals(""))
            {
                YelpCategory cat = entry.getMain_category();
                try {
                    mIconView.setIcon(Util.iconFromString(cat.getIcon_string()));
                }
                catch(IllegalArgumentException iaex)
                {
                    Log.e("ICON", "No icon found for " + cat.getIcon_string());
                    mIconView.setImageResource(0);
                }
            }
            else
            {
                mIconView.setImageResource(0);
            }
        }
    }

    private static final View.OnClickListener FIND_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlanFunApplication.getBus().post(new FindItemRequest());
        }
    };
    private static final View.OnClickListener CREATE_LISTENER = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PlanFunApplication.getBus().post(new EditItemRequest());
        }
    };
    private List<YelpEntry> mEntries;
    private Context mContext;

    public YelpEntryAdapter(List<YelpEntry> items, Context context)
    {
        this.mEntries = items;
        this.mContext=context;
    }

    public YelpEntryAdapter(Context context)
    {
        this(null,context);
    }

    public void setItems(List<YelpEntry> items){
        mEntries = items;
    }

    public List<YelpEntry> getEntries(){
        return mEntries;
    }

    @Override
    public YelpEntryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_yelp_entry, viewGroup, false);
        return new YelpEntryViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(YelpEntryViewHolder entryViewHolder, int i)
    {
        YelpEntry entry = mEntries.get(i);
        entryViewHolder.fillIn(entry);
    }

    @Override
    public int getItemCount() {
        return (mEntries==null ? 0: mEntries.size());
    }
}
