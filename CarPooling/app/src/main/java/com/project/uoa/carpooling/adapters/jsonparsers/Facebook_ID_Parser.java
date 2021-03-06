package com.project.uoa.carpooling.adapters.jsonparsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Converts a Facebook EventIDs Response (JSON) object to a list of EventID strings.
 * This will be changed in the future to
 *
 * https://developers.facebook.com/docs/facebook-login/permissions#reference-user_events
 *
 * Created by Chester on 21/06/2016.
 */
public class Facebook_ID_Parser {

    public static ArrayList<String> parse(JSONObject response) {

        ArrayList<String> list = new ArrayList<String>();

        JSONArray jarray = null;
        try {
            jarray = response.getJSONArray("data");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject oneAlbum = jarray.getJSONObject(i);
                // Get your values
                list.add(oneAlbum.getString("id"));
            }
            return list;

        } catch (JSONException e) {
            return null;
        }
    }
}
