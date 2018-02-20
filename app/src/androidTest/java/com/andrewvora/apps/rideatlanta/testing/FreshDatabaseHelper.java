package com.andrewvora.apps.rideatlanta.testing;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.andrewvora.apps.rideatlanta.data.local.RideAtlantaDbHelper;

/**
 * Created on 2/11/2018.
 *
 * @author Andrew Vorakrajangthiti
 */
public class FreshDatabaseHelper {

	public void cleanDatabase() {
		final Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
		final RideAtlantaDbHelper dbHelper = new RideAtlantaDbHelper(context);
		dbHelper.clearAllRecords();
	}
}
