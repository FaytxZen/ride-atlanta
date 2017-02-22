package com.andrewvora.apps.rideatlanta.common;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public interface BasePresenter {

    void onSaveState(Bundle outState);
    void onRestoreState(Bundle savedState);
    void start();
    void stop();
}
