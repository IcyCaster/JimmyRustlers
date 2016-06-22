package com.project.uoa.carpooling.jsonparsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chester on 21/06/2016.
 */
public class Facebook_Id_Response {

    public static ArrayList<String> parse(JSONObject response) {

        ArrayList<String> list = new ArrayList<String>();


        JSONArray jarray = null;
        try {
            jarray = response.getJSONArray("data");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject oneAlbum = jarray.getJSONObject(i);
                //get your values
                list.add(oneAlbum.getString("id"));
            }

            return list;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}