package com.andrewvora.apps.rideatlanta.buses;

import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link BusRoutesPresenter}.
 *
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class BusRoutesPresenterTest extends BaseUnitTest {

    private BusRoutesContract.Presenter mBusRoutesPresenter;
    private CachedDataMap mCachedDataMap;

    @Mock private BusRoutesContract.View mBusRoutesView;
    @Mock private Bundle mStateBundle;
    @Mock private BusesDataSource mBusSource;
    @Mock private FavoriteRoutesDataSource mFavsSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mBusRoutesPresenter = new BusRoutesPresenter(mBusRoutesView);

        mCachedDataMap = CachedDataMap.getInstance();
        mCachedDataMap.put(BusRoutesPresenter.TAG, false);

        doNothing().when(mBusSource).reloadBuses();
        doNothing().when(mBusSource).saveBus(any(Bus.class));
        doNothing().when(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
        doNothing().when(mFavsSource).deleteRoute(any(FavoriteRouteDataObject.class));
        doNothing().when(mFavsSource).saveRoute(any(FavoriteRoute.class));
        doNothing().when(mBusRoutesView).subscribeReceiver(any(BroadcastReceiver.class));

        when(mBusRoutesView.getBusesDataSource()).thenReturn(mBusSource);
        when(mBusRoutesView.getFavRoutesDataSource()).thenReturn(mFavsSource);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        mCachedDataMap.clear();
    }

    @Test
    public void onSaveState() throws Exception {
        mBusRoutesPresenter.onSaveState(mStateBundle);
    }

    @Test
    public void onRestoreState() throws Exception {
        mBusRoutesPresenter.onRestoreState(mStateBundle);
    }

    @Test
    public void start() throws Exception {
        mBusRoutesPresenter.start();

        verify(mBusRoutesView).subscribeReceiver(any(BroadcastReceiver.class));
        verify(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
    }

    @Test
    public void stop() throws Exception {
        mBusRoutesPresenter.stop();

        verify(mBusRoutesView).unsubscribeReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void loadBusRoutes() throws Exception {
        mBusRoutesPresenter.start();

        verify(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
        verify(mBusSource).reloadBuses();
    }

    @Test
    public void loadBusRoutes_hasCachedData() throws Exception {
        mCachedDataMap.put(BusRoutesPresenter.TAG, true);

        mBusRoutesPresenter.start();

        verify(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
        verify(mBusSource, never()).reloadBuses();
    }

    @Test
    public void refreshBusRoutes() throws Exception {
        mBusRoutesPresenter.start();
        mBusRoutesPresenter.refreshBusRoutes();

        verify(mBusSource, times(2)).reloadBuses();
        verify(mBusSource, times(2)).getBuses(any(BusesDataSource.GetBusesCallback.class));
    }

    @Test
    public void favoriteRoute() throws Exception {
        Bus bus = new Bus();

        mBusRoutesPresenter.start();
        mBusRoutesPresenter.favoriteRoute(bus);

        assertTrue(bus.isFavorited());
        verify(mFavsSource).saveRoute(any(FavoriteRoute.class));
    }

    @Test
    public void favoriteRoute_alreadyFavorited() throws Exception {
        Bus bus = new Bus();
        bus.setFavorited(true);

        mBusRoutesPresenter.start();
        mBusRoutesPresenter.favoriteRoute(bus);

        assertFalse(bus.isFavorited());
        verify(mFavsSource).deleteRoute(any(FavoriteRoute.class));
    }
}