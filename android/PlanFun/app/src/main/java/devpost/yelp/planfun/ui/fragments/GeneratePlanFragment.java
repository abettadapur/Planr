package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.GenerateError;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.net.requests.GeneratePlanRequest;
import devpost.yelp.planfun.ui.adapters.CategoryAdapter;
import devpost.yelp.planfun.ui.dialogs.PickCategoryDialog;
import devpost.yelp.planfun.ui.events.AddCategoryRequest;
import devpost.yelp.planfun.ui.events.OpenPlanPreviewRequest;
import devpost.yelp.planfun.ui.listutils.OnStartDragListener;
import devpost.yelp.planfun.ui.listutils.SimpleItemTouchHelperCallback;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by alexb on 3/6/2016.
 */
public class GeneratePlanFragment extends BaseFragment implements OnStartDragListener
{
    @Bind(R.id.input_name)
    MaterialEditText mNameView;
    @Bind(R.id.startAddressPicker)
    MaterialEditText mStartingAddressView;
    @Bind(R.id.startTimePicker)
    MaterialEditText mStartTimeView;
    @Bind(R.id.datePicker)
    MaterialEditText mDateView;
    @Bind(R.id.save_plan)
    Button saveButton;
    @Bind(R.id.add_category)
    ImageButton mAddCategoryButton;
    @Bind(R.id.categoryListView)
    RecyclerView mCategoryListView;

    private RestClient mRestClient;
    private final int PLACES_AUTOCOMPLETE=10001;

    private Plan mCurrentPlan;
    private List<YelpCategory> mCategories;
    private List<YelpCategory> mAllCategories;
    private CategoryAdapter mCategoryAdapter;
    private ItemTouchHelper mItemTouchHelper;


    public GeneratePlanFragment()
    {
        mRestClient = RestClient.getInstance();
        mCurrentPlan = new Plan();
        mCategories = new ArrayList<>();

        Call<List<YelpCategory>> categoryCall = mRestClient.getCategoryService().getCategories();
        categoryCall.enqueue(new Callback<List<YelpCategory>>() {
            @Override
            public void onResponse(Call<List<YelpCategory>> call, Response<List<YelpCategory>> response) {
                if(response.isSuccess())
                {
                    mAllCategories = response.body();
                    for(YelpCategory category : mAllCategories)
                    {
                        if(category.getName().equals("Lunch") || category.getName().equals("Parks"))
                        {
                            mCategories.add(category);
                        }
                    }
                    if(mCategoryAdapter!=null)
                    {
                        getActivity().runOnUiThread(mCategoryAdapter::notifyDataSetChanged);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<YelpCategory>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlanFunApplication.getBus().register(this);
    }

    @Override
    public void onDestroy() {
        PlanFunApplication.getBus().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generate_plan, container, false);
        ButterKnife.bind(this, v);
        mAddCategoryButton.setImageDrawable(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.PLUS).setColor(R.color.material_drawer_primary_icon).build());

        mStartingAddressView.setOnClickListener((view) -> openAutocomplete());

        mCategoryAdapter = new CategoryAdapter(mCategories, getActivity(), this);
        mCategoryListView.setAdapter(mCategoryAdapter);
        mCategoryListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCategoryListView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCategoryAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mCategoryListView);
        return v;
    }

    private void openAutocomplete()
    {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            startActivityForResult(intent, PLACES_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException grex) {

        } catch (GooglePlayServicesNotAvailableException gnaex) {

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Generate Plan");
        updateView();
        super.onViewCreated(view, savedInstanceState);
    }



    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCurrentPlan.getStart_time().set(Calendar.YEAR, year);
            mCurrentPlan.getStart_time().set(Calendar.MONTH, monthOfYear);
            mCurrentPlan.getStart_time().set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentPlan.getStart_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentPlan.getStart_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };


    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentPlan.getEnd_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentPlan.getEnd_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    private void updateView() {
        mStartingAddressView.setText(mCurrentPlan.getStarting_address());
        if(mCurrentPlan.getStart_time()==null)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            mCurrentPlan.setStart_time(calendar);
        }
        if(mCurrentPlan.getEnd_time()==null)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 22);
            calendar.set(Calendar.MINUTE, 0);
            mCurrentPlan.setEnd_time(calendar);
        }
        mStartTimeView.setText(PlanFunApplication.TIME_FORMAT.format(mCurrentPlan.getStart_time().getTime()));
        mDateView.setText(PlanFunApplication.DATE_FORMAT.format(mCurrentPlan.getStart_time().getTime()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACES_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                mCurrentPlan.setStarting_address(place.getAddress().toString());
                mCurrentPlan.setStarting_coordinate(place.getLatLng());
                updateView();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.save_plan)
    public void generatePlan()
    {
        if(validate()) {
            final ProgressDialog progress = ProgressDialog.show(getActivity(), "Creating", "Generating a custom plan....", true);
            mCurrentPlan.setName(mNameView.getText().toString());
            Call<Plan> createCall = mRestClient.getPlanService().generatePlan(new GeneratePlanRequest(mCategories, mCurrentPlan));
            createCall.enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    progress.dismiss();
                    if (response.isSuccess()) {
                        Log.i("CREATE ITINERARY", "SUCCESS " + response.body());
                        PlanFunApplication.getBus().post(new OpenPlanPreviewRequest(response.body()));
                    } else {
                        Converter<ResponseBody, GenerateError> errorConverter =
                                mRestClient.getRetrofitInstance().responseBodyConverter(GenerateError.class, new Annotation[0]);
                        try {
                            List<YelpCategory> failedCategories = errorConverter.convert(response.errorBody()).categories;
                            String errorMessage = "";
                            for(YelpCategory c: failedCategories)
                            {
                                errorMessage+=c.getName() + ", ";
                            }
                            final String finalErrorMessage = errorMessage.substring(0, errorMessage.length()-2);
                            getActivity().runOnUiThread(()->
                            new MaterialDialog.Builder(getContext())
                                    .title("Error")
                                    .content("Sorry! We could not find activities for the following categories: "+finalErrorMessage+". Please try another selection")
                                    .positiveText("Ok")
                                    .build()
                                    .show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //DO ERROR HANDLING HERE
                    }
                }

                @Override
                public void onFailure(Call<Plan> call, Throwable t) {

                }
            });
        }
    }

    private boolean validate()
    {
        boolean result = true;
        if(mNameView.getText().toString().isEmpty())
        {
            mNameView.setError("Name cannot be empty");
            result = false;
        }
        if(mStartingAddressView.getText().toString().isEmpty())
        {
            mStartingAddressView.setError("Starting address cannot be empty");
            result = false;
        }
        if(mStartTimeView.getText().toString().isEmpty())
        {
            mStartTimeView.setError("Start time cannot be empty");
            result = false;
        }
        if(mDateView.getText().toString().isEmpty())
        {
            mDateView.setError("Date cannot be empty");
            result = false;
        }
        return result;
    }

    @OnClick(R.id.add_category)
    public void onAddCategoryClick(View v)
    {
        PickCategoryDialog categoryDialog = PickCategoryDialog.newInstance(mAllCategories);
        categoryDialog.show(this.getChildFragmentManager(), "fm");
    }

    @OnClick(R.id.startTimePicker)
    public void openStartTimePicker()
    {
        new TimePickerDialog(getActivity(), startTimeSetListener,
                mCurrentPlan.getStart_time().get(Calendar.HOUR_OF_DAY),
                mCurrentPlan.getStart_time().get(Calendar.MINUTE), true).show();
    }
    @OnClick(R.id.datePicker)
    public void openDateTimePicker()
    {
        new DatePickerDialog(getActivity(), dateSetListener,
                mCurrentPlan.getStart_time().get(Calendar.YEAR),
                mCurrentPlan.getStart_time().get(Calendar.MONTH),
                mCurrentPlan.getStart_time().get(Calendar.DAY_OF_MONTH)).show();
    }

    @Subscribe
    public void OnAddCategory(AddCategoryRequest request)
    {
        Log.i("GENERATE", "Category received, id: "+request.category.getId());
        mCategories.add(request.category);
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }
}
