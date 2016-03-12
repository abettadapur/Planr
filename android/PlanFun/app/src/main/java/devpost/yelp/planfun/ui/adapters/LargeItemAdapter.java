package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.Item;

/**
 * Created by alexb on 3/10/2016.
 */
public class LargeItemAdapter extends RecyclerView.Adapter<LargeItemAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.titleView)
        public TextView mTitleView;
        @Bind(R.id.detailView)
        public TextView mAddressView;
        @Bind(R.id.categoryIcon)
        public MaterialIconView mCategoryIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private List<Item> mItems;
    private Context mContext;

    public LargeItemAdapter(List<Item> items, Context context)
    {
        mItems = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_large_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Item item = mItems.get(position);
        holder.mTitleView.setText(item.getName());
        holder.mAddressView.setText(item.getLocation().getAddress());

        if(item.getYelp_category().getIcon_string() != null && !item.getYelp_category().getIcon_string().equals(""))
        {
            try {
                holder.mCategoryIcon.setIcon(Util.iconFromString(item.getYelp_category().getIcon_string()));
                holder.mCategoryIcon.setVisibility(View.VISIBLE);
            }
            catch(IllegalArgumentException iaex)
            {
                Log.e("ICON", "No icon found for " + item.getYelp_category().getIcon_string());
                holder.mCategoryIcon.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            holder.mCategoryIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }
}
