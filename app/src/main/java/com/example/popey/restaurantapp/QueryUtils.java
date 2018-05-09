package com.example.popey.restaurantapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    /**
 * Tag for the log messages
 */
private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Keys used for the JSON response
    private static final String response = "response";

    private static final String results = "results";

    private static final String section = "sectionName";

    private static final String date = "webPublicationDate";

    private static final String title = "webTitle";

    private static final String url = "webUrl";

    private static final String tags = "tags";

    private static final String author = "webTitle";

    /**
     * /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Restaurant} objects.
     */

    public static List<Restaurant> fetchRestaurantData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;


        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Restaurant}s
        List<Restaurant> restaurants = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Restaurant}s
        return restaurants;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Restaurant} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Restaurant> extractFeatureFromJson(String restaurantJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(restaurantJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding restaurants to
        List<Restaurant>restaurants = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(restaurantJSON);

            // Extract the JSONObject associated with the key called "response",

            JSONObject responseJson = baseJsonResponse.getJSONObject(response);

            // Extract the JSONArray associated with the key called "results"
            JSONArray restaurantArray = responseJson.getJSONArray(results);

            // For each restaurant in the restaurantArray, create an {@link Restaurant} object
            for (int i = 0; i < restaurantArray.length(); i++) {

                // Get a single restaurant at position i within the list of restaurants
                JSONObject currentRestaurant = restaurantArray.getJSONObject(i);

                // Extract the section name for the key called "sectionName"
                String newsSection = currentRestaurant.getString(section);

                // Check if newsDate exist and than extract the date for the key called "webPublicationDate"
                String newsDate = "N/A";

                if (currentRestaurant.has(date)) {
                    newsDate = currentRestaurant.getString(date);
                }

                // Extract the article name for the key called "webTitle"
                String newsTitle = currentRestaurant.getString(title);

                // Extract the value for the key called "webUrl"
                String newsUrl = currentRestaurant.getString(url);

                //Extract the JSONArray associated with the key called "tags",
                JSONArray currentNewsAuthorArray = currentRestaurant.getJSONArray(tags);

                String newsAuthor = "N/A";

                //Check if "tags" array contains data
                int tagsLenght = currentNewsAuthorArray.length();


                if (tagsLenght == 1) {
                    // Create a JSONObject for author
                    JSONObject currentNewsAuthor = currentNewsAuthorArray.getJSONObject(0);

                    String newsAuthor1 = currentNewsAuthor.getString(author);

                    newsAuthor = "written by: " + newsAuthor1;
    }
                // Create a new News object with the title, category, author, date, url ,
                // from the JSON response.
                Restaurant restaurant = new Restaurant(newsTitle, newsSection, newsAuthor, newsDate, newsUrl);

                // Add the new {@link Restaurant} to the list of restaurants.
                restaurants.add(restaurant);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NewsUtils", "JSON results parsing problem.");
        }

        // Return the list of restaurants
        return restaurants;

}
    }

