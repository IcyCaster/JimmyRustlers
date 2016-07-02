package com.project.uoa.carpooling.services;

import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.LoginActivity;

/**
 * This connector abstracts the Facebook callback away from the LoginActivity.
 * Created by Chester on 2/07/2016.
 */
public class FacebookConnector {

    private CallbackManager callbackManager;
    private LoginActivity loginActivity;
    private LoginButton fbLoginButton;

    public FacebookConnector(LoginActivity loginActivity){
        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> mFacebookCallBack = getFacebookCallBack();

        fbLoginButton = (LoginButton) loginActivity.findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.setReadPermissions("public_profile");
        fbLoginButton.setReadPermissions("rsvp_event");
        fbLoginButton.setReadPermissions("user_events");

        fbLoginButton.registerCallback(callbackManager, mFacebookCallBack);
        this.loginActivity = loginActivity;
    }

    public FacebookCallback<LoginResult> getFacebookCallBack() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d("Facebook-login", "callback successful");
                loginActivity.successfulFacebookLogin(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d("Facebook-login", "callback cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Facebook-login", "callback onError");
                Log.d("Facebook-login", exception.getMessage());
            }
        };
    }


    // Gets the callBackManager
    public CallbackManager getCallbackManager() {
        return  callbackManager;
    }
}
