package com.andrewvora.apps.rideatlanta.data.remote.marta;

import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.models.Train;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created on 8/7/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public interface MartaService {

    String SERVICE_URL = "http://developer.itsmarta.com";

    @GET("/RealtimeTrain/RestServiceNextTrain/GetRealtimeArrivals")
    Observable<List<Train>> getTrains(@Query("apikey") String apiKey);

    @GET("/BRDRestService/RestBusRealTimeService/GetAllBus")
    Observable<List<Bus>> getBuses();

}
