package cs591e1_sp19.eatogether;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RatingModel {

    String username, useravatar, rating, rating_amount, review;
    ArrayList<RatingModel> reviewers;

    public RatingModel(String username, String useravatar, String rating, String rating_amount){
        this.username = username;
        this.useravatar = useravatar;
        this.rating = rating;
        this.rating_amount = rating_amount;
    }

    public RatingModel(String username, String useravatar, String rating, String rating_amount, String review){
        this.username = username;
        this.useravatar = useravatar;
        this.rating = rating;
        this.rating_amount = rating_amount;
        this.review = review;
    }

    public RatingModel(String username, String useravatar, String rating, String rating_amount, ArrayList<RatingModel> reviewers){
        this.username = username;
        this.useravatar = useravatar;
        this.rating = rating;
        this.rating_amount = rating_amount;
        this.reviewers = reviewers;
    }

    RatingModel(){}

}
