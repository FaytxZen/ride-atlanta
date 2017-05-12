package com.andrewvora.apps.rideatlanta.trains;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link TrainRoutesAdapter}.
 *
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesAdapterTest extends BaseUnitTest {

    @Mock private List<Train> mTrainList;
    @Mock private TrainRoutesFragment.TrainItemListener mListener;
    private TrainRoutesAdapter mAdapter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mAdapter = new TrainRoutesAdapter(mTrainList, mListener);
    }

    @Test
    public void getItemCount() throws Exception {
        when(mTrainList.size()).thenReturn(2);

        assertEquals(2, mAdapter.getItemCount());
    }

    @Test
    public void getItemCount_empty() throws Exception {
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void setTrains() throws Exception {
        List<Train> newList = new ArrayList<>();

        mAdapter.setTrains(newList);

        assertEquals(newList, mAdapter.getTrains());
    }

    @Test
    public void getTrain() throws Exception {
        Train train = mock(Train.class);
        when(mTrainList.get(0)).thenReturn(train);

        assertEquals(train, mAdapter.getTrain(0));
    }
}