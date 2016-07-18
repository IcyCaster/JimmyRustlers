package com.project.uoa.carpooling.helpers;

import com.project.uoa.carpooling.entities.facebook.SimpleFacebookEventEntity;

import java.util.Comparator;

/**
 * Created by Chester on 18/07/2016.
 */
public class SimpleEventComparator implements Comparator<SimpleFacebookEventEntity> {

    @Override
    public int compare(SimpleFacebookEventEntity o1, SimpleFacebookEventEntity o2) {
        return Double.compare(o1.getUnixStartTime(), o2.getUnixStartTime());
    }
}
