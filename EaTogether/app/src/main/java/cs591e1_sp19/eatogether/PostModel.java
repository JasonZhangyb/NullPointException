package cs591e1_sp19.eatogether;

public class PostModel {

    public String user_id, user_name;
    public String avatar;
    public String gender;
    public String country, language;
    public String time1, time2;
    public String note;
    public String restaurant_id, post_id;


    public PostModel(String user_id, String user_name, String avatar, String gender,
                     String country, String language, String time1,
                     String time2, String note, String restaurant_id,
                     String post_id){

        this.user_id = user_id;
        this.user_name = user_name;
        this.avatar = avatar;
        this.gender = gender;
        this.country = country;
        this.language = language;
        this.time1 = time1;
        this.time2 = time2;
        this.note = note;
        this.restaurant_id = restaurant_id;
        this.post_id = post_id;
    }

    public PostModel(){}

}

