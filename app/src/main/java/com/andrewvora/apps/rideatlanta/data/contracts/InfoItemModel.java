package com.andrewvora.apps.rideatlanta.data.contracts;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by faytx on 1/7/2017.
 * @author Andrew Vorakrajangthiti
 */
public interface InfoItemModel extends HomeItemModel {

    int SEE_AND_SAY = 0;
    int TIP_ABOUT_FAVORITES = 1;

    String getInfoText();
    String getActionText(@NonNull Context context);
    int getActionType();
}
