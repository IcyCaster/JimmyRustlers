package com.project.uoa.carpooling.entities.facebook;

import com.project.uoa.carpooling.entities.shared.Place;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Chester on 14/07/2016.
 */
public class ComplexEventEntity {

    private String ID;
    private String name;
    private String description;
    private Place location;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private String prettyStartTime;
    private String prettyEndTime;
    private double unixStartTime;

    public ComplexEventEntity(String ID, String name, String description, String longitude, String latitude, String placeName, String startTime, String endTime) {
        this.ID = ID;
        this.name = name;
        this.description = description;

        double longitudeAsDouble = 0.0;
        double latitudeAsDouble = 0.0;

        try {
            longitudeAsDouble = Double.parseDouble(longitude);
            latitudeAsDouble = Double.parseDouble(latitude);
        } catch (NumberFormatException e) {

        }

        this.location = new Place(placeName, longitudeAsDouble, latitudeAsDouble);

        DateFormat facebookTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        DateFormat prettyFormat = new SimpleDateFormat("MMM d, h:mmaa", Locale.ENGLISH);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        try {

            startCalendar.setTime(facebookTimeFormat.parse(startTime));
            prettyStartTime = prettyFormat.format(startCalendar.getTime());

            if (!endTime.equals("")) {
                endCalendar.setTime(facebookTimeFormat.parse(endTime));
                prettyEndTime = prettyFormat.format(endCalendar.getTime());
            }

            unixStartTime = facebookTimeFormat.parse(startTime).getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }

    public Calendar getStartCalendar() {
        return startCalendar;
    }

    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }

    public Calendar getEndCalendar() {
        return endCalendar;
    }

    public void setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
    }

    public String getPrettyStartTime() {
        return prettyStartTime;
    }

    public void setPrettyStartTime(String prettyStartTime) {
        this.prettyStartTime = prettyStartTime;
    }

    public String getPrettyEndTime() {
        return prettyEndTime;
    }

    public void setPrettyEndTime(String prettyEndTime) {
        this.prettyEndTime = prettyEndTime;
    }

    public double getUnixStartTime() {
        return unixStartTime;
    }

    public void setUnixStartTime(double unixStartTime) {
        this.unixStartTime = unixStartTime;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
