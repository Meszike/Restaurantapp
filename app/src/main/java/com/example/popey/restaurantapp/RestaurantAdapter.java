package com.example.popey.restaurantapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    /**
     * Constructs a new {@link RestaurantAdapter}.
     *
     * @param context    of the app
     * @param restaurant is the list of restaurants, which is the data source of the adapter
     */
    public RestaurantAdapter(Context context, List<Restaurant> restaurant) {
        super(context, 0, restaurant);
    }

    /**
     * Returns a list item view that displays information about the restaurant at the given position
     * in the list of restaurants.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.restaurant_list_item, parent, false);
        }

        // Find the restaurant news at the given position in the list of restaurant news
        Restaurant currentRestaurantClass = getItem(position);

        // Find the TextView with view ID title
        TextView titleTextView = listItemView.findViewById(R.id.title);

        // Display the title of the current news in that TextView
        assert currentRestaurantClass != null;
        titleTextView.setText(currentRestaurantClass.getNewsTitle());

        // Find the TextView with Author
        TextView restaurantAuthorTextView = listItemView.findViewById(R.id.newsAuthor);

        // Display the author of the current news in that TextView
        restaurantAuthorTextView.setText(currentRestaurantClass.getAuthor());

        // Find the TextView with News Category
        TextView restaurantCategoryTextView = listItemView.findViewById(R.id.category);

        // Display the category of the current news in that TextView
        restaurantCategoryTextView.setText(currentRestaurantClass.getNewsCategory());

        // Find the TextView with News Date with the ID category
        TextView newsDateTextView = listItemView.findViewById(R.id.date);

        // Display the category of the current news in that TextView
        SimpleDateFormat dateFormatJSON = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("EE dd MMM yyyy", Locale.ENGLISH);

        try {
            Date dateNews = dateFormatJSON.parse(currentRestaurantClass.getNewsDate());

            String date = dateFormat2.format(dateNews);
            newsDateTextView.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the whole list item layout

        return listItemView;
    }
}
