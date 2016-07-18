package com.project.uoa.carpooling.adapters.jsonparsers;

import com.project.uoa.carpooling.entities.facebook.SimpleFacebookEventEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts a Facebook Event Response (JSON) object to a SimpleFacebookEventEntity so it can be displayed on the recyclerView.
 *
 * https://developers.facebook.com/docs/graph-api/reference/event
 *
 * Created by Chester on 21/06/2016.
 */
public class Facebook_SimpleEvent_Parser {

    public static SimpleFacebookEventEntity parse(JSONObject response) {

        try {
            SimpleFacebookEventEntity entity = new SimpleFacebookEventEntity(response.getString("id"), response.getString("name"), response.getString("start_time"));
            return entity;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}