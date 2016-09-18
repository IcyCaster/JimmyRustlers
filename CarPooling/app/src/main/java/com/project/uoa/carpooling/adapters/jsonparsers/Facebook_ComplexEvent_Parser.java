package com.project.uoa.carpooling.adapters.jsonparsers;

import android.util.Log;

import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Facebook_ComplexEvent_Parser is created to convert the Facebook response JSON objects to complex objects.
 *
 * * Created by Angel and Chester on 12/07/2016.
 */
public class Facebook_ComplexEvent_Parser {

    public static ComplexEventEntity parse(JSONObject jsonObject) {

        String ID;
        String name;
        String description = "";
        String longitude = "0";
        String latitude = "0";
        String placeName = "";
        String startTime;
        String endTime = "";

        try {
            // ID, name and startTime are always guaranteed
            ID = jsonObject.getString("id");
            name = jsonObject.getString("name");
            startTime = jsonObject.getString("start_time");

            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("end_time")) {
                endTime = jsonObject.getString("end_time");
            }
            if (jsonObject.has("place")) {

                JSONObject placeObject = jsonObject.getJSONObject("place");

                if (placeObject.has("name")) {
                    placeName = placeObject.getString("name");
                }

                if (placeObject.has("location")) {

                    JSONObject locationObject = placeObject.getJSONObject("location");

                    if (locationObject.has("longitude")) {
                        longitude = locationObject.getString("longitude");
                    }
                    if (locationObject.has("latitude")) {
                        latitude = locationObject.getString("latitude");
                    }
                }
            }

            // Return a Complex Event Entity
            return new ComplexEventEntity(ID, name, description, longitude, latitude, placeName, startTime, endTime);

        } catch (JSONException e) {
            Log.e("ComplexEventParse", e.getMessage());
            return null;
        }
    }
}