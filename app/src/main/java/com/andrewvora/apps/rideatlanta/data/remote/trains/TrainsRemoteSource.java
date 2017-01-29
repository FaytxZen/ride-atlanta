package com.andrewvora.apps.rideatlanta.data.remote.trains;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.BuildConfig;
import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.TrainsDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Train;
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
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class TrainsRemoteSource implements TrainsDataSource {

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

    }

    @Override
    public void saveTrain(@NonNull Train route) {

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
        getRequestQueue().add(request);
    }

    private static class ParseTrainsJsonTask extends AsyncTask<JSONArray, Void, List<Train>> {

        @NonNull
        private GetTrainRoutesCallback callbackRef;

        ParseTrainsJsonTask(@NonNull GetTrainRoutesCallback routesCallback) {
            callbackRef = routesCallback;
        }

        @Override
        protected List<Train> doInBackground(JSONArray... jsonArrays) {
            final List<Train> trainList = new ArrayList<>();
            final JSONArray response = jsonArrays[0];

            for(int i = 0; i < response.length(); i++) {
                try {
                    String jsonStr = response.getJSONObject(i).toString();
                    Train train = new Gson().fromJson(jsonStr, Train.class);
                    trainList.add(train);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return trainList;
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
}
