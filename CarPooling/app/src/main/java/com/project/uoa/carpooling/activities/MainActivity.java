package com.project.uoa.carpooling.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.main.ArchivedCarpools;
import com.project.uoa.carpooling.fragments.main.CurrentCarpools;
import com.project.uoa.carpooling.fragments.main.FriendGroups;
import com.project.uoa.carpooling.fragments.main.SimpleMessenger;
import com.project.uoa.carpooling.helpers.firebase.FirebaseValueEventListener;

/**
 * MainActivity is created to houses all of the events the user has joined and displayed for them to easily access them all.
 * <p/>
 * From this the user can go to these events or join additional ones.
 * <p/>
 * * Created by Angel and Chester
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ArchivedCarpools.OnFragmentInteractionListener, FriendGroups.OnFragmentInteractionListener {

    private String userID;

    public String getUserID() {
        return userID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allows the accesstoken to be retrieved so that Facebook requests can be made
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Get the userID stored in the shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        userID = sharedPreferences.getString("Current Facebook App-scoped ID", "");

        setContentView(R.layout.nav__drawer_layout);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Display the subscribed carpools fragment
        displayView(R.id.nav_subscribed_carpools);
    }

    // Closes the drawer if the back button is pressed instead of closing the application entirely
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav__menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // This completed resets the database in case something completely unexpected happens during exhibition
        if (id == R.id.kill_switch) {
            executeFirebaseReset();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    // Navigation logic here
    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name_short);

        switch (viewId) {
            // Mainpage, overview of all subscribed carpools
            case R.id.nav_subscribed_carpools:
                fragment = new CurrentCarpools();
                title = "Carpools";
                break;

            // Unused archived list of carpools
            case R.id.nav_archived_pools:
                fragment = new ArchivedCarpools();
                title = "Archived Carpools";
                break;

            // Unused list of friend groups
            case R.id.nav_friend_groups:
                fragment = new FriendGroups();
                title = "Friend Groups";
                break;

            // Settings currently contains a global messenger
            case R.id.nav_settings:
                fragment = new SimpleMessenger();
                title = "Global Messenger";
                break;

            // THIS DOES NOTHING
            case R.id.nav_help:
                Profile profile = Profile.getCurrentProfile();
                title = "Hello, " + profile.getFirstName() + "!";
                break;

            // Logs the user out of the application.
            case R.id.nav_logout:
                LoginManager.getInstance().logOut();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.contentFragment, fragment);
            ft.commit();
        }

        // Set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void onFragmentInteraction(Uri uri) {
        // Kept Empty
    }

    // Kill switch. Only clears firebase, does not add test accounts.
    public void executeFirebaseReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("Don't mind me!");
        builder.setMessage("Only press okay if you know what you're doing!");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        final DatabaseReference fireBaseReference = FirebaseDatabase.getInstance().getReference();
                        fireBaseReference.child("events").removeValue();
                        fireBaseReference.child("users").addListenerForSingleValueEvent(new FirebaseValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    fireBaseReference.child("users").child(child.getKey()).child("events").removeValue();
                                }
                                Intent i = new Intent(MainActivity.this, MainActivity.class);
                                finish();
                                startActivity(i);
                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.show();
    }
}