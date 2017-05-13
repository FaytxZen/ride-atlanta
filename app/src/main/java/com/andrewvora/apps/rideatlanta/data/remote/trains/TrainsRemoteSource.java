package com.andrewvora.apps.rideatlanta.data.remote.trains;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.BuildConfig;
import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;
import com.andrewvora.apps.rideatlanta.data.repos.TrainsRepo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsRemoteSource implements TrainsDataSource {

    private static final String GET_TRAINS = "getTrainsRequest";

    private Context mContext;
    private RequestQueue mRequestQueue;
    private String mBaseUrl;
    private String mApiKey;

    private TrainsRemoteSource(@NonNull Context context) {
        mContext = context;
        mBaseUrl = context.getString(R.string.base_url_trains);
        mApiKey = BuildConfig.MARTA_API_KEY;
    }

    public static synchronized TrainsRemoteSource getInstance(Context context) {
        return new TrainsRemoteSource(context);
    }

    @Override
    public void getTrains(@NonNull final GetTrainRoutesCallback callback) {
        String requestUrl = String.format("%s?apikey=%s", mBaseUrl, mApiKey);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET,
                requestUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new ParseTrainsJsonTask(callback).execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        arrayRequest.setTag(GET_TRAINS);
        addToRequestQueue(arrayRequest);
    }

    @Override
    public void getTrains(@NonNull GetTrainRoutesCallback callback, @NonNull Long... trainIds) {

    }

    @Override
    public void getTrain(@NonNull Train train, @NonNull GetTrainRouteCallback callback) {

    }

    @Override
    public void deleteAllTrains(@Nullable DeleteTrainRoutesCallback callback) {
        // left-blank, remote is read-only
    }

    @Override
    public void saveTrain(@NonNull Train route) {
        // left-blank, remote is read-only
    }

    @Override
    public void getTrains(@NonNull String station, @NonNull String line, @NonNull GetTrainRoutesCallback callback) {
        // not used if class does not use cache
    }

    @Override
    public void reloadTrains() {
        // refreshing handled in the TrainsRepo
    }

    private RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> request) {
        RequestQueue requestQueue = getRequestQueue();
        requestQueue.cancelAll(request.getTag());
        requestQueue.add(request);
    }

    @Override
    public boolean hasCachedData() {
        return false;
    }

    private static class ParseTrainsJsonTask extends AsyncTask<JSONArray, Void, List<Train>> {
        @NonNull
        private GetTrainRoutesCallback callbackRef;

        ParseTrainsJsonTask(@NonNull GetTrainRoutesCallback routesCallback) {
            callbackRef = routesCallback;
        }

        @Override
        protected List<Train> doInBackground(JSONArray... jsonArrays) {
            final SortedMap<String, Train> trainMap = new TreeMap<>(new TrainsComparator());
            final JSONArray response = jsonArrays[0];

            for(int i = 0; i < response.length(); i++) {
                try {
                    String jsonStr = response.getJSONObject(i).toString();
                    Train train = new Gson().fromJson(jsonStr, Train.class);

                    String key = TrainsRepo.getKeyFor(train);
                    trainMap.put(key, train);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return new ArrayList<>(trainMap.values());
        }

        @Override
        protected void onPostExecute(List<Train> trains) {
            callbackRef.onFinished(trains);
        }
    }

    private static class ParseSingleTrainJsonTask extends AsyncTask<JSONObject, Void, Train> {

        @NonNull
        private GetTrainRouteCallback callbackRef;

        public ParseSingleTrainJsonTask(@NonNull GetTrainRouteCallback callback) {
            callbackRef = callback;
        }

        @Override
        protected Train doInBackground(JSONObject... objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Train train) {
            callbackRef.onFinished(train);
        }
    }

    private static class TrainsComparator implements Comparator<String> {
        @Override
        public int compare(String t1, String t2) {
            String[] tokens1 = t1.split(TrainsRepo.KEY_DELIMITER);
            String[] tokens2 = t2.split(TrainsRepo.KEY_DELIMITER);

            if(tokens1[1].compareTo(tokens2[1]) != 0) {
                return tokens1[1].compareTo(tokens2[1]);
            }
            else if(tokens1[2].compareTo(tokens2[2]) != 0) {
                return tokens1[2].compareTo(tokens2[2]);
            }
            else {
                return tokens1[0].compareTo(tokens2[0]);
            }
        }
    }
}
