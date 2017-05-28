package com.andrewvora.apps.rideatlanta.trains;

import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.favoriteroutes.FavoriteRoutesContract;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TrainRoutesPresenter}.
 *
 * Created by faytx on 4/2/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class TrainRoutesPresenterTest extends BaseUnitTest {

    @Mock FavoriteRoutesDataSource mFavRoutesRepo;
    @Mock FavoriteRoutesContract.LoadingCache mFavRoutesCache;
    @Mock TrainsDataSource mTrainsRepo;
    @Mock TrainRoutesContract.View mView;

    private TrainRoutesContract.Presenter mPresenter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mPresenter = new TrainRoutesPresenter(mView, mTrainsRepo, mFavRoutesRepo, mFavRoutesCache);
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

        verify(mView).subscribeReceiver(any(BroadcastReceiver.class));
        verify(mTrainsRepo).getTrains(any(TrainsDataSource.GetTrainRoutesCallback.class));

        verify(mFavRoutesCache).setListener(any(FavoriteRoutesContract.DataLoadedListener.class));
        verify(mFavRoutesCache).loadFavoriteRoutes();
    }

    @Test
    public void stop() throws Exception {
        mPresenter.stop();

        verify(mView).unsubscribeReceiver(any(BroadcastReceiver.class));
        verify(mFavRoutesCache).setListener(null);
    }

    @Test
    public void loadTrainRoutes() throws Exception {
        mPresenter.loadTrainRoutes();

        verify(mTrainsRepo).reloadTrains();
        verify(mTrainsRepo).getTrains(any(TrainsDataSource.GetTrainRoutesCallback.class));
    }

    @Test
    public void loadTrainRoutes_hasCachedData() throws Exception {
        when(mTrainsRepo.hasCachedData()).thenReturn(true);

        mPresenter.loadTrainRoutes();

        verify(mTrainsRepo, never()).reloadTrains();
        verify(mTrainsRepo).getTrains(any(TrainsDataSource.GetTrainRoutesCallback.class));
    }

    @Test
    public void refreshTrainRoutes() throws Exception {
        mPresenter.refreshTrainRoutes();

        verify(mTrainsRepo).reloadTrains();
        verify(mTrainsRepo).getTrains(any(TrainsDataSource.GetTrainRoutesCallback.class));
    }

    @Test
    public void favoriteRoute_favoriting() throws Exception {
        Train train = new Train();
        train.setTrainId(1L);

        mPresenter.favoriteRoute(train);

        verify(mTrainsRepo).saveTrain(train);
        verify(mFavRoutesRepo).reloadRoutes();

        verify(mFavRoutesRepo, never()).deleteRoute(any(FavoriteRoute.class));
        verify(mFavRoutesRepo).saveRoute(any(FavoriteRoute.class));
        verify(mFavRoutesCache).setFavoritedRoutes(anyListOf(FavoriteRouteDataObject.class));
    }

    @Test
    public void favoriteRoute_alreadyFavorited() throws Exception {
        Train train = new Train();
        train.setTrainId(1L);
        train.setFavorited(true);

        mPresenter.favoriteRoute(train);

        verify(mTrainsRepo).saveTrain(train);
        verify(mFavRoutesRepo).reloadRoutes();
        verify(mFavRoutesRepo).deleteRoute(any(FavoriteRoute.class));
        verify(mFavRoutesRepo, never()).saveRoute(any(FavoriteRoute.class));
        verify(mFavRoutesCache).setFavoritedRoutes(anyListOf(FavoriteRouteDataObject.class));
    }
}