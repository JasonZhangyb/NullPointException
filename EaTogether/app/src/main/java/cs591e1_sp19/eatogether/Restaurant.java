package cs591e1_sp19.eatogether;

public class Restaurant {
    private String Name;
    private String ImageUrl, Rating, Type;

    public Restaurant(String name, String imageUrl, String rating, String type) {
        Name = name;
        ImageUrl = imageUrl;
        Rating = rating;
        Type = type;
    }

    public Restaurant(){}

    public String getName() {
        return Name;
    }

    public String getRating() {
        return Rating;
    }

    public String getType() {
        return Type;
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

