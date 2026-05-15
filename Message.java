import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { TEXT, JOIN, LEAVE, PRIVATE, USER_LIST, SERVER }

    private final String sender;
    private final String receiver;
    private final String content;
    private final Type type;
    private final String timestamp;

    public Message(String sender, String receiver, String content, Type type) {
        this.sender    = sender;
        this.receiver  = receiver;
        this.content   = content;
        this.type      = type;
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static Message broadcast(String sender, String content) {
        return new Message(sender, null, content, Type.TEXT);
    }
    public static Message privateMsg(String sender, String receiver, String content) {
        return new Message(sender, receiver, content, Type.PRIVATE);
    }
    public static Message join(String username) {
        return new Message("SERVER", null, username + " has joined the chat.", Type.JOIN);
    }
    public static Message leave(String username) {
        return new Message("SERVER", null, username + " has left the chat.", Type.LEAVE);
    }
    public static Message userList(String csvUsers) {
        return new Message("SERVER", null, csvUsers, Type.USER_LIST);
    }
    public static Message serverInfo(String content) {
        return new Message("SERVER", null, content, Type.SERVER);
    }

    public String getSender()    { return sender; }
    public String getReceiver()  { return receiver; }
    public String getContent()   { return content; }
    public Type   getType()      { return type; }
    public String getTimestamp() { return timestamp; }
}
