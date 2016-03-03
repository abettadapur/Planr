package devpost.yelp.planfun.activities.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialize.color.Material;

import java.util.ArrayList;
import java.util.List;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.adapters.FriendsAdapter;
import devpost.yelp.planfun.activities.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.activities.adapters.UsersAdapter;
import devpost.yelp.planfun.activities.views.RoundedImageView;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.model.Share;
import devpost.yelp.planfun.model.User;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.net.requests.ShareRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexb on 3/2/2016.
 */
public class ShareItineraryDialog extends DialogFragment implements RecyclerItemClickListener.OnItemClickListener
{
    private LinearLayout mShareLayout;
    private RecyclerView mFriendsListView;
    private RecyclerView mSharedFriendsListView;
    private ProgressBar mLoadingCircle;
    private RestClient mRestClient;

    private List<User> friends;
    private List<User> sharedFriends;
    private FriendsAdapter mFriendsAdapter;
    private UsersAdapter mUsersAdapter;
    private Itinerary itinerary;

    public ShareItineraryDialog(Itinerary itinerary)
    {
        this.itinerary = itinerary;
        friends = new ArrayList<>();
        sharedFriends = new ArrayList<>();
        for(Share s: itinerary.getShared_users())
        {
            sharedFriends.add(s.getUser());
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder b = new MaterialDialog.Builder(getActivity())
                .title("Share")
                .positiveText("Share")
                .negativeText("Close")
                .onPositive((dialog, which) ->
                {
                    List<ShareRequest> shares = new ArrayList<>();
                    for(User u: sharedFriends)
                    {
                        shares.add(new ShareRequest(u.getId()+"", "READ"));
                    }
                    Call<Itinerary> shareCall = mRestClient.getItineraryService().shareItinerary(itinerary.getId(), shares);
                    final MaterialDialog loading = new MaterialDialog.Builder(getActivity())
                            .title("Sharing")
                            .content("Sharing your plan...")
                            .progress(true, 0)
                            .show();
                    shareCall.enqueue(new Callback<Itinerary>() {
                        @Override
                        public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
                            loading.dismiss();
                            if(response.isSuccess())
                            {
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Itinerary> call, Throwable t) {

                        }
                    });
                })
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                });

        LayoutInflater i = getActivity().getLayoutInflater();
        View v = i.inflate(R.layout.dialog_share_itinerary, null);
        mShareLayout = (LinearLayout)v.findViewById(R.id.shareLayout);
        mFriendsListView = (RecyclerView)v.findViewById(R.id.friendsListView);
        mSharedFriendsListView = (RecyclerView)v.findViewById(R.id.sharedUsersView);
        mLoadingCircle = (ProgressBar)v.findViewById(R.id.loadingCircle);
        mRestClient = RestClient.getInstance();

        mFriendsAdapter = new FriendsAdapter(friends, getActivity());
        mFriendsListView.setAdapter(mFriendsAdapter);
        mFriendsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFriendsListView.setItemAnimator(new DefaultItemAnimator());
        mFriendsListView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));

        mUsersAdapter = new UsersAdapter(sharedFriends, getActivity());
        mSharedFriendsListView.setAdapter(mUsersAdapter);
        mSharedFriendsListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mSharedFriendsListView.setItemAnimator(new DefaultItemAnimator());
        mSharedFriendsListView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                User user = sharedFriends.get(position);
                sharedFriends.remove(position);
                friends.add(user);
                mFriendsAdapter.notifyDataSetChanged();
                mUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongPress(View childView, int position) {

            }
        }));

        setLoading(true);
        Call<List<User>> friendsCall =  mRestClient.getFriendsService().getFriends();
        friendsCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccess())
                {
                    getActivity().runOnUiThread(()-> {
                        friends.clear();
                        for(User u: sharedFriends)
                        {
                            response.body().remove(u);
                        }
                        friends.addAll(response.body());
                        mFriendsAdapter.notifyDataSetChanged();
                        setLoading(false);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        b.customView(v, false);
        return b.build();
    }

    private void setLoading(boolean loading)
    {
        mShareLayout.setVisibility(loading?View.GONE:View.VISIBLE);
        mLoadingCircle.setVisibility(loading?View.VISIBLE:View.GONE);
    }

    @Override
    public void onItemClick(View childView, int position)
    {
        User user = friends.get(position);
        friends.remove(position);
        sharedFriends.add(user);

        mFriendsAdapter.notifyDataSetChanged();
        mUsersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

}
