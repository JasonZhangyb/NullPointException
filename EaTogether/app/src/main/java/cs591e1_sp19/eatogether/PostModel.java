package cs591e1_sp19.eatogether;

public class PostModel {

    public String user;
    public String avatar;
    public String gender;
    public String country;
    public String language;
    public String time1;
    public String time2;
    public String note;


    public PostModel(String user, String avatar, String gender,
                     String country, String language, String time1,
                     String time2, String note){
        this.user = user;
        this.avatar = avatar;
        this.gender = gender;
        this.country = country;
        this.language = language;
        this.time1 = time1;
        this.time2 = time2;
        this.note = note;
    }

    public PostModel(){}

}

