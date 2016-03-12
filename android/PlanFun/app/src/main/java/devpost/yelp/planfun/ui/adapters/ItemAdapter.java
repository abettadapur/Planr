package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.ui.events.EditItemRequest;
import devpost.yelp.planfun.ui.events.FindItemRequest;

/**
 * Created by Alex on 3/10/2015.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>
{
    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitleView;
        public TextView mSubtitleView;

        public ItemViewHolder(View itemView, boolean item) {
            super(itemView);
            if(item) {
                mTitleView = (TextView) itemView.findViewById(R.id.titleView);
                mSubtitleView = (TextView) itemView.findViewById(R.id.detailView);
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

    public ItemAdapter(Context context)
    {
        this(null,context,false);
    }

    public void setItems(List<Item> items){
        mItems = items;
    }

    public List<Item> getItems(){
        return mItems;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layout = R.layout.item_list_item;
        boolean item = true;
        if(i==getItemCount()-1 && addButton){
            layout = R.layout.item_list_buttons;
            item = false;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ItemViewHolder(v,item);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i)
    {
        if(i==getItemCount()-1 && addButton){
            itemViewHolder.itemView.findViewById(R.id.item_find).setOnClickListener(FIND_LISTENER);
            ((Button)itemViewHolder.itemView.findViewById(R.id.item_find))
                    .setCompoundDrawables(MaterialDrawableBuilder.with(mContext)
                            .setIcon(MaterialDrawableBuilder.IconValue.MAGNIFY)
                            .setColor(Color.BLACK)
                            .setToActionbarSize()
                            .build(), null, null, null);
            itemViewHolder.itemView.findViewById(R.id.item_create).setOnClickListener(CREATE_LISTENER);
            ((Button)itemViewHolder.itemView.findViewById(R.id.item_create))
                    .setCompoundDrawables(MaterialDrawableBuilder.with(mContext)
                            .setIcon(MaterialDrawableBuilder.IconValue.PLUS)
                            .setColor(Color.BLACK)
                            .setToActionbarSize()
                            .build(), null, null, null);
        }else {
            Item item = mItems.get(i);
            itemViewHolder.mTitleView.setText(item.getName());
            itemViewHolder.mSubtitleView.setText(item.getYelp_category().getName());
        }
    }

    @Override
    public int getItemCount() {
        return (mItems==null ? 0: mItems.size())+(addButton?1:0);
    }
}
