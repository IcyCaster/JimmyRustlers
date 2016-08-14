package com.project.uoa.carpooling.helpers.directions;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;
import com.project.uoa.carpooling.entities.maps.Distance;
import com.project.uoa.carpooling.entities.maps.Duration;
import com.project.uoa.carpooling.entities.maps.Leg;
import com.project.uoa.carpooling.entities.maps.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Credits to: Mai Thanh Hiep.
 */
public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String TAG = "DirectionFinder";

    private final String GOOGLE_API_KEY;
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    // Firebase reference
    private DatabaseReference fireBaseReference;

    public DirectionFinder(DirectionFinderListener listener, String origin, String dest, String APIKey) {
        this.listener = listener;
        this.origin = origin;
        this.destination = dest;
        this.GOOGLE_API_KEY = APIKey;

        this.fireBaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    // Creates URL string for Google Directions Web API HTTP request.
    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        // Can supply a url with waypoints as well.
        Log.d("Route Request: ", DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY);
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    // Async Task for executing and downloading response from Directions API.
    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
            }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSON(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSON(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);

        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            Log.d("TEST", "parseJSON: route " + i);
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();
            route.legs = new ArrayList<>();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            for (i = 0; i < jsonLegs.length(); i++) {
                Leg leg = new Leg();

                JSONObject jsonLeg = jsonLegs.getJSONObject(i);
                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                leg.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                leg.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                leg.endAddress = jsonLeg.getString("end_address");
                leg.startAddress = jsonLeg.getString("start_address");
                leg.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                leg.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));

                route.legs.add(leg);
            }

            route.points = PolyUtil.decode(overview_polylineJson.getString("points"));
            routes.add(route);
        }
        listener.onDirectionFinderSuccess(routes);
    }
}
