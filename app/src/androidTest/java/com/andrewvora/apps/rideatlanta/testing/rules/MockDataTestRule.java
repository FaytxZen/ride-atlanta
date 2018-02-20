package com.andrewvora.apps.rideatlanta.testing.rules;

import com.andrewvora.apps.rideatlanta.RideAtlantaApplication;

import org.junit.rules.ExternalResource;

/**
 * Configures {@link RideAtlantaApplication} to use mock data before all tests and reverts to
 * normal data after all tests.
 *
 * Created on 2/10/2018.
 * @author Andrew Vorakrajangthiti
 */
public class MockDataTestRule extends ExternalResource {
	@Override
	protected void before() {
		RideAtlantaApplication.USE_LOCAL = true;
	}

	@Override
	protected void after() {
		RideAtlantaApplication.USE_LOCAL = false;
	}
}
