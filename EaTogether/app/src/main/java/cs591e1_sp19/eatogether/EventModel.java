package cs591e1_sp19.eatogether;

import java.util.HashMap;

public class EventModel {

    public String creator_id, res_id, res_name, post_id, latitude, longitude, time1, time2;
    public HashMap<String, String> guests;

    public EventModel(String creator_id, String res_id, String res_name, String post_id,
                      String latitude, String longitude, HashMap<String, String> guests,
                      String time1, String time2){
        this.creator_id = creator_id;
        this.res_id = res_id;
        this.res_name = res_name;
        this.post_id = post_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.guests = guests;
        this.time1 = time1;
        this.time2 = time2;

    }

    public EventModel(){}

}
