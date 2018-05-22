package com.example.popey.restaurantapp;

public class Restaurant {

    //Stings
    private String RestaurantNewsTitle;
    private String RestaurantNewsCategory;
    private String RestaurantNewsDate;
    private String RestaurantNewsUrl;
    private String RestaurantNewsAuthor;

    /**
     * Constructs a new Restaurant news object.
     *
     * @param title    is news title
     * @param category is the section of the news
     * @param author   is the author of the news
     * @param date     is news date publishing
     * @param url      is news URL
     */

    public Restaurant(String title, String category, String author, String date, String url) {
        RestaurantNewsTitle = title;
        RestaurantNewsCategory = category;
        RestaurantNewsDate = date;
        RestaurantNewsUrl = url;
        RestaurantNewsAuthor = author;
    }

    /**
     * Returns the news title.
     */
    public String getNewsTitle() {
        return RestaurantNewsTitle;
    }

    /**
     * Returns the news category.
     */
    public String getNewsCategory() {
        return RestaurantNewsCategory;
    }

    /**
     * Returns publishing date.
     */
    public String getNewsDate() {
        return RestaurantNewsDate;
    }

    /**
     * Returns the news URL.
     */
    public String getUrl() {
        return RestaurantNewsUrl;
    }

    /**
     * Returns the news author.
     */

    public String getAuthor() {
        return RestaurantNewsAuthor;
    }

}