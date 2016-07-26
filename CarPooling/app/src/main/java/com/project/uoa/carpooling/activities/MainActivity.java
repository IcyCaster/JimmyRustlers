package com.project.uoa.carpooling.activities;

import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.fragments.main.ArchivedCarpools;
import com.project.uoa.carpooling.fragments.main.SimpleMessenger;
import com.project.uoa.carpooling.fragments.carpool.Event_Explorer;
import com.project.uoa.carpooling.fragments.carpool.Event_Details;
import com.project.uoa.carpooling.fragments.carpool.Event_Map;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Offers;
import com.project.uoa.carpooling.fragments.carpool.Explorer_Requests;
import com.project.uoa.carpooling.fragments.main.FriendGroups;
import com.project.uoa.carpooling.fragments.main.CurrentCarpools;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CurrentCarpools.OnFragmentInteractionListener, ArchivedCarpools.OnFragmentInteractionListener, FriendGroups.OnFragmentInteractionListener, SimpleMessenger.OnFragmentInteractionListener, Event_Details.OnFragmentInteractionListener, Event_Explorer.OnFragmentInteractionListener, Event_Map.OnFragmentInteractionListener, Explorer_Requests.OnFragmentInteractionListener, Explorer_Offers.OnFragmentInteractionListener {

    private String userID;

    public String getUserID() {
        return userID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        if (id == R.id.action_settings) {
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
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_subscribed_carpools:
                fragment = new CurrentCarpools();
                title = "Car Pools";
                break;

            case R.id.nav_archived_pools:
                fragment = new ArchivedCarpools();
                title = "Archived Pools";
                break;

            case R.id.nav_friend_groups:
                fragment = new FriendGroups();
                title = "Friend Groups";
                break;

            case R.id.nav_settings:
                fragment = new SimpleMessenger();
                title = "Global Messenger";
                break;

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
}
