package com.andrewvora.apps.rideatlanta.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by faytx on 11/13/2016.
 * @author Andrew Vorakrajangthiti
 */

public class CachedDataMap {
    @Nullable
    private static CachedDataMap mInstance;

    @NonNull
    private Map<String, Boolean> mHasCachedDataMap;

    private CachedDataMap() {
        mHasCachedDataMap = new HashMap<>();
    }

    public static synchronized CachedDataMap getInstance() {
        if(mInstance == null) {
            mInstance = new CachedDataMap();
        }

        return mInstance;
    }

    public boolean hasCachedData(String tag) {
        if(mHasCachedDataMap.containsKey(tag)) {
            return mHasCachedDataMap.get(tag);
        }
        else {
            return false;
        }
    }

    public void put(String tag, Boolean hasCachedData) {
        mHasCachedDataMap.put(tag, hasCachedData);
    }

    public void clear() {
        mHasCachedDataMap.clear();
    }
}
