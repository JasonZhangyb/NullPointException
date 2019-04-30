package cs591e1_sp19.eatogether;

import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.Coordinates;

import java.util.ArrayList;

public class MapModel {

    public String res_name, res_price, res_img, res_id;
    public Double res_rating;
    public ArrayList<Category> type;
    public Coordinates location;
    public String res_rating_str, type_str;

    public MapModel(String res_name, String res_price, Double res_rating,
                    ArrayList<Category> type, Coordinates location){

        this.res_name = res_name;
        this.res_price = res_price;
        this.res_rating = res_rating;
        this.type = type;
        this.location = location;
    }

    public MapModel(String res_name, String res_img, String res_id, String res_rating, String type){
        this.res_name = res_name;
        this.res_img = res_img;
        this.res_id = res_id;
        this.res_rating_str = res_rating;
        this.type_str = type;
    }

    public MapModel(){}

}
