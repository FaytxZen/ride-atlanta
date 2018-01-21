package com.andrewvora.apps.rideatlanta.buses;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.models.Bus;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link BusRoutesAdapter}.
 *
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesAdapterTest extends BaseUnitTest {

    @Mock
    private List<Bus> mMockBusRoutes;
    @Mock
    private BusRoutesFragment.BusItemListener mListener;

    private BusRoutesAdapter mBusAdapter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mBusAdapter = new BusRoutesAdapter(mMockBusRoutes, mListener);
    }

    @Test
    public void getItemCount() {
        int sizeToReturn = 5;

        when(mMockBusRoutes.size()).thenReturn(sizeToReturn);

        assertEquals(sizeToReturn, mBusAdapter.getItemCount());
    }

    @Test
    public void getItemCount_constructorWithNullList() {
        mBusAdapter = new BusRoutesAdapter(null, mListener);

        assertEquals(0, mBusAdapter.getItemCount());
    }

    @Test
    public void getItemCount_constructorWithEmptyList() {
        mBusAdapter = new BusRoutesAdapter(new ArrayList<>(), mListener);

        assertEquals(0, mBusAdapter.getItemCount());
    }

    @Test
    public void getItemAtPosition() {
        Bus mockBus1 = mock(Bus.class);
        Bus mockBus2 = mock(Bus.class);

        when(mMockBusRoutes.get(3)).thenReturn(mockBus1);
        when(mMockBusRoutes.get(0)).thenReturn(mockBus2);

        assertEquals(mockBus1, mBusAdapter.getItemAtPosition(3));
        assertEquals(mockBus2, mBusAdapter.getItemAtPosition(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItemAtPosition_throwsException() {
        mBusAdapter.setBuses(null);
        mBusAdapter.getItemAtPosition(4);
    }

    @Test
    public void setBuses() {
        Bus mockBus1 = mock(Bus.class);
        Bus mockBus2 = mock(Bus.class);

        List<Bus> busList = new ArrayList<>();
        busList.add(mockBus1);
        busList.add(mockBus2);

        mBusAdapter.setBuses(busList);

        assertEquals(2, mBusAdapter.getItemCount());
        assertEquals(mockBus1, mBusAdapter.getItemAtPosition(0));
        assertEquals(mockBus2, mBusAdapter.getItemAtPosition(1));
    }

    @Test
    public void setBuses_nullList() {
        mBusAdapter.setBuses(null);

        assertEquals(0, mBusAdapter.getItemCount());
    }
}