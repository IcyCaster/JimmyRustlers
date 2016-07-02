package com.project.uoa.carpooling.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.services.FacebookConnector;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    // Tracks the Facebook AccessToken and current profile
    public static AccessTokenTracker accessTokenTracker;
    public static ProfileTracker profileTracker;
    private Profile profile;
    private DatabaseReference fireBaseReference;

    private FacebookConnector facebookConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Must initialise FbSDK before setContentView as the view uses Facebook components
        initializeFacebookSDK();
        setContentView(R.layout.activity_login);

        facebookConnector = new FacebookConnector(this);
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if the user is logged on
        if (isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

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
        facebookConnector.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    // Initialize the Facebook components
    protected void initializeFacebookSDK() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
                Profile.fetchProfileForCurrentAccessToken();
            }
        };

    }

    /*
  This method checks to see if the user is currently logged into Facebook and the AccessToken is valid.
   */
    public boolean isLoggedIn() {

        // Fetch the token
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        // Check if it exists (aka. not the users first time using the app/cleared cache)
        if (accessToken == null) {
            return false;
        }
        // Check expiry of the token.
        else if (accessToken.isExpired()) {
            return false;
        }
        // Must be logged in and valid.
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


    public void successfulFacebookLogin(LoginResult loginResult) {
        if (Profile.getCurrentProfile() == null) {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile1, Profile profile2) {
                    // profile2 is the new profile
                    profile = profile2;
                    Log.d("facebook - profile", profile.getFirstName());
                    Log.d("facebook - profile-id", profile.getId());
                    profileTracker.stopTracking();
                }
            };
        } else {
            profile = Profile.getCurrentProfile();
            Log.d("facebook - profile", profile.getFirstName());
            Log.d("facebook - profile-id", profile.getId());
        }

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {

                            final String userId = object.getString("id");
                            Log.d("firebase - fb", userId);
                            final String userName = object.getString("name");
                            Log.d("firebase - fb", userName);

                            Log.d("firebase - fbObj", object.toString());
                            Log.d("firebase - fbresponse", response.toString());

                            fireBaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Log.d("firebase - user found", snapshot.toString());
                                    }
                                    else {
                                        fireBaseReference.child("users").child(userId).setValue(userName);
                                        Log.d("firebase - user created", userId);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

        request.executeAsync();



    }
}
