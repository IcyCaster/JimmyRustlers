package com.project.uoa.carpooling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CarPools.OnFragmentInteractionListener, ArchivedPools.OnFragmentInteractionListener, FriendGroups.OnFragmentInteractionListener, CarPoolEvent.OnFragmentInteractionListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nav_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_car_pools);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.car_pools, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        displayView(item.getItemId());

//        Fragment fragment = null;
//        Class fragmentClass = null;
//
//
//
        return true;
    }

    public void displayView(int viewId) {


        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_car_pools) {
//            fragmentClass = CarPools.class;
//
//            CarPools testFrag = new CarPools();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.contentFragment, testFrag).commit();
//
//        } else if (id == R.id.nav_archived_pools) {
//            fragmentClass = ArchivedPools.class;
//        } else if (id == R.id.nav_friend_groups) {
//            fragmentClass = FriendGroups.class;
//        } else if (id == R.id.nav_settings) {
//            // TEMP FOR TESTING OUT EVENT DISPLAYS
//
//            fragmentClass = CarPoolEvent.class;
//        } else if (id == R.id.nav_help) {
//
//            // TEMP FOR TESTING NAME RETRIEVAL
//            Log.d("Test First Name:", "BEFORE");
//            Profile profile = Profile.getCurrentProfile();
//            Log.d("Test First Name:", profile.getFirstName());
//
//        }else if (id == R.id.nav_logout) {
//            LoginManager.getInstance().logOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            finish();
//        }


        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_car_pools:
                fragment = new CarPools();
                title = "Car Pools";
                break;

            case R.id.nav_archived_pools:
                fragment = new ArchivedPools();
                title = "Archived Pools";
                break;

            case R.id.nav_friend_groups:
                fragment = new FriendGroups();
                title = "Friend Groups";
                break;

            case R.id.nav_settings:
                fragment = new CarPoolEvent();
                title = "TEMP CAR POOL EVENT";
                break;

            case R.id.nav_help:
                // TEMP FOR TESTING NAME RETRIEVAL
                Log.d("Test First Name:", "BEFORE");
                Profile profile = Profile.getCurrentProfile();
                Log.d("Test First Name:", profile.getFirstName());
                title = "Hello, " + profile.getFirstName() + "!";
                break;

            case R.id.nav_logout:
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
