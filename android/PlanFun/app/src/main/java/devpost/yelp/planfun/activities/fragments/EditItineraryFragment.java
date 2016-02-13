package devpost.yelp.planfun.activities.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Itinerary;

public class EditItineraryFragment extends Fragment {

    private Itinerary mCurrentItinerary;

    private EditText mNameBox, mCityBox, mDateBox, mStartTimeBox, mEndTimeBox;
    private CheckBox mCheckBox;

    private String dateFormat = "MM/dd/yyyy";
    private SimpleDateFormat dateSdf;
    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;

    private final String[] cities = {"Atlanta", "Austin", "Miami", "Portland", "Philadelphia", "Seattle"};


    public static EditItineraryFragment newInstance()
    {
        return new EditItineraryFragment();
    }

    public EditItineraryFragment()
    {}

    public void setItinerary(Itinerary itinerary)
    {
        mCurrentItinerary = itinerary;
    }

    public void setItineraryAndUpdate(Itinerary itinerary)
    {
        mCurrentItinerary = itinerary;
        updateView();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edit_itinerary, container, false);

        mNameBox = (EditText) v.findViewById(R.id.nameBox);
        mCityBox = (EditText) v.findViewById(R.id.citySpinner);
        mDateBox = (EditText) v.findViewById(R.id.datePicker);
        mStartTimeBox = (EditText) v.findViewById(R.id.startPicker);
        mEndTimeBox = (EditText) v.findViewById(R.id.endPicker);
        mCheckBox = (CheckBox)v.findViewById(R.id.publicBox);

        dateSdf = new SimpleDateFormat(dateFormat, Locale.US);
        timeSdf = new SimpleDateFormat(timeFormat, Locale.US);

        mCityBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(EditItineraryFragment.this.getActivity())
                        .title("Cities")
                        .content("At this time, you cannot change the city of your itinerary. Please create a new itinerary in the desired city")
                        .positiveText("Ok")
                        .show();
            }
        });


        mStartTimeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EditItineraryFragment.this.getActivity(), startTimeSetListener, mCurrentItinerary.getStart_time().get(Calendar.HOUR_OF_DAY), mCurrentItinerary.getStart_time().get(Calendar.MINUTE), true).show();
            }
        });

        mEndTimeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EditItineraryFragment.this.getActivity(), endTimeSetListener, mCurrentItinerary.getEnd_time().get(Calendar.HOUR_OF_DAY), mCurrentItinerary.getEnd_time().get(Calendar.MINUTE), true).show();
            }
        });

        mDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditItineraryFragment.this.getActivity(), dateSetListener, mCurrentItinerary.getDate().get(Calendar.YEAR), mCurrentItinerary.getDate().get(Calendar.MONTH), mCurrentItinerary.getDate().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCurrentItinerary.setPublic(isChecked);
            }
        });

        updateView();

        return v;
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCurrentItinerary.getDate().set(Calendar.YEAR, year);
            mCurrentItinerary.getDate().set(Calendar.MONTH, monthOfYear);
            mCurrentItinerary.getDate().set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mCurrentItinerary.getStart_time().set(Calendar.YEAR, year);
            mCurrentItinerary.getStart_time().set(Calendar.MONTH, monthOfYear);
            mCurrentItinerary.getStart_time().set(Calendar.DAY_OF_MONTH, dayOfMonth);

            mCurrentItinerary.getEnd_time().set(Calendar.YEAR, year);
            mCurrentItinerary.getEnd_time().set(Calendar.MONTH, monthOfYear);
            mCurrentItinerary.getEnd_time().set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentItinerary.getStart_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentItinerary.getStart_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCurrentItinerary.getEnd_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCurrentItinerary.getEnd_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };



    private void updateView() {
        mNameBox.setText(mCurrentItinerary.getName());
        mCityBox.setText(mCurrentItinerary.getCity());
        mDateBox.setText(dateSdf.format(mCurrentItinerary.getDate().getTime()));
        mStartTimeBox.setText(timeSdf.format(mCurrentItinerary.getStart_time().getTime()));
        mEndTimeBox.setText(timeSdf.format(mCurrentItinerary.getEnd_time().getTime()));
    }
}
