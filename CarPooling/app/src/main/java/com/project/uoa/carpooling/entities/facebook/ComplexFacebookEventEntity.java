package com.project.uoa.carpooling.entities.facebook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chester on 14/07/2016.
 */
public class ComplexFacebookEventEntity {

    private String ID;
    private String name;
    private String description;
    private Place location;
    private String startTime;
    private String endTime;
    private String prettyStartTime;
    private String prettyEndTime;
    private double unixStartTime;

    public ComplexFacebookEventEntity(String ID, String name, String description, String longitude, String latitude, String placeName, String startTime, String endTime) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.location = new Place(placeName, longitude, latitude);
        this.startTime = startTime;
        this.endTime = endTime;

        DateFormat facebookTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);


        DateFormat prettyFormat = new SimpleDateFormat("MMM d - h aa", Locale.ENGLISH);

//        Date date = originalFormat.parse("August 21, 2012");
//        String formattedDate = targetFormat.format(date);  // 20120821

//        String timeStamp = "2014-12-14T18:23:17+0000";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
//        try {
//            System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }


    public double getUnixStartTime() {
        return unixStartTime;
    }

    public void setUnixStartTime(double unixStartTime) {
        this.unixStartTime = unixStartTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Place getLocation() {
        return location;
    }

    public void setLocation(Place location) {
        this.location = location;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
