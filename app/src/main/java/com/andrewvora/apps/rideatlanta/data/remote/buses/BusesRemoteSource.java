package com.andrewvora.apps.rideatlanta.data.remote.buses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewvora.apps.rideatlanta.R;
import com.andrewvora.apps.rideatlanta.data.models.Bus;
import com.andrewvora.apps.rideatlanta.data.contracts.BusesDataSource;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faytx on 10/22/2016.
 * @author Andrew Vorakrajangthiti
 */

public class BusesRemoteSource implements BusesDataSource {

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
                        List<Bus> buses = new ArrayList<>();

                        for(int i = 0; i < response.length(); i++) {
                            try {
                                String jsonStr = response.getJSONObject(i).toString();
                                Bus bus = new Gson().fromJson(jsonStr, Bus.class);
                                buses.add(bus);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        callback.onFinished(buses);
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
    public void getBuses(@NonNull GetBusesCallback callback, @NonNull String... routeIds) {

    }

    @Override
    public void getBus(@NonNull String routeId, @NonNull GetBusCallback callback) {

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
        getRequestQueue().add(request);
    }
}
