package cs591e1_sp19.eatogether;

public class Message implements Comparable<Message> {
    private String id;
    private String text;
    private String senderUid;

    public Message() {

    }

    public Message(String id, String text, String senderUid) {
        this.id = id;
        this.text = text;
        this.senderUid = senderUid;
    }

    @Override
    public int compareTo(Message other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Message)) {
            return false;
        }

        Message otherMsg = (Message) other;

        return this.id.equals(otherMsg.getId()) && this.text.equals(otherMsg.getText());
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(String newId) {
        id = newId;
    }

    public void setText(String newText) {
        text = newText;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}