package cs591e1_sp19.eatogether;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String uid;
    private String uname;
    private String avatar;
    private String email;
    public String gender, location, language;
    public HashMap<String, String> fav;

    public User(String gender, String location, String language,
                HashMap<String, String> fav) {
        //this.avatar = avatar;
        this.gender = gender;
        this.location = location;
        this.language = language;
        this.fav = fav;
    }

    public User(String uid, String uname, String avatar, String email) {
        this.uid = uid;
        this.uname = uname;
        this.avatar = avatar;
        this.email = email;
    }

    public User(){}

    public String getUid() {
        return uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUname() {
        return uname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }
}

