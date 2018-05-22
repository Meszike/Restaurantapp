package com.example.popey.restaurantapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Restaurant>>, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = RestaurantActivity.class.getSimpleName();
    /**
     * URL for Restaurant news data from the GUARDIAN dataset
     */
    private static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search?show-fields=byline";

    /**
     * Constant value for the Restaurant news loader ID. We can choose any integer.
     */
    private static final int RESTAURANT_LOADER_ID = 1;

    /**
     * Adapter for the list of restaurant news
     */
    private RestaurantAdapter rAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView restaurantListView = findViewById(R.id.list);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        restaurantListView.setEmptyView(mEmptyStateTextView);

        // New adapter that takes an empty list of restaurants news as input
        rAdapter = new RestaurantAdapter(this, new ArrayList<Restaurant>());

        // Set the adapter on the {@link ListView}
        restaurantListView.setAdapter(rAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);


        // Click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected restaurant.
        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current Restaurant news that was clicked
                Restaurant currentRestaurant = rAdapter.getItem(position);

                // The String URL into a URI object (to pass into the Intent constructor)
                Uri restaurantUri = Uri.parse(currentRestaurant.getUrl());

                // New intent to view the Restaurant news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, restaurantUri);

                // The intent to launch a new activity
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
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        // Obtain a reference to the SharedPreferences file for this app

        if (key.equals(getString(R.string.settings_amount_key)) ||
                key.equals(getString(R.string.settings_order_by_key))) {
            // Clear the ListView as a new query will be kicked off
            rAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the new as the query settings have been updated
            getLoaderManager().restartLoader(RESTAURANT_LOADER_ID, null, this);
        }
    }


    @Override
    public Loader<List<Restaurant>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String articlesAmount = sharedPrefs.getString(
                getString(R.string.settings_amount_key),
                getString(R.string.settings_amount_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("section", "lifeandstyle");
        uriBuilder.appendQueryParameter("q", "restaurant||vegetarian");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", articlesAmount);
        uriBuilder.appendQueryParameter("api-key", "51d29186-f983-44eb-9972-8a289449db3e");

        // Return the completed uri
        return new RestaurantLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Restaurant>> loader, List<Restaurant> restaurants) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No restaurants found."
        mEmptyStateTextView.setText(R.string.no_restaurants);

        // Clear the adapter of previous Restaurant news data
        rAdapter.clear();

        // If there is a valid list of {@link Restaurant news}s, then add them to the adapter's
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

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
