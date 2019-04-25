package cs591e1_sp19.eatogether;

public class MsgModel {

    public String sender_id, sender_name,txt;

    public MsgModel(String sender_id, String sender_name, String txt){
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.txt = txt;
    }

    public MsgModel(){}

}
