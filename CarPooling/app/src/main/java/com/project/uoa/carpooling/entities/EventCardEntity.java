package com.project.uoa.carpooling.entities;

import android.media.Image;

import java.util.Date;

/**
 * Created by Chester on 13/06/2016.
 */
public class EventCardEntity {

    public long id;
    public int eventImageId;
    public String eventName;
    public String startDate;

    public EventCardEntity(long id, int eventImageId, String eventName, String startDate) {
        this.id = id;
        this.eventImageId = eventImageId;
        this.eventName = eventName;
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return eventName + " is here!";
    }
}
