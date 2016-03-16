package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.ui.events.EditItemRequest;
import devpost.yelp.planfun.ui.events.FindItemRequest;

/**
 * Created by Alex on 3/10/2015.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>
{
    public static class ItemViewHolder extends YelpEntryAdapter.YelpEntryViewHolder
    {
        public TextView mStartTimeView;
        public TextView mEndTimeView;
        public Button mCreateButton;
        public Button mFindButton;

        public ItemViewHolder(Context context, View itemView) {
            super(context, itemView);
            mStartTimeView = (TextView) itemView.findViewById(R.id.startTimeView);
            mEndTimeView = (TextView) itemView.findViewById(R.id.endTimeView);
            mFindButton = (Button)itemView.findViewById(R.id.item_find);
            mCreateButton = (Button)itemView.findViewById(R.id.item_create);
        }

        public void fillIn(Item item){
            if(item.getYelp_item()!=null)
                super.fillIn(item.getYelp_item());
            else{
                itemView.findViewById(R.id.yelp_entry_rating_layout).setVisibility(View.GONE);
            }
            mTitleView.setText(item.getName());

            YelpCategory cat = item.getYelp_category();
            if(cat!=null) {
                try {
                    mIconView.setIcon(Util.iconFromString(cat.getIcon_string()));
                } catch (IllegalArgumentException iaex) {
                    Log.e("ICON", "No icon found for " + cat.getIcon_string());
                    mIconView.setImageResource(0);
                }
            }
            mStartTimeView.setText(PlanFunApplication.TIME_FORMAT.format(item.getStart_time().getTime()));
            mEndTimeView.setText(PlanFunApplication.TIME_FORMAT.format(item.getEnd_time().getTime()));
        }
    }

    private static final View.OnClickListener FIND_LISTENER = v -> PlanFunApplication.getBus().post(new FindItemRequest());
    private static final View.OnClickListener CREATE_LISTENER = v -> PlanFunApplication.getBus().post(new EditItemRequest());
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

    private int last;

    @Override
    public int getItemViewType(int i){
        Log.i("ITEM_ADAPTER","Getting layout for "+i+" with "+getItemCount());
        if(i>getItemCount())
            i=last;
        else
            last=i;
        int layout = R.layout.item_list_item;
        if(i==getItemCount()-1 && addButton){
            layout = R.layout.item_list_buttons;
        }
        return layout;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layout = getItemViewType(i);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ItemViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i)
    {
        if(itemViewHolder.mFindButton !=null){
            itemViewHolder.mFindButton.setOnClickListener(FIND_LISTENER);
            itemViewHolder.mFindButton.setCompoundDrawables(MaterialDrawableBuilder.with(mContext)
                    .setIcon(MaterialDrawableBuilder.IconValue.MAGNIFY)
                    .setColor(Color.BLACK)
                    .setToActionbarSize()
                    .build(), null, null, null);
            itemViewHolder.mCreateButton.setOnClickListener(CREATE_LISTENER);
            itemViewHolder.mCreateButton.setCompoundDrawables(MaterialDrawableBuilder.with(mContext)
                    .setIcon(MaterialDrawableBuilder.IconValue.PLUS)
                    .setColor(Color.BLACK)
                    .setToActionbarSize()
                    .build(), null, null, null);
        }else if(itemViewHolder.mStartTimeView!=null) {
            Item item = mItems.get(i);
            itemViewHolder.fillIn(item);
        }
    }

    @Override
    public int getItemCount() {
        return (mItems==null ? 0: mItems.size())+(addButton?1:0);
    }


}
