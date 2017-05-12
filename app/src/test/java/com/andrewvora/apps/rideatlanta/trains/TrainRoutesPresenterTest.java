package com.andrewvora.apps.rideatlanta.trains;

import android.content.BroadcastReceiver;
import android.os.Bundle;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.CachedDataMap;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRoutesDataSource;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
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
    @Mock TrainsDataSource mTrainsRepo;
    @Mock TrainRoutesContract.View mView;

    private CachedDataMap mCachedDataMap;
    private TrainRoutesContract.Presenter mPresenter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mCachedDataMap = CachedDataMap.getInstance();
        mCachedDataMap.clear();

        mPresenter = new TrainRoutesPresenter(mView, mTrainsRepo, mFavRoutesRepo);
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
    }

    @Test
    public void stop() throws Exception {
        mPresenter.stop();
    }

    @Test
    public void loadTrainRoutes() throws Exception {
        mPresenter.loadTrainRoutes();

        verify(mTrainsRepo).reloadTrains();
        verify(mTrainsRepo).getTrains(any(TrainsDataSource.GetTrainRoutesCallback.class));
    }

    @Test
    public void loadTrainRoutes_hasCachedData() throws Exception {
        mCachedDataMap.put(TrainRoutesPresenter.getCachedDataTag(), true);

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
        Train train = mock(Train.class);

        mPresenter.favoriteRoute(train);

        verify(mTrainsRepo).saveTrain(train);
        verify(mFavRoutesRepo).reloadRoutes();
        verify(mFavRoutesRepo).deleteRoute(any(FavoriteRoute.class));
    }

    @Test
    public void favoriteRoute_alreadyFavorited() throws Exception {
        Train train = mock(Train.class);
        when(train.isFavorited()).thenReturn(true);

        mPresenter.favoriteRoute(train);

        verify(mTrainsRepo).saveTrain(train);
        verify(mFavRoutesRepo).reloadRoutes();
        verify(mFavRoutesRepo).saveRoute(any(FavoriteRoute.class));
    }
}