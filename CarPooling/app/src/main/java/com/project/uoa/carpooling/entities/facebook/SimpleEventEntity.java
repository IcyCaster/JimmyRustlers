package com.project.uoa.carpooling.entities.facebook;

/**
 * Created by Chester on 13/06/2016.
 */
public class SimpleEventEntity {

    public String eventID;
    public String eventImageURL;
    public String eventName;
    public String startDate;

    public SimpleEventEntity(String eventID, String eventImageURL, String eventName, String startDate) {
        this.eventID = eventID;
        this.eventImageURL = eventImageURL;
        this.eventName = eventName;
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "eventID:" + eventID;
    }

    public void setImage(String url) {
        this.eventImageURL = url;
    }
}
