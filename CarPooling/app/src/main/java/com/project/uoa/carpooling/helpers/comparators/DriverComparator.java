package com.project.uoa.carpooling.helpers.comparators;

import com.project.uoa.carpooling.fragments.carpool._entities.DriverEntity;

import java.util.Comparator;

/**
 * Created by Chester on 24/07/2016.
 */
public class DriverComparator implements Comparator<DriverEntity> {

    @Override
    public int compare(DriverEntity o1, DriverEntity o2) {
        return o1.getName().compareTo(o2.getName());
    }
}