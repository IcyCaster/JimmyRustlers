package com.project.uoa.carpooling.dialogs;

import android.app.DialogFragment;

/**
 * Created by Chester on 14/07/2016.
 */
public class UpdateDetailsDialog extends DialogFragment{


    //this class enables the user to update their driver/passenger details.
    //step 1) warn user + commit update
    //step 3) warn user, remove all registered things, commit updates
    //step 4) warn user, only remove all registered things which are affected (ie passenger capacity 4 > 3, and pass count is 4, remove all), commit updates
    //step 5) warn user about things that are going to be removed if update is committed, only remove all registered things which are affected (ie passenger capacity 4 > 3, and pass count is 4, remove all), commit updates


}
