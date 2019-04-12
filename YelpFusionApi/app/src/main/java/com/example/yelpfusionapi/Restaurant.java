package com.example.yelpfusionapi;

public class Restaurant {
    private String Name;
    private String ImageUrl;

    public Restaurant(String name, String imageUrl) {
        Name = name;
        ImageUrl = imageUrl;
    }

    public Restaurant(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }


}
