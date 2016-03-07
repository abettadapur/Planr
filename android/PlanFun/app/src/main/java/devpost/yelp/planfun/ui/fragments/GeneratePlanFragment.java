package devpost.yelp.planfun.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Plan;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexb on 3/6/2016.
 */
public class GeneratePlanFragment extends Fragment
{
    @Bind(R.id.input_name)
    MaterialEditText mNameView;
    @Bind(R.id.startAddressPicker)
    MaterialEditText mStartingAddressView;
    @Bind(R.id.startTimePicker)
    MaterialEditText mStartTimeView;
    @Bind(R.id.endTimePicker)
    MaterialEditText mEndTimeView;
    @Bind(R.id.datePicker)
    MaterialEditText mDateView;
    @Bind(R.id.save_plan)
    Button saveButton;

    private RestClient mRestClient;

    private String dateFormat = "MM/dd/yyyy";
    private SimpleDateFormat dateSdf;
    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;

    private final int PLACES_AUTOCOMPLETE=10001;

    private Plan mCurrentPlan;


    public GeneratePlanFragment()
    {
        mRestClient = RestClient.getInstance();
        mCurrentPlan = new Plan();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generate_plan, container, false);
        ButterKnife.bind(this, v);
        dateSdf = new SimpleDateFormat(dateFormat, Locale.US);
        timeSdf = new SimpleDateFormat(timeFormat, Locale.US);

        mStartingAddressView.setOnClickListener((view) -> openAutocomplete());
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Generate Itinerary");
        updateView();
        super.onViewCreated(view, savedInstanceState);
    }



    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCurrentPlan.getStart_time().set(Calendar.YEAR, year);
            mCurrentPlan.getStart_time().set(Calendar.MONTH, monthOfYear);
            mCurrentPlan.getStart_time().set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mCurrentPlan.getEnd_time().set(Calendar.YEAR, year);
            mCurrentPlan.getEnd_time().set(Calendar.MONTH, monthOfYear);
            mCurrentPlan.getEnd_time().set(Calendar.DAY_OF_MONTH, dayOfMonth);
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
        mStartTimeView.setText(timeSdf.format(mCurrentPlan.getStart_time().getTime()));
        mEndTimeView.setText(timeSdf.format(mCurrentPlan.getEnd_time().getTime()));
        mDateView.setText(dateSdf.format(mCurrentPlan.getStart_time().getTime()));
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
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Creating", "Generating a custom plan....", true);
        mCurrentPlan.setName(mNameView.getText().toString());
        Call<Plan> createCall = mRestClient.getItineraryService().createItinerary(mCurrentPlan);
        createCall.enqueue(new Callback<Plan>() {
            @Override
            public void onResponse(Call<Plan> call, Response<Plan> response) {
                progress.dismiss();
                if (response.isSuccess()) {
                    Log.i("CREATE ITINERARY", "SUCCESS " + response.body());
                    PlanFunApplication.getBus().post(new OpenPlanRequest(response.body().getId(), true));
                } else {
                    Log.i("CREATE ITINERARY", "FAIL: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Plan> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.startTimePicker)
    public void openStartTimePicker()
    {
        new TimePickerDialog(getActivity(), startTimeSetListener,
                mCurrentPlan.getStart_time().get(Calendar.HOUR_OF_DAY),
                mCurrentPlan.getStart_time().get(Calendar.MINUTE), true).show();
    }
    @OnClick(R.id.endTimePicker)
    public void openEndTimePicker()
    {
        new TimePickerDialog(getActivity(), endTimeSetListener,
                mCurrentPlan.getEnd_time().get(Calendar.HOUR_OF_DAY),
                mCurrentPlan.getEnd_time().get(Calendar.MINUTE), true).show();
    }
    @OnClick(R.id.datePicker)
    public void openDateTimePicker()
    {
        new DatePickerDialog(getActivity(), dateSetListener,
                mCurrentPlan.getStart_time().get(Calendar.YEAR),
                mCurrentPlan.getStart_time().get(Calendar.MONTH),
                mCurrentPlan.getStart_time().get(Calendar.DAY_OF_MONTH)).show();
    }
}
