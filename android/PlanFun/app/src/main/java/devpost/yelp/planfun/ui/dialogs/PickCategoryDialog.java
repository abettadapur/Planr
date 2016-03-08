package devpost.yelp.planfun.ui.dialogs;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.CategoryAdapter;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.ui.events.AddCategoryRequest;
import devpost.yelp.planfun.ui.listutils.SimpleItemTouchHelperCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexb on 3/8/2016.
 */
public class PickCategoryDialog extends DialogFragment implements RecyclerItemClickListener.OnItemClickListener
{
    @Bind(R.id.categoryListView)
    RecyclerView mCategoryListView;
    @Bind(R.id.loadingCircle)
    ProgressBar mLoadingCircle;

    private RestClient mRestClient;
    private CategoryAdapter mCategoryAdapter;
    private List<YelpCategory> mCategories;

    public PickCategoryDialog()
    {
        mRestClient = RestClient.getInstance();
        mCategories = new ArrayList<>();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder b = new MaterialDialog.Builder(getActivity())
                .title("Categories")
                .negativeText("Close")
                .onNegative((dialog, which)->dialog.dismiss());

        LayoutInflater i = getActivity().getLayoutInflater();
        View v = i.inflate(R.layout.dialog_pick_category, null);
        ButterKnife.bind(this, v);

        mCategoryAdapter = new CategoryAdapter(mCategories, getActivity());
        mCategoryListView.setAdapter(mCategoryAdapter);
        mCategoryListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCategoryListView.setItemAnimator(new DefaultItemAnimator());
        mCategoryListView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

        setLoading(true);
        Call<List<YelpCategory>> categoryCall = mRestClient.getCategoryService().getCategories();
        categoryCall.enqueue(new Callback<List<YelpCategory>>() {
            @Override
            public void onResponse(Call<List<YelpCategory>> call, Response<List<YelpCategory>> response) {
                if (response.isSuccess()) {
                    getActivity().runOnUiThread(() ->
                    {
                        mCategories.clear();
                        mCategories.addAll(response.body());
                        mCategoryAdapter.notifyDataSetChanged();
                        setLoading(false);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<YelpCategory>> call, Throwable t) {

            }
        });
        b.customView(v, false);
        return b.build();
    }

    @Override
    public void onItemClick(View childView, int position)
    {
        YelpCategory category = mCategories.get(position);
        PlanFunApplication.getBus().post(new AddCategoryRequest(category));
        this.dismiss();
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    private void setLoading(boolean loading)
    {
        mCategoryListView.setVisibility(loading?View.GONE:View.VISIBLE);
        mLoadingCircle.setVisibility(loading?View.VISIBLE:View.GONE);
    }
}
