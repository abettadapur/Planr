package devpost.yelp.planfun.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.ui.views.RoundedImageView;
import devpost.yelp.planfun.model.User;

/**
 * Created by alexb on 3/2/2016.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>
{
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public RoundedImageView mProfileImage;
        public TextView mName;

        public ViewHolder(View friendView)
        {
            super(friendView);
            mProfileImage = (RoundedImageView)friendView.findViewById(R.id.profileView);
            mName = (TextView)friendView.findViewById(R.id.nameView);
        }
    }

    private List<User> mItems;
    private Context mContext;

    public FriendsAdapter(List<User> friends, Context context)
    {
        this.mItems = friends;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        User user = mItems.get(position);
        Picasso.with(mContext).load("https://graph.facebook.com/"+user.getFacebook_id()+"/picture?type=normal").into(viewHolder.mProfileImage);
        viewHolder.mName.setText(user.getFirst_name()+" "+user.getLast_name());
    }


    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }
}
