package devpost.yelp.planfun.ui.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.events.EditPlanRequest;
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import devpost.yelp.planfun.ui.events.SharePlanRequest;
import devpost.yelp.planfun.ui.views.RoundedImageView;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.Share;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 3/10/2015.
 */
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>
{
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitleView;
        public TextView mSubtitleView;
        public SwipeLayout mSwipeDelete;
        public View itemView;
        public LinearLayout userLayout;
        public ImageButton mShareView,mEditView,mDeleteView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.titleView);
            mSubtitleView = (TextView)itemView.findViewById(R.id.detailView);
            userLayout = (LinearLayout)itemView.findViewById(R.id.usersLayout);
            mSwipeDelete = (SwipeLayout)itemView.findViewById(R.id.planSwipeLayout);
            mSwipeDelete.setShowMode(SwipeLayout.ShowMode.PullOut);
            mSwipeDelete.setDragEdge(SwipeLayout.DragEdge.Right);
            mShareView = (ImageButton)itemView.findViewById(R.id.sharePlanButton);
            mEditView = (ImageButton)itemView.findViewById(R.id.editPlanButton);
            mDeleteView = (ImageButton)itemView.findViewById(R.id.deletePlanButton);

            mShareView.setImageDrawable(MaterialDrawableBuilder
                    .with(mContext)
                    .setIcon(MaterialDrawableBuilder.IconValue.ACCOUNT_PLUS)
                    .setColor(Color.WHITE)
                    .build());

            mDeleteView.setImageDrawable(MaterialDrawableBuilder
                    .with(mContext)
                    .setIcon(MaterialDrawableBuilder.IconValue.DELETE)
                    .setColor(Color.WHITE)
                    .build());

            this.itemView = itemView;
        }
    }
    private List<Plan> mItems;
    private Activity mContext;

    public PlanAdapter(List<Plan> items, Activity context)
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
        v.setOnClickListener(view -> {
            int itinerary_id = mItems.get(i).getId();
            PlanFunApplication.getBus().post(new OpenPlanRequest(itinerary_id, false));
        });
        return new ViewHolder(v);
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

        viewHolder.mShareView.setOnClickListener(v -> {
            PlanFunApplication.getBus().post(new SharePlanRequest(plan));
        });

        viewHolder.mEditView.setOnClickListener(v -> {
            PlanFunApplication.getBus().post(new EditPlanRequest(plan));
        });

        viewHolder.mDeleteView.setImageDrawable(MaterialDrawableBuilder
                .with(mContext)
                .setIcon(MaterialDrawableBuilder.IconValue.DELETE)
                .setColor(Color.WHITE)
                .build());

        viewHolder.mDeleteView.setOnClickListener(v -> new MaterialDialog.Builder(mContext)
                .title("Delete plan?")
                .content("Are you sure you want to delete plan " + plan.getName())
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive((materialDialog, dialogAction) -> {
                    Call<ResponseBody> call = RestClient.getInstance().getPlanService().deletePlan(plan.getId());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccess()) {
                                mContext.runOnUiThread(() -> {
                                    PlanAdapter.this.mItems.remove(i);
                                    PlanAdapter.this.notifyItemRemoved(i);
                                });
                            }
                        }


                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                })
                .icon(MaterialDrawableBuilder
                        .with(mContext)
                        .setIcon(MaterialDrawableBuilder.IconValue.DELETE)
                        .build())
                .show());
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
