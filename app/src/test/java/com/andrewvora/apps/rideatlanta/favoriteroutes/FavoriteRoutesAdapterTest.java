package com.andrewvora.apps.rideatlanta.favoriteroutes;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;
import com.andrewvora.apps.rideatlanta.data.contracts.FavoriteRouteDataObject;
import com.andrewvora.apps.rideatlanta.data.models.FavoriteRoute;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by faytx on 4/2/2017.
 * @author Andrew Vorakrajangthiti
 */
public class FavoriteRoutesAdapterTest extends BaseUnitTest {

    @Mock private List<FavoriteRouteDataObject> mRoutesList;
    @Mock private FavoriteRoutesFragment.AdapterCallback mAdapterCallback;
    private FavoriteRoutesAdapter mAdapter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mAdapter = new FavoriteRoutesAdapter(mRoutesList, mAdapterCallback);
    }

    @Test
    public void setFavoriteRoutes() throws Exception {
        List<FavoriteRouteDataObject> favRouteList = new ArrayList<>();
        mAdapter.setFavoriteRoutes(favRouteList);

        assertEquals(favRouteList, mAdapter.getFavoriteRoutes());
    }

    @Test
    public void setFavoriteRoute() throws Exception {
        FavoriteRoute favoriteRoute = new FavoriteRoute();
        favoriteRoute.setType(FavoriteRouteDataObject.TYPE_BUS);
        favoriteRoute.setRouteId("1234");

        mAdapter.setFavoriteRoute(favoriteRoute);
    }

    @Test
    public void getPosition() throws Exception {
        FavoriteRoute favoriteRoute = new FavoriteRoute();
        favoriteRoute.setType(FavoriteRouteDataObject.TYPE_BUS);
        favoriteRoute.setRouteId("1234");

        List<FavoriteRouteDataObject> routes = new ArrayList<>();
        routes.add(favoriteRoute);

        mAdapter.setFavoriteRoutes(routes);

        int routeAdapterPosition = mAdapter.getPosition(favoriteRoute);
        assertEquals(0, routeAdapterPosition);
    }

    @Test
    public void getPosition_itemNotYetAdded() throws Exception {
        FavoriteRoute favoriteRoute = new FavoriteRoute();
        favoriteRoute.setType(FavoriteRouteDataObject.TYPE_BUS);
        favoriteRoute.setRouteId("1234");

        int routeAdapterPosition = mAdapter.getPosition(favoriteRoute);
        assertEquals(FavoriteRoutesAdapter.NEW_INDEX, routeAdapterPosition);
    }

    @Test
    public void getItemCount() throws Exception {
        // non-empty list
        List<FavoriteRouteDataObject> routes = new ArrayList<>();
        routes.add(new FavoriteRoute());

        mAdapter.setFavoriteRoutes(routes);

        assertEquals(1, mAdapter.getItemCount());
    }

    @Test
    public void getItemCount_empty() throws Exception {
        assertEquals(0, mAdapter.getItemCount());
    }

}