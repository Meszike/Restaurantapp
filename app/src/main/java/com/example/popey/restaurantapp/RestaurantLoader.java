package com.example.popey.restaurantapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class RestaurantLoader extends AsyncTaskLoader<List<Restaurant>> {

    private static final String LOG_TAG = RestaurantLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link RestaurantLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public RestaurantLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Restaurant> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of restaurants.
        List<Restaurant> restaurant = QueryUtils.fetchRestaurantData(mUrl);
        return restaurant;
    }
}
