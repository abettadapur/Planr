package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.ui.events.EditItemRequest;

/**
 * Created by Alex on 3/10/2015.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitleView;
        public TextView mSubtitleView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView)itemView.findViewById(R.id.titleView);
            mSubtitleView = (TextView)itemView.findViewById(R.id.detailView);
        }
    }
    private List<Item> mItems;
    private Context mContext;
    private boolean addButton;

    public ItemAdapter(List<Item> items, Context context){
        this(items,context,false);
    }

    public ItemAdapter(List<Item> items, Context context, boolean addButton)
    {
        this.mItems = items;
        this.mContext=context;
        this.addButton = addButton;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layout = R.layout.item_list_item;
        if(i==getItemCount()-1 && addButton){
            layout = R.layout.item_list_add_button;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        if(i==getItemCount()-1 && addButton){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlanFunApplication.getBus().post(new EditItemRequest(true));
                }
            });
        }else {
            Item item = mItems.get(i);
            viewHolder.mTitleView.setText(item.getName());
            viewHolder.mSubtitleView.setText(item.getYelp_category().getName());
        }
    }

    @Override
    public int getItemCount() {
        return (mItems==null ? 0: mItems.size())+(addButton?1:0);
    }
}
