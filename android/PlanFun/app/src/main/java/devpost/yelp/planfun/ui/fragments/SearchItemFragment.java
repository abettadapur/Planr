package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.ItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ros on 3/8/16.
 */
public class SearchItemFragment extends BaseFragment {
    private final int PLACES_AUTOCOMPLETE=10000;
    private Place autoCompleteResult;

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
                                for(int i=0;i<text.length;i++){
                                    buf.append(text[i]);
                                    if(i!=text.length-1)
                                        buf.append(",");
                                }
                                mCategoriesText.setText(buf.toString());
                            }
                        });
                        return true;
                    }
                })
                .positiveText("Done")
                .negativeText("Cancel")
                .show();
    }

    @Bind(R.id.item_search_results)
    RecyclerView mItemsView;

    private ItemAdapter mAdapter;

    @Bind(R.id.item_search_loading)
    ProgressBar mProgressView;

    @Bind(R.id.cancel_item_add)
    Button mCancelButton;

    @OnClick(R.id.cancel_item_add)
    public void cancelClicked(View view){

    }

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
        mAdapter = new ItemAdapter(null,this.getActivity());
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
                // TODO: Handle the error.

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doQuery(){
        mItemsView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);

        Call<List<Item>> itemsCall = mRestClient.getSearchService().searchItems(autoCompleteResult.getLatLng().latitude,
                autoCompleteResult.getLatLng().longitude);
        itemsCall.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mItemsView.setVisibility(View.VISIBLE);
                        mProgressView.setVisibility(View.GONE);
                        mAdapter.setItems(response.body());
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
            }
        });
    }
}
