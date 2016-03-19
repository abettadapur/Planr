package devpost.yelp.planfun.ui.fragments;

import android.app.DatePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.places.Place;
import com.mikepenz.materialize.color.Material;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

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
import devpost.yelp.planfun.ui.events.OpenPlanRequest;
import devpost.yelp.planfun.ui.events.SaveItemRequest;
import devpost.yelp.planfun.ui.events.SavePlanRequest;
import devpost.yelp.planfun.model.Plan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPlanFragment extends BackPressFragment {
    private Plan mCurrentPlan;
    private static final String TAG = "EDIT_PLAN_FRAGMENT";

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

    @Bind(R.id.plan_input_desc)
    MaterialEditText mDescBox;

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
        if(validate()) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .title("Saving activity...")
                    .content("Just a sec")
                    .progress(true, 0)
                    .show();
            if (mCurrentPlan.getId() == -1) {
                //new plan, call create new
                mCurrentPlan.setName(mNameBox.getText().toString());
                mCurrentPlan.setDescription(mNameBox.getText().toString());

                Call<Plan> plan = RestClient.getInstance().getPlanService().createPlan(mCurrentPlan);
                plan.enqueue(new Callback<Plan>() {
                    @Override
                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                        Log.i("EDIT_PLAN", "Successfully saved new plan " + mCurrentPlan.getId());
                        dialog.dismiss();
                        PlanFunApplication.getBus().post(new OpenPlanRequest(response.body().getId(), true));
                    }

                    @Override
                    public void onFailure(Call<Plan> call, Throwable t) {
                        Log.e("EDIT_PLAN", "Could not create new plan " + mCurrentPlan.getId());

                    }
                });
            } else {
                //existing plan, call edit
                Call<Plan> plan = RestClient.getInstance().getPlanService().updatePlan(mCurrentPlan.getId(), mCurrentPlan);
                plan.enqueue(new Callback<Plan>() {
                    @Override
                    public void onResponse(Call<Plan> call, Response<Plan> response) {
                        Log.i("EDIT_PLAN", "Successfully updated plan " + mCurrentPlan.getId());
                        dialog.dismiss();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Call<Plan> call, Throwable t) {
                        Log.e("EDIT_PLAN", "Could not update plan " + mCurrentPlan.getId());

                    }
                });

            }
        }
    }

    private RestClient mRestClient;

    public static EditPlanFragment newInstance()
    {
        EditPlanFragment fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditPlanFragment newInstance(Plan plan) {
        EditPlanFragment fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        args.putParcelable("plan", plan);
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
        Log.i(TAG,"onCreate");
        PlanFunApplication.getBus().register(this);
        Bundle args = getArguments();
        int planId = args.getInt("plan_id", -1);
        if(args.containsKey("plan"))
            mCurrentPlan = args.getParcelable("plan");
        else if(planId!=-1)
        {
            Call<Plan> planCall = mRestClient.getPlanService().getPlan(planId);
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

        }
        else
        {
            mCurrentPlan = new Plan();
        }
        mAdapter = new ItemAdapter(mCurrentPlan ==null?null: mCurrentPlan.getItems(), this.getActivity(), true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        PlanFunApplication.getBus().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreteView");
        View v = inflater.inflate(R.layout.fragment_edit_plan, container, false);
        ButterKnife.bind(this, v);

        mItemsView.setAdapter(mAdapter);
        mItemsView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter.notifyDataSetChanged();

        int planId = mCurrentPlan.getId();
        if(planId!=-1)
        {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Edit Plan");

        }
        else
        {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Create Plan");
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView();
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
            mCurrentPlan.setDescription(mDescBox.getText().toString());

            updateView();
        }
    };

    private void updateView() {
        mNameBox.setText(mCurrentPlan.getName());
        if(mCurrentPlan.getStart_time()!=null)
        {
            mDateBox.setText(PlanFunApplication.DATE_FORMAT.format(mCurrentPlan.getStart_time().getTime()));
        }
        mDescBox.setText(mCurrentPlan.getDescription());
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
            dialog = EditItemDialog.newInstance(request.item);
        }

        dialog.show(getActivity().getSupportFragmentManager(), "fm");
    }


    @Subscribe
    public void onSaveItemRequest(SaveItemRequest request)
    {
        boolean newItem = mCurrentPlan.addItem(request.to_save);
        mAdapter.setItems(mCurrentPlan.getItems());
        mAdapter.notifyDataSetChanged();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, this)
                .addToBackStack("")
                .commit();
        mItemsView.scrollToPosition(mCurrentPlan.getItems().size()-1);

    }

    private boolean validate()
    {
        boolean result = true;
        if(mNameBox.getText().toString().isEmpty())
        {
            mNameBox.setError("Name cannot be empty");
            result = false;
        }
        if(mCurrentPlan.getItems().size()==0)
        {
            new MaterialDialog.Builder(getContext())
                    .title("Error")
                    .content("Your plan must have at least one item")
                    .positiveText("Ok")
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();
                    }).show();
            result = false;
        }
        return result;
    }
}
