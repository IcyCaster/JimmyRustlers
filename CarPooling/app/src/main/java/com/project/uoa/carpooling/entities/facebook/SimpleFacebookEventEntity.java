package com.project.uoa.carpooling.entities.facebook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Chester on 13/06/2016.
 */
public class SimpleFacebookEventEntity {

    private String eventID;
    private String eventImageURL = null;
    private String eventName;
    private Calendar startCalendar;
    private String prettyStartTime;
    private double unixStartTime;

    public SimpleFacebookEventEntity(String eventID, String eventName, String startTime) {
        this.eventID = eventID;
        this.eventName = eventName;

        DateFormat facebookTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        DateFormat prettyFormat = new SimpleDateFormat("MMM d, h:mmaa", Locale.ENGLISH);

        startCalendar = Calendar.getInstance();
        try {

            startCalendar.setTime(facebookTimeFormat.parse(startTime));

            prettyStartTime = prettyFormat.format(startCalendar.getTime());

            unixStartTime = facebookTimeFormat.parse(startTime).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "eventID:" + eventID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventImageURL() {
        return eventImageURL;
    }

    public void setImage(String eventImageURL) {
        this.eventImageURL = eventImageURL;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Calendar getStartCalendar() {
        return startCalendar;
    }

    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }

    public String getPrettyStartTime() {
        return prettyStartTime;
    }

    public void setPrettyStartTime(String prettyStartTime) {
        this.prettyStartTime = prettyStartTime;
    }

    public double getUnixStartTime() {
        return unixStartTime;
    }

    public void setUnixStartTime(double unixStartTime) {
        this.unixStartTime = unixStartTime;
    }
}
