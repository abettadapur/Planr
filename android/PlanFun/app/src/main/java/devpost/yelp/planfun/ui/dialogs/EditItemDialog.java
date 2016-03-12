package devpost.yelp.planfun.ui.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devpost.yelp.planfun.PlanFunApplication;
import devpost.yelp.planfun.R;
import devpost.yelp.planfun.etc.Util;
import devpost.yelp.planfun.model.Item;

/**
 * Created by ros on 3/10/16.
 */
public class EditItemDialog extends DialogFragment {
    private final int PLACES_AUTOCOMPLETE=10000;

    @Bind(R.id.item_add_name)
    public MaterialEditText mNameBox;

    @Bind(R.id.item_add_start)
    public MaterialEditText mStartBox;

    @OnClick(R.id.item_add_start)
    public void startClickListener(View view){
        new TimePickerDialog(getActivity(), startTimeSetListener,
                mItem.getStart_time().get(Calendar.HOUR_OF_DAY),
                mItem.getEnd_time().get(Calendar.MINUTE),
                true).show();
    }

    @Bind(R.id.item_add_end)
    public MaterialEditText mEndBox;

    @OnClick(R.id.item_add_end)
    public void endClickListener(View view){
        new TimePickerDialog(getActivity(), endTimeSetListener,
                mItem.getStart_time().get(Calendar.HOUR_OF_DAY),
                mItem.getEnd_time().get(Calendar.MINUTE),
                true).show();
    }

    @Bind(R.id.item_add_at)
    public MaterialEditText mAtBox;

    @Bind(R.id.item_add_description)
    public MaterialEditText mDescBox;

    @Bind(R.id.add_item_yelp_view)
    public CardView mYelpItemView;

    @Bind(R.id.item_add_category)
    public MaterialEditText mCategoryText;

    private Item mItem;

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

        return f;
    }

    public EditItemDialog()
    {

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
                        // TODO
                    }
                })
                .negativeText("Cancel");

        LayoutInflater i = getActivity().getLayoutInflater();
        View v = i.inflate(R.layout.dialog_edit_item, null);
        ButterKnife.bind(this, v);

        if(savedInstanceState.containsKey("item"))
            mItem = savedInstanceState.getParcelable("item");

        if(mItem==null){
            b = b.title("Create Activity");
            mItem = new Item();
            mNameBox.requestFocusFromTouch();
        }
        updateView();
        b.customView(v, false);
        return b.build();
    }

    TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mItem.getStart_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mItem.getStart_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mItem.getEnd_time().set(Calendar.HOUR_OF_DAY, hourOfDay);
            mItem.getEnd_time().set(Calendar.MINUTE, minute);
            updateView();
        }
    };

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


    private void updateView()
    {
        mNameBox.setText(mItem.getName());
        mDescBox.setText(mItem.getDescription());
        MaterialDrawableBuilder.IconValue categoryIcon = MaterialDrawableBuilder.IconValue.FOLDER;
        if(mItem.getYelp_category()!=null){
            mCategoryText.setText(mItem.getYelp_category().getName());
            categoryIcon = Util.iconFromString(mItem.getYelp_category().getIcon_string());
        }
        mCategoryText.setIconRight(MaterialDrawableBuilder.with(getContext())
                .setIcon(categoryIcon)
                .setColor(Color.BLACK)
                .setToActionbarSize()
                .build());

        if(mItem.getLocation()!=null)
            mAtBox.setText(mItem.getLocation().getAddress());

        if(mItem.getStart_time()!=null)
            mStartBox.setText(PlanFunApplication.TIME_FORMAT.format(mItem.getStart_time()));

        if(mItem.getEnd_time()!=null)
            mStartBox.setText(PlanFunApplication.TIME_FORMAT.format(mItem.getStart_time()));

        if(mItem.getYelp_item()==null){
            mYelpItemView.setVisibility(View.GONE);
        }else{
            mCategoryText.setVisibility(View.GONE);
        }

    }
}
