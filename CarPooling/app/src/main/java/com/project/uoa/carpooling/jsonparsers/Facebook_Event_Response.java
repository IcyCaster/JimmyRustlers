package com.project.uoa.carpooling.jsonparsers;

import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.entities.CarPoolEventEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chester on 21/06/2016.
 */
public class Facebook_Event_Response {

    public static CarPoolEventEntity parse(JSONObject response) {

   JSONArray jarray = null;
        try {
            jarray = response.getJSONArray("data");

            CarPoolEventEntity entity = null;

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject oneAlbum = jarray.getJSONObject(i);
                //get your values
                entity = new CarPoolEventEntity(Long.parseLong(oneAlbum.getString("id")), R.drawable.test, oneAlbum.getString("name"), oneAlbum.getString("start_time"));
            }

            return entity;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}