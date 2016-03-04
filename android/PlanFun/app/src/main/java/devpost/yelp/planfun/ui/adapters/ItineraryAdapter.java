package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.ui.views.RoundedImageView;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.model.Share;

/**
 * Created by Alex on 3/10/2015.
 */
public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder>
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
    private List<Itinerary> mItems;
    private Context mContext;

    public ItineraryAdapter(List<Itinerary> items, Context context)
    {
        this.mItems = items;
        this.mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itinerary_list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Itinerary itinerary = mItems.get(i);
        viewHolder.mTitleView.setText(itinerary.getName());
        viewHolder.mSubtitleView.setText(itinerary.getCity());
        viewHolder.itemView.setTag(i);
        viewHolder.userLayout.removeAllViews();
        ImageView ownerImageView = createImageView(itinerary.getUser().getFacebook_id(), false);
        viewHolder.userLayout.addView(ownerImageView);
        for(Share share: itinerary.getShared_users())
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
