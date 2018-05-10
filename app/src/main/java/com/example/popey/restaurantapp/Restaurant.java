package com.example.popey.restaurantapp;

public class Restaurant {

    // News title
    private String newsTitle;

    // News section
    private String newsCategory;

    // Date of publishing
    private String newsDate;

    // News URL
    private String newsUrl;

    /**
     * Constructs a new EducationNews object.
     *
     * @param title    is news title
     * @param category is the section of the news
     * @param date     is news date publishing
     * @param url      is news URL
     */

    public Restaurant(String title, String category, String date, String url) {
        newsTitle = title;
        newsCategory = category;
        newsDate = date;
        newsUrl = url;
    }

    /**
     * Returns the news title.
     */
    public String getNewsTitle() {
        return newsTitle;
    }

    /**
     * Returns the news category.
     */
    public String getNewsCategory() {
        return newsCategory;
    }

       /**
     * Returns publishing date.
     */
    public String getNewsDate() {
        return newsDate;
    }

    /**
     * Returns the news URL.
     */
    public String getUrl() {
        return newsUrl;
    }
}