package com.project.uoa.carpooling.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static AccessTokenTracker accessTokenTracker; // Facebook AccessToken Tracker
    public static ProfileTracker profileTracker; // Facebook Profile Tracker
    private DatabaseReference fireBaseReference; // Root Firebase Reference
    private SharedPreferences sharedPreferences; // Access to SharedPreferences

    private FacebookConnector facebookConnector; // Abstracts away the connection to Facebook
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise FbSDK before setContentView, as the view uses Facebook components
        initializeFacebookSDK();
        setContentView(R.layout.activity_login);

        // Initialise shared preferences
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Connect to Facebook and Firebase
        facebookConnector = new FacebookConnector(this);
        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if the user is already logged on
        if (isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // JUNK BUTTON. ONLY HERE FOR IF WE IMPLEMENT OUR OWN LOGIN
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
        // Don't actually know what this does, Facebook tutorials told me to add it. ¯\_(ツ)_/¯
        facebookConnector.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    // Initialize the Facebook components and tracks the current user's access token.
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
  This method checks to see if the user is currently logged into Facebook and if the AccessToken is valid.
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

    // This occurs on a successful Facebook Login
    public void successfulFacebookLogin(LoginResult loginResult) {

        // This profile tracking will need to be revised in the future
        if (Profile.getCurrentProfile() == null) {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile1, Profile profile2) {
                    // profile2 is the new profile
                    profile = profile2;
                    Log.d("facebook - profile", profile.getId() + " : " + profile.getFirstName());
                    profileTracker.stopTracking();
                }
            };
        } else {
            profile = Profile.getCurrentProfile();
            Log.d("facebook - profile", profile.getId() + " : " + profile.getFirstName());
        }

        // Retrieve Facebook's app-scoped ID and matches it to the Firebase database.
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            // Gets user's ID and name
                            Log.d("facebook - response", response.toString());
                            final String userId = object.getString("id");
                            final String userName = object.getString("name");

                            // Keep a reference of this App-scoped ID for future Firebase use
                            sharedPreferences.edit().putString("Current Facebook App-scoped ID", userId).apply();

                            // Checks DB/users/{user-id}
                            fireBaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    // If it exists, everything is sweet
                                    if (snapshot.exists()) {
                                        Log.d("firebase - user found", snapshot.toString());
                                    }
                                    // If it doesn't, create the user in the Firebase database
                                    else {
                                        fireBaseReference.child("users").child(userId).push();
                                        fireBaseReference.child("users").child(userId).child("Name").setValue(userName);
                                        Log.d("firebase - user created", userId);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError firebaseError) {
                                    Log.e("firebase - error", firebaseError.getMessage());
                                }
                            });
                        } catch (JSONException e) {
                            Log.e("facebook - JSON-error", e.getMessage());
                        }
                    }
                });
        request.executeAsync();
    }
}
