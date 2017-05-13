package com.andrewvora.apps.rideatlanta.favoriteroutes;

import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesPresenterTest extends BaseUnitTest {

    @Mock FavoriteRoutesDataSource mFavRouteRepo;
    @Mock TrainsDataSource mTrainRepo;
    @Mock BusesDataSource mBusRepo;
    @Mock FavoriteRoutesContract.View mView;
    private FavoriteRoutesContract.Presenter mPresenter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mPresenter = new FavoriteRoutesPresenter(mView, mFavRouteRepo, mBusRepo, mTrainRepo);
    }

    @Test
    public void onSaveState() throws Exception {
        mPresenter.onSaveState(mock(Bundle.class));
    }

    @Test
    public void onRestoreState() throws Exception {
        mPresenter.onRestoreState(mock(Bundle.class));
    }

    @Test
    public void start() throws Exception {
        mPresenter.start();

        verify(mFavRouteRepo).getFavoriteRoutes(any(FavoriteRoutesDataSource.GetFavoriteRoutesCallback.class));
        verify(mView).subscribeReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void stop() throws Exception {
        mPresenter.stop();

        verify(mView).unsubscribeReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void loadFavoriteRoutes() throws Exception {
        mPresenter.loadFavoriteRoutes();

        Class<FavoriteRoutesDataSource.GetFavoriteRoutesCallback> callbackClass =
                FavoriteRoutesDataSource.GetFavoriteRoutesCallback.class;

        verify(mFavRouteRepo).getFavoriteRoutes(any(callbackClass));
    }

    @Test
    public void loadRouteInformation() throws Exception {
        mPresenter.refreshRouteInformation();

        Class<FavoriteRoutesDataSource.GetFavoriteRoutesCallback> callbackClass =
                FavoriteRoutesDataSource.GetFavoriteRoutesCallback.class;

        verify(mFavRouteRepo).getFavoriteRoutes(any(callbackClass));
    }
}