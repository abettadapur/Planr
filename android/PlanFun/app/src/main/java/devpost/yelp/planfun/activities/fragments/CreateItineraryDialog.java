package devpost.yelp.planfun.activities.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.activities.adapters.CityAutoCompleteAdapter;
import devpost.yelp.planfun.activities.views.WebAutoCompleteTextView;
import devpost.yelp.planfun.model.City;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.Itinerary;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.net.interfaces.ItineraryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 3/12/2015.
 */
public class CreateItineraryDialog extends DialogFragment {
    private Button newButton;
    private Button searchButton;
    private EditText mStartPicker;
    private EditText mEndPicker;
    private WebAutoCompleteTextView mCityPicker;
    private Calendar mStart, mEnd;
    public final static int CREATE=1;
    public final static int SEARCH=2;
    private static Itinerary newItinerary;

    public static Itinerary getNewItinerary(){
        return newItinerary;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder b = new MaterialDialog.Builder(getActivity())
                .title("Add Itinerary");

        LayoutInflater i = getActivity().getLayoutInflater();
        View v = i.inflate(R.layout.dialog_create_itinerary, null);

        mStart = Calendar.getInstance();
        mStart.set(Calendar.HOUR_OF_DAY, 10);
        mStart.set(Calendar.MINUTE, 0);

        mEnd = Calendar.getInstance();
        mEnd.set(Calendar.HOUR_OF_DAY, 21);
        mEnd.set(Calendar.MINUTE, 0);

        newButton = (Button)v.findViewById(R.id.newItinerary);
        newButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createItinerary(true);
            }
        });
        searchButton = (Button)v.findViewById(R.id.searchItinerary);
        searchButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                createItinerary(false);
            }
        });

        mCityPicker = (WebAutoCompleteTextView)v.findViewById(R.id.cityPicker);
        mCityPicker.setThreshold(2);
        mCityPicker.setAdapter(new CityAutoCompleteAdapter(this.getContext()));
        mCityPicker.setLoadingIndicator((ProgressBar) v.findViewById(R.id.autoCompleteProgressBar));
        mCityPicker.setOnItemClickListener((parent, view, position, id) -> {
            City city = (City) parent.getItemAtPosition(position);
            mCityPicker.setText(city.getName() + ", " + city.getState());
        });

        mStartPicker = (EditText)v.findViewById(R.id.timePicker);
        mStartPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateItineraryDialog.this.getActivity(), startTimeSetListener, mStart.get(Calendar.HOUR_OF_DAY), mStart.get(Calendar.MINUTE), true).show();
            }
        });

        mEndPicker = (EditText)v.findViewById(R.id.datePicker);
        mEndPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateItineraryDialog.this.getActivity(), endTimeSetListener, mEnd.get(Calendar.HOUR_OF_DAY), mEnd.get(Calendar.MINUTE), true).show();
            }
        });

        updateView();
        b = b.customView(v,false);
        return b.build();
    }

    private void createItinerary(boolean fromScratch) {
        String city = mCityPicker.getText().toString();
        ItineraryService service = RestClient.getInstance().getItineraryService();

        if(fromScratch) {
            newItinerary = new Itinerary("New Itinerary", mStart, mEnd, city, false, new ArrayList<Item>());
            final ProgressDialog progress = ProgressDialog.show(CreateItineraryDialog.this.getActivity(), "Creating", "Creating a custom itinerary....", true);
            Call<Itinerary> createCall = service.createItinerary(newItinerary);
            createCall.enqueue(new Callback<Itinerary>() {
                @Override
                public void onResponse(Call<Itinerary> call, Response<Itinerary> response) {
                    progress.dismiss();
                    if (response.isSuccess()) {
                        Log.e("CREATE ITINERARY", "SUCCESS " + response.body());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), CREATE, new Intent());
                    } else {
                        Log.e("CREATE ITINERARY", "FAIL: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Itinerary> call, Throwable t) {

                }
            });
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mStart.set(Calendar.YEAR, year);
            mStart.set(Calendar.MONTH, monthOfYear);
            mStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mEnd.set(Calendar.YEAR, year);
            mEnd.set(Calendar.MONTH, monthOfYear);
            mEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mStart.set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mEnd.set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    private void updateView()
    {
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat dateSdf = new SimpleDateFormat(dateFormat, Locale.US);
        String timeFormat = "H:mm";
        SimpleDateFormat timeSdf = new SimpleDateFormat(timeFormat, Locale.US);

        mStartPicker.setText(timeSdf.format(mStart.getTime()));
        mEndPicker.setText(timeSdf.format(mEnd.getTime()));
    }

}
