package devpost.yelp.planfun.activities.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.jar.Attributes;

/**
 * Created by alexb on 2/29/2016.
 */
public class WebAutoCompleteTextView extends AutoCompleteTextView
{
    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;

    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar mLoadingIndicator;

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            WebAutoCompleteTextView.super.performFiltering((CharSequence)msg.obj, msg.arg1);
        }
    };

    public WebAutoCompleteTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setLoadingIndicator(ProgressBar progressBar)
    {
        mLoadingIndicator = progressBar;
    }

    public void setAutoCompleteDelay(int delay)
    {
        mAutoCompleteDelay = delay;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if(mLoadingIndicator!=null)
            mLoadingIndicator.setVisibility(View.VISIBLE);

        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if(mLoadingIndicator!=null)
            mLoadingIndicator.setVisibility(View.GONE);

        super.onFilterComplete(count);
    }
}