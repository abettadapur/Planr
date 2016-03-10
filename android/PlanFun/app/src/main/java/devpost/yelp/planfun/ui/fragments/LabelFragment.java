package devpost.yelp.planfun.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import devpost.yelp.planfun.R;

/**
 * Created by alexb on 3/10/2016.
 */
public class LabelFragment extends Fragment
{
    @Bind(R.id.labelView)
    TextView mLabelView;

    public static LabelFragment newInstance(String label)
    {
        Bundle args = new Bundle();
        args.putString("label", label);
        LabelFragment fragment = new LabelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_label_view, container, false);
        ButterKnife.bind(this, v);
        Bundle args = getArguments();
        mLabelView.setText(args.getString("label"));
        return v;
    }
}
