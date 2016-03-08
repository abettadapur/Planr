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
import devpost.yelp.planfun.model.YelpCategory;

/**
 * Created by alexb on 3/8/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.categoryIconView)
        public MaterialIconView mCategoryIcon;
        @Bind(R.id.categoryNameView)
        public TextView mCategoryName;

        public ViewHolder(View v)
        {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    private List<YelpCategory> mCategories;
    private Context mContext;

    public CategoryAdapter(List<YelpCategory> categories, Context context) {
        mCategories = categories;
        mContext = context;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        YelpCategory category = mCategories.get(position);
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
    }

    @Override
    public int getItemCount() {
        return mCategories == null? 0 : mCategories.size();
    }
}
