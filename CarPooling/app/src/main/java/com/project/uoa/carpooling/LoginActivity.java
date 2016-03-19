package com.project.uoa.carpooling;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // NOTE: MIGHT NOT NEED ANYMORE - breaks stuff
        // Removes title bar on the login screen
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN);

        facebookSDKInitialize();

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
                final Profile profile = Profile.getCurrentProfile();
                startActivity(new Intent(LoginActivity.this, CurrentPoolActivity.class));
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });

        // This is temporary until we figure out how to store users and complete a signup/login process
        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CurrentPoolActivity.class));
            }
        });
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        //TODO: Currently there is no MainActivity present
        moveTaskToBack(true);
    }
}
