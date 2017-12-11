package com.andrewvora.apps.rideatlanta.data.remote.trains;

import android.app.Application;
import android.support.annotation.NonNull;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.RideAtlantaApplication;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.remote.marta.MartaService;
import com.andrewvora.apps.rideatlanta.utils.InputStreamConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */
public class TrainsRemoteSource implements TrainsDataSource {

    private String apiKey;
	private MartaService martaService;
	private Application app;
	private Gson gson;
	private InputStreamConverter converter;

    public TrainsRemoteSource(@NonNull Application app,
                              @NonNull String apiKey,
                              @NonNull MartaService service,
                              @NonNull Gson gson,
                              @NonNull InputStreamConverter converter)
    {
        this.apiKey = apiKey;
		this.martaService = service;
		this.app = app;
		this.gson = gson;
		this.converter = converter;
    }

    @Override
    public Observable<List<Train>> getTrains() {
        if (RideAtlantaApplication.USE_LOCAL) {
            final InputStream trainsFileStream = app.getResources().openRawResource(R.raw.trains);
            final String trainsJsonStr = converter.getString(trainsFileStream);
            final List<Train> trains = gson.fromJson(trainsJsonStr, new TypeToken<List<Train>>(){}.getType());
            Collections.sort(trains, new TrainsComparator());
            return Observable.just(trains);
        }
        else return martaService.getTrains(apiKey).map(new Function<List<Train>, List<Train>>() {
            @Override
            public List<Train> apply(@io.reactivex.annotations.NonNull List<Train> trains) throws Exception {
                Collections.sort(trains, new TrainsComparator());
                return trains;
            }
        });
    }

    @Override
    public Observable<List<Train>> getFreshTrains() {
        return getTrains();
    }

    @Override
    public Observable<List<Train>> getTrains(@NonNull final Long... trainIds) {
        return martaService.getTrains(apiKey)
                .flatMap(new Function<List<Train>, ObservableSource<Train>>() {
                    @Override
                    public ObservableSource<Train> apply(@io.reactivex.annotations.NonNull List<Train> trains) throws Exception {
                        return Observable.fromIterable(trains);
                    }
                })
                .filter(new Predicate<Train>() {
                    @Override
                    public boolean test(@io.reactivex.annotations.NonNull Train train) throws Exception {
                        for (Long trainId : trainIds) {
                            if (trainId.equals(train.getTrainId())) {
                                return true;
                            }
                        }

                        return false;
                    }
                })
                .toList()
                .toObservable()
                .map(new Function<List<Train>, List<Train>>() {
                    @Override
                    public List<Train> apply(@io.reactivex.annotations.NonNull List<Train> trains) throws Exception {
                        Collections.sort(trains, new TrainsComparator());
                        return trains;
                    }
                });
    }

    @Override
    public Observable<Train> getTrain(@NonNull Train train) {
        return Observable.just(new Train());
    }

    @Override
    public Observable<Long> deleteAllTrains() {
        return Observable.empty();
    }

    @Override
    public Observable<List<Train>> getTrains(@NonNull String station, @NonNull String line) {
        return Observable.empty();
    }

    @Override
    public Observable<Long> saveTrain(@NonNull Train route) {
        return Observable.empty();
    }

    @Override
    public void reloadTrains() {
        // refreshing handled in the TrainsRepo
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    private static class TrainsComparator implements Comparator<Train> {
        @Override
        public int compare(Train t1, Train t2) {
            if(t1.getLine().compareTo(t2.getLine()) != 0) {
                return t1.getLine().compareTo(t2.getLine());
            }
            else if(t1.getStation().compareTo(t2.getStation()) != 0) {
                return t1.getStation().compareTo(t2.getStation());
            }
            else {
                return t1.getTimeTilArrival().compareTo(t2.getTimeTilArrival());
            }
        }
    }
}
