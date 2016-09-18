package com.project.uoa.carpooling.helpers.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * FirebaseChildEventListener is used so that our classes look better.
 * Rather than overriding all these methods each time we want to use a ChildEventListener for Firebase
 * We just call this.
 * Created by Chester and Angel on 14/08/2016.
 */
public class FirebaseChildEventListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.e("firebase", "The method onChildAdded was not overridden!");

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.e("firebase", "The method onChildChanged was not overridden!");

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.e("firebase", "The method onChildRemoved was not overridden!");

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.e("firebase", "The method onChildMoved was not overridden!");

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e("firebase", databaseError.getMessage());
    }
}
