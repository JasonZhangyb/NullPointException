package cs591e1_sp19.eatogether;

public class PostModel {

    public String user_id, user_name;
    public String avatar;
    public String time1, time2;
    public String date, month, year;
    public String note;
    public String restaurant_id, restaurant_name,post_id,restaurant_img;
    public String latitude, longitude;
    public String res_rating, res_type;


    public PostModel(String user_id, String user_name, String avatar, String date,
                     String month, String year, String time1,
                     String time2, String note, String restaurant_id,
                     String restaurant_name, String post_id, String restaurant_img,
                     String latitude, String longitude, String res_rating, String res_type){

        this.user_id = user_id;
        this.user_name = user_name;
        this.avatar = avatar;
        this.year = year;
        this.date = date;
        this.month = month;
        this.time1 = time1;
        this.time2 = time2;
        this.note = note;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.post_id = post_id;
        this.restaurant_img = restaurant_img;
        this.latitude = latitude;
        this.longitude = longitude;
        this.res_rating = res_rating;
        this.res_type = res_type;
    }

    public PostModel(String user_id, String user_name, String avatar,String restaurant_id,
                     String restaurant_name, String post_id, String time1, String time2,
                     String latitude, String longitude, String note){

        this.user_id = user_id;
        this.user_name = user_name;
        this.avatar = avatar;
        this.time1 = time1;
        this.time2 = time2;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.post_id = post_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;

    }

    public PostModel(){}

}

