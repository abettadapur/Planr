package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.dialogs.ShareItineraryDialog;
import devpost.yelp.planfun.ui.events.DeletePlanRequest;
import devpost.yelp.planfun.ui.events.EditPlanRequest;
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import devpost.yelp.planfun.ui.events.SharePlanRequest;
import devpost.yelp.planfun.ui.views.RoundedImageView;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.Share;

/**
 * Created by Alex on 3/10/2015.
 */
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public Plan mPlan;
        public TextView mTitleView;
        public TextView mSubtitleView;
        public SwipeLayout mSwipeDelete;
        public View itemView;
        public LinearLayout userLayout;
        public Button mShareView,mEditView,mDeleteView;


        public ViewHolder(View itemView, int position) {
            super(itemView);

            mPlan = PlanAdapter.this.mItems.get(position);
            mTitleView = (TextView)itemView.findViewById(R.id.titleView);
            mSubtitleView = (TextView)itemView.findViewById(R.id.detailView);
            userLayout = (LinearLayout)itemView.findViewById(R.id.usersLayout);
            mSwipeDelete = (SwipeLayout)itemView.findViewById(R.id.swipeLayout);
            mSwipeDelete.setShowMode(SwipeLayout.ShowMode.PullOut);
            mSwipeDelete.setDragEdge(SwipeLayout.DragEdge.Right);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int itinerary_id = mItems.get(position).getId();
                    PlanFunApplication.getBus().post(new OpenPlanRequest(itinerary_id, false));
                }
            });
            mShareView = (Button)itemView.findViewById(R.id.sharePlanButton);
            mEditView = (Button)itemView.findViewById(R.id.editPlanButton);
            mDeleteView = (Button)itemView.findViewById(R.id.deletePlanButton);

            mShareView.setCompoundDrawables(MaterialDrawableBuilder
                    .with(mContext)
                    .setIcon(MaterialDrawableBuilder.IconValue.ACCOUNT_PLUS)
                    .setColor(Color.WHITE)
                    .build()
                    , null, null, null);
            mShareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Plan plan = mItems.get(position);
                    PlanFunApplication.getBus().post(new SharePlanRequest(plan));
                }
            });

            mEditView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Plan plan = mItems.get(position);
                    PlanFunApplication.getBus().post(new EditPlanRequest(plan));
                }
            });

            mDeleteView.setCompoundDrawables(MaterialDrawableBuilder
                    .with(mContext)
                    .setIcon(MaterialDrawableBuilder.IconValue.DELETE)
                    .setColor(Color.WHITE)
                    .build(), null, null, null);

            mDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(mContext)
                            .title("Delete plan?")
                            .content("Are you sure you want to delete plan "+mPlan.getName())
                            .positiveText("Yes")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                    RestClient.getInstance().getPlanService().deletePlan(PlanAdapter.this.mItems.get(position).getId());
                                    PlanAdapter.this.mItems.remove(position);
                                    PlanAdapter.this.notifyItemRemoved(position);
                                }
                            })
                            .icon(MaterialDrawableBuilder
                                    .with(mContext)
                                    .setIcon(MaterialDrawableBuilder.IconValue.DELETE)
                                    .build())
                            .show();
                }
            });

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

    public List<Plan> getPlans(){
        return mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_plan, viewGroup, false);
        return new ViewHolder(v,i);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Plan plan = mItems.get(i);
        viewHolder.mTitleView.setText(plan.getName());
        viewHolder.mSubtitleView.setText(plan.getItems().size()+ " Activities" + 
                                         (plan.getCity()!=null?" in "+plan.getCity() : "") + " on "+
                                       PlanFunApplication.DATE_FORMAT.format(plan.getStart_time().getTime())+", "+
                                       PlanFunApplication.TIME_FORMAT.format(plan.getStart_time().getTime())+"-"+
                                       PlanFunApplication.TIME_FORMAT.format(plan.getEnd_time().getTime()));

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
