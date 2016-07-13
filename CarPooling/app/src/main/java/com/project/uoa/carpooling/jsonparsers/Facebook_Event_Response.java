package com.project.uoa.carpooling.jsonparsers;

import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts a Facebook Event Response (JSON) object to a SimpleEventEntity so it can be displayed on the recyclerView.
 *
 * https://developers.facebook.com/docs/graph-api/reference/event
 *
 * Created by Chester on 21/06/2016.
 */
public class Facebook_Event_Response {

    public static SimpleEventEntity parse(JSONObject response) {

        try {

            // EventID, EventImage, EventName, StartTime
            // TODO: Swap R.drawable.test with the actual event image.
            // TODO: Format how the startTime is displayed

            SimpleEventEntity entity = new SimpleEventEntity(response.getString("id"), "TEMPORARY", response.getString("name"), response.getString("start_time"));
            return entity;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}