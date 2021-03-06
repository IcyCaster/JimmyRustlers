package com.project.uoa.carpooling.helpers.comparators;

import com.project.uoa.carpooling.fragments.carpool._entities.PassengerEntity;

import java.util.Comparator;

/**
 * Created by Chester on 18/07/2016.
 */
public class PassengerComparator implements Comparator<PassengerEntity> {

    @Override
    public int compare(PassengerEntity o1, PassengerEntity o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
