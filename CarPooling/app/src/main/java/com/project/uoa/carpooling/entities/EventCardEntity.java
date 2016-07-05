package com.project.uoa.carpooling.entities;

import android.media.Image;

import java.util.Date;

/**
 * Created by Chester on 13/06/2016.
 */
public class EventCardEntity {

    public long id;
    public String eventImageURL;
    public String eventName;
    public String startDate;

    public EventCardEntity(long id, String eventImageURL, String eventName, String startDate) {
        this.id = id;
        this.eventImageURL = eventImageURL;
        this.eventName = eventName;
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "id:" + id;
    }

    public void setImage(String url) {
        this.eventImageURL = url;
    }
}
