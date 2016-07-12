package com.project.uoa.carpooling.jsonparsers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.activities.CarpoolEventActivity;
import com.project.uoa.carpooling.entities.EventCardEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Converts a Facebook Event Response (JSON) object to a EventCardEntity so it can be displayed on the recyclerView.
 *
 * https://developers.facebook.com/docs/graph-api/reference/event
 *
 * Created by Chester on 21/06/2016.
 */
public class Facebook_Event_Response {

    public static EventCardEntity parse(JSONObject response) {



        try {

            // EventID, EventImage, EventName, StartTime
            // TODO: Swap R.drawable.test with the actual event image.
            // TODO: Format how the startTime is displayed

            EventCardEntity entity = new EventCardEntity(Long.parseLong(response.getString("id")), "TEMP", response.getString("name"), response.getString("start_time"));

            Log.d("JSON->Entity:", entity.toString());

            return entity;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}