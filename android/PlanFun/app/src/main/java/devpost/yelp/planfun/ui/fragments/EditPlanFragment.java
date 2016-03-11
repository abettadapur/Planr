package devpost.yelp.planfun.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;

import com.google.android.gms.location.places.Place;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.ui.adapters.ItemAdapter;
import devpost.yelp.planfun.ui.dialogs.EditItemDialog;
import devpost.yelp.planfun.ui.events.EditItemRequest;
import devpost.yelp.planfun.ui.events.FindItemRequest;
import devpost.yelp.planfun.ui.events.SavePlanRequest;
import devpost.yelp.planfun.model.Plan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPlanFragment extends BaseFragment {
    private Plan mCurrentPlan;
    private Place autoCompleteResult;

    @Bind(R.id.plan_input_name)
    MaterialEditText mNameBox;

    @Bind(R.id.plan_date_picker)
    MaterialEditText mDateBox;

    @OnClick(R.id.plan_date_picker)
    public void dateClickListener(View view){
        new DatePickerDialog(EditPlanFragment.this.getActivity(), dateSetListener,
                mCurrentPlan.getStart_time().get(Calendar.YEAR),
                mCurrentPlan.getStart_time().get(Calendar.MONTH),
                mCurrentPlan.getStart_time().get(Calendar.DAY_OF_MONTH)).show();
    }

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

    private RestClient mRestClient;

    public static EditPlanFragment newInstance()
    {
        EditPlanFragment fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditPlanFragment newInstance(int plan_id) {
        EditPlanFragment fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        args.putInt("plan_id", plan_id);
        fragment.setArguments(args);
        return fragment;
    }

    public EditPlanFragment()
    {
        mRestClient = RestClient.getInstance();
    }

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
        PlanFunApplication.getBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PlanFunApplication.getBus().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_plan, container, false);
        ButterKnife.bind(this, v);

        mAdapter = new ItemAdapter(mCurrentPlan ==null?null: mCurrentPlan.getItems(), this.getActivity(), true);
        mItemsView.setAdapter(mAdapter);
        mItemsView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter.notifyDataSetChanged();
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int planId = args.getInt("plan_id", -1);
        if(planId!=-1)
        {
            Call<Plan> planCall = mRestClient.getItineraryService().getItinerary(planId);
            planCall.enqueue(new Callback<Plan>() {
                @Override
                public void onResponse(Call<Plan> call, Response<Plan> response) {
                    mCurrentPlan = response.body();
                    getActivity().runOnUiThread(EditPlanFragment.this::updateView);
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
            mCurrentPlan = new Plan();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Create Plan");
            mNameBox.requestFocusFromTouch();
        }

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
            mCurrentPlan.setName(mNameBox.getText().toString());

            updateView();
        }
    };

    private void updateView() {
        mNameBox.setText(mCurrentPlan.getName());
        if(mCurrentPlan.getStart_time()==null)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            mCurrentPlan.setStart_time(calendar);
        }
    }

    @Subscribe
    public void onSavePlanRequest(SavePlanRequest request)
    {
        PlanDetailFragment fragment = PlanDetailFragment.newInstance(request.to_save.getId());
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }


    @Subscribe
    public void onFindItemRequest(FindItemRequest request)
    {
        SearchItemFragment fragment = SearchItemFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commit();
    }


    @Subscribe
    public void onEditItemRequest(EditItemRequest request)
    {
        EditItemDialog dialog = new EditItemDialog();

        if(!request.new_item) {
            dialog = EditItemDialog.newInstance(request.item_id);
        }

        dialog.show(getActivity().getSupportFragmentManager(), "fm");
    }

}
