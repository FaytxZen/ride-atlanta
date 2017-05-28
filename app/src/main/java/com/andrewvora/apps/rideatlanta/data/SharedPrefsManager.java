package com.andrewvora.apps.rideatlanta.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;

/**
 * Created on 5/28/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class SharedPrefsManager {

    private static final String APP_PREF_TAG = "PreferenceStoreForApp";
    private static final String SELECTED_TAB_TAG = "CurrentlySelectedTab";

    @NonNull private SharedPreferences mSharedPrefs;

    public SharedPrefsManager(@NonNull Context context) {
        Application app = (Application) context.getApplicationContext();

        mSharedPrefs = app.getSharedPreferences(APP_PREF_TAG, Context.MODE_PRIVATE);
    }

    @IdRes
    public int getSelectedTab() {
        final int DEFAUL_TAB = R.id.tab_home;
        return mSharedPrefs.getInt(SELECTED_TAB_TAG, DEFAUL_TAB);
    }

    public void setSelectedTab(@IdRes int selectedTab) {
        mSharedPrefs.edit().putInt(SELECTED_TAB_TAG, selectedTab).apply();
    }
}
