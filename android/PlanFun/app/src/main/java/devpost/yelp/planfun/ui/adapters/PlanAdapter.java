package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.ui.views.RoundedImageView;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.Share;

/**
 * Created by Alex on 3/10/2015.
 */
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitleView;
        public TextView mSubtitleView;
        public SwipeLayout mSwipeDelete;
        public View itemView;
        public LinearLayout userLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.titleView);
            mSubtitleView = (TextView)itemView.findViewById(R.id.detailView);
            userLayout = (LinearLayout)itemView.findViewById(R.id.usersLayout);
            mSwipeDelete = (SwipeLayout)itemView.findViewById(R.id.swipeLayout);
            mSwipeDelete.setShowMode(SwipeLayout.ShowMode.LayDown);
            mSwipeDelete.setDragEdge(SwipeLayout.DragEdge.Right);
            this.itemView = itemView;
        }
    }
    private List<Plan> mItems;
    private Context mContext;

    public PlanAdapter(List<Plan> items, Context context)
    {
        this.mItems = items;
        this.mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_plan, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Plan plan = mItems.get(i);
        viewHolder.mTitleView.setText(plan.getName());

        viewHolder.mSubtitleView.setText(plan.getItems().size()+" Activities" + (plan.getCity()!=null?" in "+plan.getCity() : "") + " on "+
            PlanFunApplication.DATE_FORMAT.format(plan.getStart_time().getTime()));



        viewHolder.itemView.setTag(i);
        viewHolder.userLayout.removeAllViews();
        ImageView ownerImageView = createImageView(plan.getUser().getFacebook_id(), false);
        viewHolder.userLayout.addView(ownerImageView);
        for(Share share: plan.getShared_users())
        {
            viewHolder.userLayout.addView(createImageView(share.getUser().getFacebook_id(), true));
        }


    }

    private ImageView createImageView(String facebookId, boolean includeMargin)
    {
        float scale = mContext.getResources().getDisplayMetrics().density;
        ImageView imageView = new RoundedImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(22*scale), (int)(22*scale));
        imageView.setLayoutParams(params);
        params.leftMargin = includeMargin ? (int)(3*scale) : 0;
        Picasso.with(mContext).load("https://graph.facebook.com/"+facebookId+"/picture?type=normal").into(imageView);
        return imageView;
    }

    @Override
    public int getItemCount() {
        return mItems==null ? 0: mItems.size();
    }
}
