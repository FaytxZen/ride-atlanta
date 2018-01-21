package com.andrewvora.apps.rideatlanta;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void tearDown() {

    }
}
