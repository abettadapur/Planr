package devpost.yelp.planfun.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.ui.adapters.LargeItemAdapter;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.ui.events.ItemDetailRequest;

/**
 * Created by alexb on 3/10/2016.
 */
public class ItemListFragment extends Fragment implements RecyclerItemClickListener.OnItemClickListener {
    private List<Item> mItems;

    @Bind(R.id.itemsView)
    RecyclerView mItemView;

    private LargeItemAdapter mItemAdapter;

    public static ItemListFragment newInstance(ArrayList<Item> items)
    {
        Bundle args = new Bundle();
        args.putParcelableArrayList("items", items);
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, v);
        Bundle args = getArguments();
        mItems = args.getParcelableArrayList("items");

        mItemAdapter = new LargeItemAdapter(mItems, getContext());
        mItemView.setAdapter(mItemAdapter);
        mItemView.setLayoutManager(new LinearLayoutManager(getContext()));
        mItemView.setItemAnimator(new DefaultItemAnimator());
        mItemView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(View childView, int position)
    {
        Item item = mItems.get(position);
        PlanFunApplication.getBus().post(new ItemDetailRequest(item));
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }
}
