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

    @Mock private List<FavoriteRouteDataObject> routesList;
    @Mock private FavoriteRoutesFragment.AdapterCallback adapterCallback;
    private FavoriteRoutesAdapter adapter;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        adapter = new FavoriteRoutesAdapter(routesList, adapterCallback);
    }

    @Test
    public void setFavoriteRoutes() {
        List<FavoriteRouteDataObject> favRouteList = new ArrayList<>();
        adapter.setFavoriteRoutes(favRouteList);

        assertEquals(favRouteList, adapter.getFavoriteRoutes());
    }

    @Test
    public void setFavoriteRoute() {
        FavoriteRoute favoriteRoute = new FavoriteRoute();
        favoriteRoute.setType(FavoriteRouteDataObject.TYPE_BUS);
        favoriteRoute.setRouteId("1234");

        adapter.setFavoriteRoute(0, favoriteRoute);
    }

    @Test
    public void getItemCount() {
        // non-empty list
        List<FavoriteRouteDataObject> routes = new ArrayList<>();
        routes.add(new FavoriteRoute());

        adapter.setFavoriteRoutes(routes);

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void getItemCount_empty() {
        assertEquals(0, adapter.getItemCount());
    }

}