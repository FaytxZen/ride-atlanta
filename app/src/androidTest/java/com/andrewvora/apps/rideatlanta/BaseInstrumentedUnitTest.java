package com.andrewvora.apps.rideatlanta;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(AndroidJUnit4.class)
public abstract class BaseInstrumentedUnitTest {

    @Rule
    protected ActivityTestRule<MainActivity> mMainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }
}
