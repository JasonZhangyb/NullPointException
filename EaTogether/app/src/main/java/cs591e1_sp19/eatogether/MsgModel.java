package cs591e1_sp19.eatogether;

public class MsgModel {

    public String sender_id, sender_name,txt, avatar;

    public MsgModel(String sender_id, String sender_name, String txt, String avatar){
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.avatar = avatar;
        this.txt = txt;
    }

    public MsgModel(){}

}
