package com.andrewvora.apps.rideatlanta.buses;

import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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

    @Mock private BusRoutesContract.View mBusRoutesView;
    @Mock private Bundle mStateBundle;
    @Mock private BusesDataSource mBusSource;
    @Mock private FavoriteRoutesDataSource mFavsSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mBusRoutesPresenter = new BusRoutesPresenter(mBusRoutesView, mBusSource, mFavsSource);

        when(mBusSource.hasCachedData()).thenReturn(false);

        doNothing().when(mBusSource).reloadBuses();
        doNothing().when(mBusSource).saveBus(any(Bus.class));
        doNothing().when(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
        doNothing().when(mFavsSource).deleteRoute(any(FavoriteRouteDataObject.class));
        doNothing().when(mFavsSource).saveRoute(any(FavoriteRoute.class));
        doNothing().when(mBusRoutesView).subscribeReceiver(any(BroadcastReceiver.class));
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
    public void loadBusRoutes_noCachedData() throws Exception {
        mBusRoutesPresenter.loadBusRoutes();

        verify(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
        verify(mBusSource).reloadBuses();
    }

    @Test
    public void loadBusRoutes_hasCachedData() throws Exception {
        when(mBusSource.hasCachedData()).thenReturn(true);

        mBusRoutesPresenter.loadBusRoutes();

        verify(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
        verify(mBusSource, never()).reloadBuses();
    }

    @Test
    public void refreshBusRoutes() throws Exception {
        mBusRoutesPresenter.refreshBusRoutes();

        verify(mBusSource).reloadBuses();
        verify(mBusSource).getBuses(any(BusesDataSource.GetBusesCallback.class));
    }

    @Test
    public void favoriteRoute() throws Exception {
        Bus bus = new Bus();

        mBusRoutesPresenter.start();
        mBusRoutesPresenter.favoriteRoute(bus);

        assertTrue(bus.isFavorited());

        verify(mBusSource).saveBus(bus);
        verify(mFavsSource).reloadRoutes();
        verify(mFavsSource).saveRoute(any(FavoriteRoute.class));
    }

    @Test
    public void favoriteRoute_alreadyFavorited() throws Exception {
        Bus bus = new Bus();
        bus.setFavorited(true);

        mBusRoutesPresenter.favoriteRoute(bus);

        assertFalse(bus.isFavorited());

        verify(mBusSource).saveBus(bus);
        verify(mFavsSource).reloadRoutes();
        verify(mFavsSource).deleteRoute(any(FavoriteRoute.class));
    }
}