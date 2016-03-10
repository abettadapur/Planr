package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.CategoryAdapter;
import devpost.yelp.planfun.ui.adapters.ItemAdapter;
import devpost.yelp.planfun.ui.adapters.RecyclerItemClickListener;
import devpost.yelp.planfun.ui.dialogs.PickCategoryDialog;
import devpost.yelp.planfun.ui.events.SavePlanRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ros on 3/8/16.
 */
public class EditItemFragment extends BaseFragment {
    private final int PLACES_AUTOCOMPLETE=10000;
    private Item mCurrentItem;
    private Place autoCompleteResult;

    @Bind(R.id.item_duration_picker)
    MaterialEditText mDurationBox;

    @Bind(R.id.item_time_picker)
    MaterialEditText mTimeBox;

    @OnClick(R.id.item_time_picker)
    public void startClickListener(View view){
        new TimePickerDialog(EditItemFragment.this.getActivity(), startTimeSetListener,
                mCurrentItem.getStart_time().get(Calendar.HOUR_OF_DAY),
                mCurrentItem.getStart_time().get(Calendar.MINUTE), true).show();
    }

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

    @Bind(R.id.item_search_categories)
    EditText mCategoriesText;

    @OnClick(R.id.item_search_categories)
    public void setCategories(View v){
        Call<List<YelpCategory>> categoryCall = mRestClient.getCategoryService().getCategories();

        categoryCall.enqueue(new Callback<List<YelpCategory>>() {
            @Override
            public void onResponse(Call<List<YelpCategory>> call, Response<List<YelpCategory>> response) {
                if (response.isSuccess()) {
                    getActivity().runOnUiThread(() ->
                    {
                        new MaterialDialog.Builder(getContext())
                                .title("Choose Categories")
                                .items(response.body())
                                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                        /**
                                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                         * returning false here won't allow the newly selected check box to actually be selected.
                                         * See the limited multi choice dialog example in the sample project for details.
                                         **/
                                        return true;
                                    }
                                })
                                .positiveText("Done")
                                .show();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<YelpCategory>> call, Throwable t) {

            }
        });
    }

    @Bind(R.id.items_view)
    RecyclerView mItemsView;

    private ItemAdapter mAdapter;

    @Bind(R.id.do_item_add)
    Button mAddButton;

    @OnClick(R.id.do_item_add)
    public void addClicked(View view){

    }

    @Bind(R.id.cancel_item_add)
    Button mCancelButton;

    @OnClick(R.id.cancel_item_add)
    public void cancelClicked(View view){

    }

    private String dateFormat = "MM/dd/yyyy";
    private SimpleDateFormat dateSdf;
    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;

    private RestClient mRestClient;

    public static EditItemFragment newInstance()
    {
        EditItemFragment fragment = new EditItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditItemFragment newInstance(int item_id) {
        EditItemFragment fragment = new EditItemFragment();
        Bundle args = new Bundle();
        args.putInt("item_id", item_id);
        fragment.setArguments(args);
        return fragment;
    }

    public EditItemFragment()
    {
        mRestClient = RestClient.getInstance();
    }

    public void setPlan(Item item)
    {
        mCurrentItem = item;
    }

    public void setPlanAndUpdate(Item item)
    {
        mCurrentItem = item;
        updateView();
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

        dateSdf = new SimpleDateFormat(dateFormat, Locale.US);
        timeSdf = new SimpleDateFormat(timeFormat, Locale.US);
        mAdapter = new ItemAdapter(null,this.getActivity());
        mItemsView.setAdapter(mAdapter);
        mItemsView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int planId = args.getInt("item_id", -1);
        if(planId!=-1)
        {
            Call<Plan> planCall = mRestClient.getItineraryService().getItinerary(planId);
            planCall.enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    //mCurrentImte = response.body();
                    getActivity().runOnUiThread(EditItemFragment.this::updateView);
                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {

                }
            });
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Edit Plan");
            updateView();
        }
        else
        {
            mCurrentItem = new Item();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Create Plan");
        }

        super.onViewCreated(view, savedInstanceState);
    }

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentItem.getStart_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentItem.getStart_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };


    private void updateView() {
        //TODO
        mTimeBox.setText(timeSdf.format(mCurrentItem.getStart_time().getTime()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACES_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                mPlaceBox.setText(place.getAddress());
                autoCompleteResult = place;
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
