package com.project.uoa.carpooling.services;

/**
 * Created by Chester on 2/08/2016.
 */
import android.app.Service;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutorialService extends Service {

    private DatabaseReference fireBaseReference;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {

        fireBaseReference = FirebaseDatabase.getInstance().getReference();

        fireBaseReference.child("TestDriver").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                if(dataSnapshot.getKey().equals("CurrentLocation")) {

                    Log.d("Loc1", "Result: " + previousChildName);
                    Log.d("Loc2", "Result: " + dataSnapshot.toString());

                    dataSnapshot.child("Latitude").getValue();


                    broadcastIntent((long) dataSnapshot.child("Longitude").getValue(), (long) dataSnapshot.child("Latitude").getValue());

                }
            }

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("TutorialService",
                10);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Well calling mServiceHandler.sendMessage(message); from onStartCommand,
            // this method will be called.

            // Add your cpu-blocking activity here
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            showToast("Finishing TutorialService, id: " + msg.arg1);
            // the msg.arg1 is the startId used in the onStartCommand,
            // so we can track the running sevice here.
            stopSelf(msg.arg1);
        }
    }

    public void broadcastIntent(long longitude, long latitude) {
        Intent intent = new Intent();
        intent.setAction("com.example.Broadcast");
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Longitude", longitude);
        sendBroadcast(intent);
    }
}
