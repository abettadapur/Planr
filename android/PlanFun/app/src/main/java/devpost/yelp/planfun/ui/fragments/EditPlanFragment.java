package devpost.yelp.planfun.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.ui.adapters.ItemAdapter;
import devpost.yelp.planfun.ui.events.SavePlanRequest;
import devpost.yelp.planfun.ui.views.WebAutoCompleteTextView;
import devpost.yelp.planfun.model.Plan;

public class EditPlanFragment extends Fragment {

    private Plan mCurrentPlan;

    @Bind(R.id.input_name)
    EditText mNameBox;

    @Bind(R.id.time_picker)
    EditText mStartTimeBox;

    @OnClick(R.id.time_picker)
    public void startClickListener(View view){
        new TimePickerDialog(EditPlanFragment.this.getActivity(), startTimeSetListener,
                mCurrentPlan.getStart_time().get(Calendar.HOUR_OF_DAY),
                mCurrentPlan.getStart_time().get(Calendar.MINUTE), true).show();
    }

    @Bind(R.id.city_picker)
    WebAutoCompleteTextView mCityPicker;
    CheckBox mCheckBox;

    @Bind(R.id.items_view)
    RecyclerView mItemsView;

    private ItemAdapter mAdapter;

    @OnCheckedChanged(R.id.privateBox)
    public void privateChecked(CompoundButton buttonView, boolean isChecked) {
        mCurrentPlan.setPublic(!isChecked);
    }

    @Bind(R.id.save_plan)
    Button mSaveButton;

    @OnClick(R.id.save_plan)
    public void saveClicked(View view){
        PlanFunApplication.getBus().post(new SavePlanRequest(mCurrentPlan));
    }

    private String dateFormat = "MM/dd/yyyy";
    private SimpleDateFormat dateSdf;
    private String timeFormat = "H:mm";
    private SimpleDateFormat timeSdf;

    public static EditPlanFragment newInstance()
    {
        return new EditPlanFragment();
    }

    public static EditPlanFragment newInstance(int plan_id) {
        EditPlanFragment fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        args.putInt("plan_id", plan_id);
        fragment.setArguments(args);;
        return fragment;
    }

    public EditPlanFragment()
    {}

    public void setPlan(Plan plan)
    {
        mCurrentPlan = plan;
    }

    public void setPlanAndUpdate(Plan plan)
    {
        mCurrentPlan = plan;
        updateView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_plan, container, false);
        ButterKnife.bind(this,v);
        dateSdf = new SimpleDateFormat(dateFormat, Locale.US);
        timeSdf = new SimpleDateFormat(timeFormat, Locale.US);

        updateView();

        mAdapter = new ItemAdapter(mCurrentPlan ==null?null: mCurrentPlan.getItems(), getActivity(), true);
        mItemsView.setAdapter(mAdapter);

        return v;
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
        mNameBox.setText(mCurrentPlan.getName());
        mCityPicker.setText(mCurrentPlan.getCity());
        mStartTimeBox.setText(timeSdf.format(mCurrentPlan.getStart_time().getTime()));
    }
}
