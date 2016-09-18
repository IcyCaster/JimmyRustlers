package com.project.uoa.carpooling.entities.shared;

/**
 * Entity used to represent a location.
 * This entity is used within the ComplexEventEntity.
 *
 * Created by Chester Booker and Angel Castro on 14/07/2016.
 */
public class Place  {
    private String placeName;
    private double longitude;
    private double latitude;

    public Place() {
    }

    public Place(String placeName, double longitude, double latitude) {
        this.placeName = placeName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean hasPlaceName() {
        if(placeName == null || placeName.equals("")) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean hasLatLong() {
        if(latitude == 0.0 && longitude == 0.0) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public String toString() {
        return placeName;
    }
}
