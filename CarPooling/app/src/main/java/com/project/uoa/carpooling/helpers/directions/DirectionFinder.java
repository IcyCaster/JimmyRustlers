package com.project.uoa.carpooling.helpers.directions;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
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
 * Code below adapted from: https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
 */
public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String WAYPOINT_URL = "&waypoints=optimize:true";
    private static final String TAG = "DirectionFinder";

    private final String GOOGLE_API_KEY;
    private List<String> passengerLocations = new ArrayList<>();
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String dest, String APIKey) {
        this.listener = listener;
        this.origin = origin;
        this.destination = dest;
        this.GOOGLE_API_KEY = APIKey;
    }

    public DirectionFinder(DirectionFinderListener listener, String origin, String dest, String APIKey, List<String> passengerLocations) {
        this.listener = listener;
        this.origin = origin;
        this.destination = dest;
        this.GOOGLE_API_KEY = APIKey;
        this.passengerLocations = passengerLocations;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    // Creates URL string for Google Directions Web API HTTP request.
    private String createUrl() throws UnsupportedEncodingException {
//        String urlOrigin = URLEncoder.encode(origin, "utf-8");
//        String urlDestination = URLEncoder.encode(destination, "utf-8");

        // Construct URL
        String requestURL = DIRECTION_URL_API + "origin=" + origin + "&destination=" + destination;

        if (!passengerLocations.isEmpty()) {
            //Append additional waypoint data, if available.
            requestURL += WAYPOINT_URL;

            for(String location : passengerLocations) {
                requestURL += "|"  + location;
            }
        }

        requestURL += "&key=" + GOOGLE_API_KEY;
        Log.d(TAG, requestURL);

        return requestURL;
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

                route.waypointOrder = new ArrayList<>();
                JSONArray jsonWaypointOrder = jsonRoute.getJSONArray("waypoint_order");

                for (int j = 0; j < jsonWaypointOrder.length(); j++) {
                    route.waypointOrder.add(jsonWaypointOrder.getInt(j));
                }
            }

            route.points = PolyUtil.decode(overview_polylineJson.getString("points"));
            routes.add(route);
        }
        listener.onDirectionFinderSuccess(routes);
    }
}
