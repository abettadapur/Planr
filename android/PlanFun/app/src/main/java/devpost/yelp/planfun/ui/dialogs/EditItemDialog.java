package devpost.yelp.planfun.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.Item;
import devpost.yelp.planfun.model.ItemType;
import devpost.yelp.planfun.model.Location;
import devpost.yelp.planfun.model.YelpCategory;
import devpost.yelp.planfun.ui.adapters.YelpEntryAdapter;
import devpost.yelp.planfun.ui.events.AddCategoryRequest;
import devpost.yelp.planfun.ui.events.SaveItemRequest;

/**
 * Created by ros on 3/10/16.
 */
public class EditItemDialog extends DialogFragment {
    private final int PLACES_AUTOCOMPLETE=10000;
    private Place autoCompleteResult;
    private Item mItem;

    @Bind(R.id.item_add_name)
    public MaterialEditText mNameBox;

    @Bind(R.id.item_add_start)
    public MaterialEditText mStartBox;

    @OnClick(R.id.item_add_start)
    public void startClickListener(View view){
        if(mItem.getStart_time()!=null)
            new TimePickerDialog(getActivity(), startTimeSetListener,
                    mItem.getStart_time().get(Calendar.HOUR_OF_DAY),
                    mItem.getStart_time().get(Calendar.MINUTE),
                    true).show();
        else
            new TimePickerDialog(getActivity(), startTimeSetListener,12,0,
                    true).show();
    }

    @Bind(R.id.item_add_end)
    public MaterialEditText mEndBox;

    @OnClick(R.id.item_add_end)
    public void endClickListener(View view){
        if(mItem.getEnd_time()!=null)
            new TimePickerDialog(getActivity(), endTimeSetListener,
                mItem.getEnd_time().get(Calendar.HOUR_OF_DAY),
                mItem.getEnd_time().get(Calendar.MINUTE),
                true).show();
        else if(mItem.getStart_time()!=null)
            new TimePickerDialog(getActivity(), endTimeSetListener,
                    mItem.getStart_time().get(Calendar.HOUR_OF_DAY),
                    mItem.getStart_time().get(Calendar.MINUTE),
                    true).show();
        else
            new TimePickerDialog(getActivity(), endTimeSetListener,12,0,
                    true).show();
    }

    @Bind(R.id.item_add_at)
    public MaterialEditText mAtBox;

    @OnClick(R.id.item_add_at)
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


    @Bind(R.id.item_add_description)
    public MaterialEditText mDescBox;

    @Bind(R.id.item_add_category)
    public MaterialEditText mCategoryText;

    @OnClick(R.id.item_add_category)
    public void onCategoryClick(View view){
        mItem.setName(mNameBox.getText().toString());
        mItem.setDescription(mDescBox.getText().toString());
        PickCategoryDialog categoryDialog = PickCategoryDialog.newInstance(YelpCategory.SERVER_CATEGORIES);
        categoryDialog.show(this.getChildFragmentManager(), "fm");
    }

    @Bind(R.id.add_item_yelp_view)
    public CardView mYelpItemView;

    public static EditItemDialog newInstance(int item_id) {
        EditItemDialog f = new EditItemDialog();

        //TODO load in item and just called newInstance with Item?
        Bundle args = new Bundle();
        args.putInt("item_id", item_id);
        f.setArguments(args);

        return f;
    }


    public static EditItemDialog newInstance(Item clicked) {
        EditItemDialog f = new EditItemDialog();

        Bundle args = new Bundle();
        args.putParcelable("item", clicked);
        f.setArguments(args);
        f.setItem(clicked);

        return f;
    }

    public EditItemDialog()
    {

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

    public void setItem(Item item){
        this.mItem = item;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MaterialDialog.Builder b = new MaterialDialog.Builder(getActivity())
                .title("Edit Activity")
                .positiveText("Done")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mItem.setDescription(mDescBox.getText().toString());
                        mItem.setName(mNameBox.getText().toString());
                        PlanFunApplication.getBus().post(new SaveItemRequest(mItem));
                    }
                })
                .negativeText("Cancel");

        LayoutInflater i = getActivity().getLayoutInflater();
        View v = i.inflate(R.layout.dialog_edit_item, null);
        ButterKnife.bind(this, v);

        if(savedInstanceState!=null && savedInstanceState.containsKey("item"))
            mItem = savedInstanceState.getParcelable("item");

        if(mItem==null){
            b = b.title("Create Activity");
            mItem = new Item();
            mItem.setType(ItemType.USER);
            mNameBox.requestFocusFromTouch();
        } else{
            updateView();
        }
        updateView();
        b.customView(v, false);
        return b.build();
    }

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            mItem.setStart_time(time);
            mItem.setDescription(mDescBox.getText().toString());
            mItem.setName(mNameBox.getText().toString());
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            mItem.setEnd_time(time);
            mItem.setDescription(mDescBox.getText().toString());
            mItem.setName(mNameBox.getText().toString());
            updateView();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACES_AUTOCOMPLETE) {
            if (resultCode == Activity.RESULT_OK) {
                autoCompleteResult = PlaceAutocomplete.getPlace(getContext(), data);
                mAtBox.setText(autoCompleteResult.getAddress());
                mItem.setLocation(new Location(autoCompleteResult));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error?

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateView()
    {
        mNameBox.setText(mItem.getName());
        mDescBox.setText(mItem.getDescription());
        MaterialDrawableBuilder.IconValue categoryIcon = MaterialDrawableBuilder.IconValue.FOLDER;
        if(mItem.getYelp_category()!=null){
            mCategoryText.setText(mItem.getYelp_category().getName());
            try {
                categoryIcon = Util.iconFromString(mItem.getYelp_category().getIcon_string());
            }catch(IllegalArgumentException iaex)
            {
                Log.e("ICON", "No icon found for " + mItem.getYelp_category().getIcon_string());
            }
        }
        mCategoryText.setIconRight(MaterialDrawableBuilder.with(getContext())
                .setIcon(categoryIcon)
                .setColor(Color.BLACK)
                .setToActionbarSize()
                .build());

        if(mItem.getLocation()!=null)
            mAtBox.setText(mItem.getLocation().getAddress());

        if(mItem.getStart_time()!=null)
            mStartBox.setText(PlanFunApplication.TIME_FORMAT.format(mItem.getStart_time().getTime()));

        if(mItem.getEnd_time()!=null)
            mEndBox.setText(PlanFunApplication.TIME_FORMAT.format(mItem.getEnd_time().getTime()));

        if(mItem.getYelp_item()==null){
            mYelpItemView.setVisibility(View.GONE);
        }else{
            mCategoryText.setVisibility(View.GONE);
            mAtBox.setVisibility(View.GONE);
            YelpEntryAdapter.YelpEntryViewHolder holder = new YelpEntryAdapter.YelpEntryViewHolder(getContext(), mYelpItemView);
            holder.fillIn(mItem.getYelp_item());
        }
    }


    @Subscribe
    public void OnAddCategory(AddCategoryRequest request)
    {
        Log.i("EDIT_ITEM", "Category received, id: " + request.category.getId());
        mItem.setYelp_category(request.category);
        updateView();
    }

}
