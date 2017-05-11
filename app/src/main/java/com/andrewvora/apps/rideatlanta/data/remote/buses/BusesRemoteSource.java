package com.andrewvora.apps.rideatlanta.data.remote.buses;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
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
public class BusesRemoteSource implements BusesDataSource {

    private static final String GET_BUSES = "getAllBusesRequest";

    private Context mContext;
    private RequestQueue mRequestQueue;
    private String mBaseUrl;

    private BusesRemoteSource(@NonNull Context context) {
        mContext = context;
        mBaseUrl = context.getString(R.string.base_url_buses);
    }

    public static synchronized BusesRemoteSource getInstance(@NonNull Context context) {
        return new BusesRemoteSource(context);
    }

    @Override
    public void getBuses(@NonNull final GetBusesCallback callback) {
        String requestUrl = mBaseUrl;

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new ParseBusJsonTask(callback).execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        arrayRequest.setTag(GET_BUSES);
        addToRequestQueue(arrayRequest);
    }

    @Override
    public void getBuses(@NonNull GetBusesCallback callback, @NonNull String... routeIds) {

    }

    @Override
    public void getBus(@NonNull Bus bus, @NonNull GetBusCallback callback) {

    }

    @Override
    public void deleteAllBus(@Nullable DeleteBusesCallback callback) {

    }

    @Override
    public void saveBus(@NonNull Bus route) {

    }

    @Override
    public void reloadBuses() {

    }

    private RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }

        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> request) {
        RequestQueue requestQueue = getRequestQueue();
        requestQueue.cancelAll(request.getTag());
        requestQueue.add(request);
    }

    private static class BusComparator implements Comparator<String> {
        @Override
        public int compare(String t1, String t2) {
            return Integer.parseInt(t1) - Integer.parseInt(t2);
        }
    }

    private static class ParseBusJsonTask extends AsyncTask<JSONArray, Void, List<Bus>> {

        @NonNull
        private GetBusesCallback callbackRef;

        ParseBusJsonTask(@NonNull GetBusesCallback callback) {
            callbackRef = callback;
        }

        @Override
        protected List<Bus> doInBackground(JSONArray... jsonArrays) {
            final SortedMap<String, Bus> busMap = new TreeMap<>(new BusComparator());
            final JSONArray response = jsonArrays[0];

            for(int i = 0; i < response.length(); i++) {
                try {
                    String jsonStr = response.getJSONObject(i).toString();
                    Bus bus = new Gson().fromJson(jsonStr, Bus.class);
                    busMap.put(bus.getRouteId(), bus);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return new ArrayList<>(busMap.values());
        }

        @Override
        protected void onPostExecute(List<Bus> buses) {
            callbackRef.onFinished(buses);
        }
    }

    private static class ParseSingleBusJsonTask extends AsyncTask<JSONObject, Void, Bus> {

        private GetBusCallback callbackRef;

        ParseSingleBusJsonTask(@NonNull GetBusCallback callback) {
            callbackRef = callback;
        }

        @Override
        protected Bus doInBackground(JSONObject... jsonObjects) {
            return null;
        }

        @Override
        protected void onPostExecute(Bus bus) {
            callbackRef.onFinished(bus);
        }
    }
}
