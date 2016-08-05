package com.project.uoa.carpooling.helpers.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Chester on 6/08/2016.
 *
 * This was created so that we didn't have to keep repeating the "onCancelled" method.
 *
 */
public class FirebaseValueEventListener implements ValueEventListener {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.e("firebase", "The method onDataChange was not overridden!");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("firebase", databaseError.getMessage());
    }
}
