package devpost.yelp.planfun.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import devpost.yelp.planfun.R;
import devpost.yelp.planfun.net.RestClient;
import devpost.yelp.planfun.net.interfaces.AuthService;
import devpost.yelp.planfun.net.requests.AuthRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
Main activity, that starts the app. Handles login, and if succesfull starts ItineraryActivity.
 */
public class SplashActivity extends FragmentActivity {


    private LoginButton authButton;
    private ProgressBar progressBar;
    private CallbackManager callbackManager;
    private boolean started;
    private boolean requesting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        authButton = (LoginButton)findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        callbackManager = CallbackManager.Factory.create();

        authButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                checkServerLogin();
            }

            @Override
            public void onCancel() {
                //some error
            }

            @Override
            public void onError(FacebookException error) {
                //some error
            }
        });

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if(extras==null || extras.getBoolean("delay", true)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkServerLogin();
                }
            }, 3000);
        }
        else
            checkServerLogin();
    }

    private void checkServerLogin()
    {
        AuthService auth = RestClient.getInstance().getAuthService();
        AccessToken token = AccessToken.getCurrentAccessToken();
        if(token != null)
        {
            Call<Boolean> authCall = auth.verifyAuthentication(new AuthRequest(token.getToken(), token.getUserId()));
            authCall.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccess()) {
                        Log.i("LOGIN", "Login was successful, launch new activity");
                        Log.i("Token", token.getToken());
                        Log.i("User", token.getUserId());

                        if (!started) {
                            Intent i = new Intent(SplashActivity.this, ItineraryActivity.class);
                            i.addCategory(Intent.CATEGORY_HOME);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            SplashActivity.this.startActivity(i);
                            started = true;
                            finish();
                        }
                    } else {
                        Log.e("LOGIN", response.errorBody().toString());
                        Log.e("LOGIN", "Login failed, verification was ");
                        Log.e("Token", token.getToken());
                        Log.e("User", token.getUserId());
                        authButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("LOGIN", "Could not connect to the login server");
                    authButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else
        {
            authButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
