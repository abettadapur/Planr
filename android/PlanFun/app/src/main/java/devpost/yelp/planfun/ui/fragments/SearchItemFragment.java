package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.model.YelpCategorySearchFilter;
import devpost.yelp.planfun.model.YelpEntry;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.ui.adapters.YelpEntryAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ros on 3/8/16.
 */
public class SearchItemFragment extends BaseFragment implements RecyclerItemClickListener.OnItemClickListener {
    private final int PLACES_AUTOCOMPLETE=10000;
    private Place autoCompleteResult;
    private String categoriesQuery, termText;
    @Bind(R.id.item_place_picker)
    MaterialEditText mPlaceBox;

    @OnClick(R.id.item_place_picker)
    public void openAutocomplete(View view)
    {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            startActivityForResult(intent, PLACES_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException grex) {

        } catch (GooglePlayServicesNotAvailableException gnaex) {

        }
    }

    private List<YelpCategory> mCategories;

    private Integer[] mCategoriesSelected;

    @Bind(R.id.item_search_categories)
    EditText mCategoriesText;

    @OnClick(R.id.item_search_categories)
    public void setCategories(View v){
        if(YelpCategory.SERVER_CATEGORIES==null){
            MaterialDialog wait_dialog = new MaterialDialog.Builder(getContext())
                    .title("Getting Categories")
                    .content("Please Wait")
                    .progress(true, 0)
                    .show();

            while(YelpCategory.SERVER_CATEGORIES==null){
            }
            wait_dialog.dismiss();
        }

        new MaterialDialog.Builder(getContext())
                .title("Choose Categories")
                .items(YelpCategory.SERVER_CATEGORIES)
                .itemsCallbackMultiChoice(mCategoriesSelected, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        mCategoriesSelected = which;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuffer buf = new StringBuffer();
                                for(CharSequence str:text){
                                    buf.append(str+",");
                                }
                                if(text.length>0)
                                    buf.deleteCharAt(buf.length()-1);
                                mCategoriesText.setText(buf.toString());

                                categoriesQuery = null;
                                if(!buf.toString().equals("")){
                                    StringBuffer queryBuf = new StringBuffer();
                                    for(Integer i:which){
                                        for(YelpCategorySearchFilter filter: YelpCategory.SERVER_CATEGORIES.get(i).getSearch_filters()){
                                            queryBuf.append(filter.getFilter()+",");
                                        }
                                    }
                                    queryBuf.deleteCharAt(queryBuf.length()-1);
                                    categoriesQuery = queryBuf.toString();
                                }
                                doQuery();
                            }
                        });
                        return true;
                    }
                })
                .positiveText("Done")
                .negativeText("Cancel")
                .show();
    }

    @Bind(R.id.item_search_term)
    MaterialEditText mTermText;

    @OnEditorAction(R.id.item_search_term)
    public boolean onTermEntered(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (!event.isShiftPressed()) {
                termText=v.getText().toString().equals("") ? null : v.getText().toString();
                //hide keyboard
                View view = this.getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                doQuery();
                return true; // consume.
            }
        }
        return false; // pass on to other listeners.
    }

    @Bind(R.id.item_search_results)
    RecyclerView mItemsView;

    private YelpEntryAdapter mAdapter;

    @Bind(R.id.item_search_loading)
    ProgressBar mProgressView;

    private RestClient mRestClient;

    public static SearchItemFragment newInstance()
    {
        SearchItemFragment fragment = new SearchItemFragment();
        return fragment;
    }

    public SearchItemFragment()
    {
        mRestClient = RestClient.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_search, container, false);
        ButterKnife.bind(this, v);
        mAdapter = new YelpEntryAdapter(null,this.getActivity());
        mItemsView.setAdapter(mAdapter);
        mItemsView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Find Activity");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACES_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                autoCompleteResult = PlaceAutocomplete.getPlace(getContext(), data);
                mPlaceBox.setText(autoCompleteResult.getAddress());
                doQuery();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error?

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doQuery(){
        setLoading(true);
        Call<List<YelpEntry>> itemsCall = mRestClient.getSearchService().searchItems(
                autoCompleteResult.getLatLng().latitude,
                autoCompleteResult.getLatLng().longitude,
                categoriesQuery,
                termText);
        itemsCall.enqueue(new Callback<List<YelpEntry>>() {
            @Override
            public void onResponse(Call<List<YelpEntry>> call, Response<List<YelpEntry>> response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        mAdapter.setItems(response.body());
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<YelpEntry>> call, Throwable t) {
            }
        });
    }

    public void setLoading(boolean loading){
        if(!loading){
            mItemsView.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        }else{
            mItemsView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View childView, int position) {
        YelpEntry clicked = mAdapter.getEntries().get(position);
        //EditItemDialog dialog = EditItemDialog.newInstance(clicked);
        //dialog.show(getActivity().getSupportFragmentManager(), "fm");
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        //TODO dialog with info
    }
}
