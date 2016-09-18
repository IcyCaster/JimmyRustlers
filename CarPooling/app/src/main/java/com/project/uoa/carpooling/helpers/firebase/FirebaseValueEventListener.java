package com.project.uoa.carpooling.helpers.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * FirebaseValueEventListener is used so that our classes look better.
 * Rather than overriding all these methods each time we want to use a ValueEventListener for Firebase
 * We just call this.
 * Created by Chester and Angel on 14/08/2016.
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
