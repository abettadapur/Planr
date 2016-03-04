package devpost.yelp.planfun.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.model.Itinerary;

public class EditItineraryFragment extends Fragment {

    private Itinerary mCurrentItinerary;

    private EditText mNameBox, mStartTimeBox, mEndTimeBox;
    private AutoCompleteTextView mCityPicker;
    private CheckBox mCheckBox;

    private String dateFormat = "MM/dd/yyyy";
    private SimpleDateFormat dateSdf;
    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;



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
        mCityPicker = (AutoCompleteTextView) v.findViewById(R.id.cityPicker);
        mStartTimeBox = (EditText) v.findViewById(R.id.startPicker);
        mEndTimeBox = (EditText) v.findViewById(R.id.endPicker);
        mCheckBox = (CheckBox)v.findViewById(R.id.publicBox);

        dateSdf = new SimpleDateFormat(dateFormat, Locale.US);
        timeSdf = new SimpleDateFormat(timeFormat, Locale.US);


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
        mCityPicker.setText(mCurrentItinerary.getCity());
        mStartTimeBox.setText(timeSdf.format(mCurrentItinerary.getStart_time().getTime()));
        mEndTimeBox.setText(timeSdf.format(mCurrentItinerary.getEnd_time().getTime()));
    }
}
