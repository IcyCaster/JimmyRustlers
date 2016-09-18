package com.project.uoa.carpooling.helpers.comparators;

import com.project.uoa.carpooling.entities.facebook.SimpleEventEntity;

import java.util.Comparator;

/**
 * Comparator for SimpleEventEntities.
 *
 * Created by Chester Booker and Angel Castro on 18/07/2016.
 */
public class SimpleEventComparator implements Comparator<SimpleEventEntity> {

    @Override
    public int compare(SimpleEventEntity o1, SimpleEventEntity o2) {
        return Double.compare(o1.getUnixStartTime(), o2.getUnixStartTime());
    }
}
