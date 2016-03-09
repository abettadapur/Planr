package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.ui.listutils.ItemTouchHelperAdapter;
import devpost.yelp.planfun.ui.listutils.OnStartDragListener;

/**
 * Created by alexb on 3/8/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements ItemTouchHelperAdapter
{

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.categoryIconView)
        public MaterialIconView mCategoryIcon;
        @Bind(R.id.categoryNameView)
        public TextView mCategoryName;
        @Bind(R.id.handle)
        public MaterialIconView mHandle;

        public ViewHolder(View v)
        {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    private List<YelpCategory> mCategories;
    private Context mContext;
    private OnStartDragListener mDragListener;
    private boolean showHandle;

    public CategoryAdapter(List<YelpCategory> categories, Context context) {
        mCategories = categories;
        mContext = context;
        showHandle = false;
    }

    public CategoryAdapter(List<YelpCategory> categories, Context context, OnStartDragListener dragListener) {
        mCategories = categories;
        mContext = context;
        mDragListener = dragListener;
        showHandle = true;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        YelpCategory category = mCategories.get(position);
        holder.mHandle.setVisibility(showHandle?View.VISIBLE:View.GONE);
        holder.mCategoryName.setText(category.getName());

        if(category.getIcon_string() != null && !category.getIcon_string().equals(""))
        {
            try {
                holder.mCategoryIcon.setIcon(Util.fromString(category.getIcon_string()));
                holder.mCategoryIcon.setVisibility(View.VISIBLE);
            }
            catch(IllegalArgumentException iaex)
            {
                Log.e("ICON", "No icon found for " + category.getIcon_string());
                holder.mCategoryIcon.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            holder.mCategoryIcon.setVisibility(View.INVISIBLE);
        }

        holder.mHandle.setOnTouchListener((v, event) -> {
            mDragListener.onStartDrag(holder);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mCategories == null? 0 : mCategories.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mCategories, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mCategories, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position)
    {
        mCategories.remove(position);
        notifyItemRemoved(position);
    }
}
