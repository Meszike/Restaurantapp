package com.example.popey.restaurantapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Restaurant>> {


    /**
     * URL for Restaurant data from the GUARDIAN dataset
     */
    private static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search?q=the_best||restaurants&api-key=51d29186-f983-44eb-9972-8a289449db3e";

    /**
     * Constant value for the restaurant loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int RESTAURANT_LOADER_ID = 1;

    /**
     * Adapter for the list of restaurants
     */
    private RestaurantAdapter rAdapter;
    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);
        // Find a reference to the {@link ListView} in the layout
        ListView restaurantListView = findViewById(R.id.list);

        mEmptyStateTextView =  findViewById(R.id.empty_view);

        restaurantListView.setEmptyView(mEmptyStateTextView);



        // Create a new adapter that takes an empty list of restaurants as input
        rAdapter = new RestaurantAdapter(this, new ArrayList<Restaurant>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        restaurantListView.setAdapter(rAdapter);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected restaurant.
        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Restaurant that was clicked on
                Restaurant currentRestaurant = rAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri restaurantUri = Uri.parse(currentRestaurant.getUrl());

                // Create a new intent to view the Restaurant URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, restaurantUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(RESTAURANT_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }


    @Override
    public Loader<List<Restaurant>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new RestaurantLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Restaurant>> loader, List<Restaurant> restaurants) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No restaurants found."
        mEmptyStateTextView.setText(R.string.no_restaurants);

        // Clear the adapter of previous restaurant data
        rAdapter.clear();

        // If there is a valid list of {@link Restaurant}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (restaurants != null && !restaurants.isEmpty()) {
            rAdapter.addAll(restaurants);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<Restaurant>> loader) {
        // Loader reset, so we can clear out our existing data.
        rAdapter.clear();
    }

}