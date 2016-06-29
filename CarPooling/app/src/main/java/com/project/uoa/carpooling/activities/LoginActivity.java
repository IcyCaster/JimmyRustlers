package com.project.uoa.carpooling.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.project.uoa.carpooling.R;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_LOGGED_IN = "MyLoginTracker";
    private CallbackManager callbackManager;
    public static AccessTokenTracker accessTokenTracker;

    private Profile profile;



    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null) {
            return false;
        }
        else if(accessToken.isExpired()) {
            return false;
        }
        else {
            return true;
        }
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        facebookSDKInitialize();



        if (isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


        setContentView(R.layout.activity_login);
        updateWithToken(AccessToken.getCurrentAccessToken());

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.setReadPermissions("public_profile");
        fbLoginButton.setReadPermissions("rsvp_event");
        fbLoginButton.setReadPermissions("user_events");

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker profileTracker;

            @Override
            public void onSuccess(LoginResult login_result) {

                AccessToken accessToken = login_result.getAccessToken();


                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile1, Profile profile2) {
                            // profile2 is the new profile
                            profile = profile2;
                            Log.d("facebook - profile", profile2.getFirstName());
                            profileTracker.stopTracking();
                        }
                    };
                }
                else {
                    profile = Profile.getCurrentProfile();
                    Log.d("facebook - profile", profile.getFirstName());
                }

            }

            @Override
            public void onCancel() {
                // code for cancellation
                Log.d("fb_login_sdk", "callback cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
                Log.d("fb_login_sdk", "callback onError");
            }
        });

        // This is temporary until we figure out how to store users and complete a signup/login process
        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // Initialize the Facebook components
    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
                Profile.fetchProfileForCurrentAccessToken();
            }
        };

    }


}
