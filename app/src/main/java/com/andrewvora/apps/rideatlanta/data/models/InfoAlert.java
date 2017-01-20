package com.andrewvora.apps.rideatlanta.data.models;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.common.HomeItemModel;
import com.andrewvora.apps.rideatlanta.common.InfoItemModel;

/**
 * Created by faytx on 1/11/2017.
 * @author Andrew Vorakrajangthiti
 */
public class InfoAlert implements InfoItemModel {

    private int actionType;
    private String infoText;

    @Override
    public int getViewType() {
        return HomeItemModel.VIEW_TYPE_INFO_ITEM;
    }

    @Override
    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(@NonNull String text) {
        this.infoText = text;
    }

    @Override
    public int getActionType() {
        return actionType;
    }

    public void setActionType(@IntRange(from=InfoItemModel.SEE_AND_SAY,
                                        to=InfoItemModel.TIP_ABOUT_FAVORITES) int actionType)
    {
        this.actionType = actionType;
    }

    @Override
    public String getActionText(@NonNull Context context) {
        switch(getActionType()) {
            case SEE_AND_SAY:
                return context.getString(R.string.button_see_and_say);

            case TIP_ABOUT_FAVORITES:
                return context.getString(R.string.button_tips);
        }

        return "";
    }
}
