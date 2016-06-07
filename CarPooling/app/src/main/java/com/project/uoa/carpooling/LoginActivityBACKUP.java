package com.project.uoa.carpooling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivityBACKUP extends AppCompatActivity {

    public static final String PREFS_LOGGED_IN = "MyLoginTracker";
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private static int SPLASH_TIME_OUT = 3000;


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(LoginActivityBACKUP.this, MainActivity.class);
                    startActivity(i);

                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        facebookSDKInitialize();


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        // Only display page if the user hasn't logged in yet
        SharedPreferences settings = getSharedPreferences(LoginActivityBACKUP.PREFS_LOGGED_IN, 0);
        // Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(isLoggedIn())
        {
            startActivity(new Intent(LoginActivityBACKUP.this, MainActivity.class));
            finish();
        }










        // NOTE: MIGHT NOT NEED ANYMORE - breaks stuff
        // Removes title bar on the login screen
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // ----------------------------------------------------------- KEY HASH FOR FACEBOOK - REMOVE LATER
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.project.uoa.carpooling",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        // ------------------------------------------------------------------------------------------



        setContentView(R.layout.activity_login);





        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.setReadPermissions("public_profile");
        fbLoginButton.setReadPermissions("rsvp_event");
        fbLoginButton.setReadPermissions("user_events");

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {

                // Record that the user has logged in
                SharedPreferences settings = getSharedPreferences(LoginActivityBACKUP.PREFS_LOGGED_IN, 0); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("hasLoggedIn", true);
                editor.commit();


                Log.d("Test First Name:", "BEFORE");
                final Profile profile = Profile.getCurrentProfile();

                Log.d("Test First Name:", profile.getFirstName());
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                finish();
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
                startActivity(new Intent(LoginActivityBACKUP.this, MainActivity.class));
            }
        });
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
