package com.project.uoa.carpooling.entities;

import android.media.Image;

import java.util.Date;

/**
 * Created by Chester on 13/06/2016.
 */
public class CarPoolEventEntity {

    private long id;
    private Image eventImage;
    private String eventName;
    private Date startDate;

    public CarPoolEventEntity(long id, Image eventImage, String eventName, Date startDate) {
        this.id = id;
        this.eventImage = eventImage;
        this.eventName = eventName;
        this.startDate = startDate;
    }
}
