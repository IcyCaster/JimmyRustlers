package com.project.uoa.carpooling.jsonparsers;

import android.util.Log;

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


        try {


            CarPoolEventEntity entity = new CarPoolEventEntity(Long.parseLong(response.getString("id")), R.drawable.test, response.getString("name"), response.getString("start_time"));

            Log.d("FB", entity.toString());


            return entity;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}