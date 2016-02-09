package thingswithworth.org.funtimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import retrofit.RetrofitError;
import thingswithworth.org.funtimes.R;

/**
 * Created by Andrey on 2/7/16.
 */
public class SplashActivity extends AppCompatActivity {

    private LoginButton authButton;
    private ProgressBar progressBar;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception)
        {
            onSessionStateChange(session, state, exception);
        }
    };

    private UiLifecycleHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        authButton = (LoginButton)findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        helper = new UiLifecycleHelper(this, callback);
        helper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        checkLogin();

        helper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        helper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Check if already logged in. If not, display auth button. If
     */
    private void checkLogin()
    {
        Session session = Session.getActiveSession();
        if(session!=null && (session.isOpened() || session.isClosed())){
            onSessionStateChange(session, session.getState(), null);
        }
        else
        {
            authButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception)
    {
        if(state.isOpened()){
            //authenticated
            Log.e("LOGIN", "Authenticated");
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response)
                {
                    if(user!=null)
                    {
                        final String user_ID = user.getId();
                        final String token = session.getAccessToken();

                        //on success, forward to next activity

                    }
                }

            });
            request.executeAsync();

        }
        else if(state.isClosed())
        {
            authButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
