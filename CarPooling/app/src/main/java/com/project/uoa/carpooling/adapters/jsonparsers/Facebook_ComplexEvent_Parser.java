package com.project.uoa.carpooling.adapters.jsonparsers;

import com.project.uoa.carpooling.entities.facebook.ComplexEventEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chester on 12/07/2016.
 */
public class Facebook_ComplexEvent_Parser {

    public static ComplexEventEntity parse(JSONObject jsonObject) {

        String ID;
        String name;
        String description = "";
        String longitude = "";
        String latitude = "";
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

            ComplexEventEntity entity = new ComplexEventEntity(ID, name, description, longitude, latitude, placeName, startTime, endTime);

            return entity;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
