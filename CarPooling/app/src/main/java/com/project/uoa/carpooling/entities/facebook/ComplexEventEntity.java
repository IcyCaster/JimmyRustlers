package com.project.uoa.carpooling.entities.facebook;

import android.util.Log;
import android.os.Parcel;
import android.os.Parcelable;

import com.project.uoa.carpooling.entities.shared.Place;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Entity used to represent an event. Used to hold the extracted
 * event data from the users' Facebook accounts. A more complex
 * version of SimpleEventEntity which was implemented to be sent
 * within Android intents as a Parcelable.
 *
 * Created by Chester Booker and Angel Castro on 13/08/2016.
 */
public class ComplexEventEntity implements Parcelable {

    private String ID;
    private String name;
    private String description;
    private Place location;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private String prettyStartTime;
    private String prettyEndTime;
    private String longStartDate;
    private String longStartTime;
    private double unixStartTime;

    public ComplexEventEntity(String ID, String name, String description, String longitude, String latitude, String placeName, String startTime, String endTime) {
        this.ID = ID;
        this.name = name;
        this.description = description;

        double longitudeAsDouble = 0.0;
        double latitudeAsDouble = 0.0;

        try {

            Log.d("fb", longitude + latitude);
            longitudeAsDouble = Double.parseDouble(longitude);
            latitudeAsDouble = Double.parseDouble(latitude);
        } catch (NumberFormatException e) {
Log.e("numberformatexception", "lat or long");
        }

        this.location = new Place(placeName, longitudeAsDouble, latitudeAsDouble);

        DateFormat facebookTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        DateFormat prettyFormat = new SimpleDateFormat("MMM d, h:mmaa", Locale.ENGLISH);

        DateFormat longDateFormat = new SimpleDateFormat("EEEE, d MMMM y", Locale.ENGLISH);

        DateFormat longTimeFormat = new SimpleDateFormat("h:mmaa", Locale.ENGLISH);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        try {

            startCalendar.setTime(facebookTimeFormat.parse(startTime));
            prettyStartTime = prettyFormat.format(startCalendar.getTime());

            longStartDate = longDateFormat.format(startCalendar.getTime());
            longStartTime = longTimeFormat.format(startCalendar.getTime());

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

    public String getLongStartDate() {
        return longStartDate;
    }

    public void setLongStartDate(String longStartDate) {
        this.longStartDate = longStartDate;
    }

    public String getLongStartTime() {
        return longStartTime;
    }

    public void setLongStartTime(String longStartTime) {
        this.longStartTime = longStartTime;
    }

    protected ComplexEventEntity(Parcel in) {
        ID = in.readString();
        name = in.readString();
        description = in.readString();
        location = new Place(in.readString(), in.readDouble(), in.readDouble());
        startCalendar = (Calendar) in.readValue(Calendar.class.getClassLoader());
        endCalendar = (Calendar) in.readValue(Calendar.class.getClassLoader());
        prettyStartTime = in.readString();
        prettyEndTime = in.readString();
        longStartDate = in.readString();
        longStartTime = in.readString();
        unixStartTime = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(location.toString());
        dest.writeDouble(location.getLatitude());
        dest.writeDouble(location.getLongitude());
        dest.writeValue(startCalendar);
        dest.writeValue(endCalendar);
        dest.writeString(prettyStartTime);
        dest.writeString(prettyEndTime);
        dest.writeString(longStartDate);
        dest.writeString(longStartTime);
        dest.writeDouble(unixStartTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ComplexEventEntity> CREATOR = new Parcelable.Creator<ComplexEventEntity>() {
        @Override
        public ComplexEventEntity createFromParcel(Parcel in) {
            return new ComplexEventEntity(in);
        }

        @Override
        public ComplexEventEntity[] newArray(int size) {
            return new ComplexEventEntity[size];
        }
    };
}