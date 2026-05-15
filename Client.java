import java.io.*;
import java.net.*;

public class Client {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final String username;
    private final String host;
    private final int port;
    private ClientListener listener;

    public interface ClientListener {
        void onMessage(Message message);
        void onConnected();
        void onDisconnected(String reason);
        void onUserListUpdated(String[] users);
    }

    public Client(String username, String host, int port) {
        this.username = username;
        this.host     = host;
        this.port     = port;
    }

    public void setListener(ClientListener l) { this.listener = l; }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out    = new ObjectOutputStream(socket.getOutputStream());
            in     = new ObjectInputStream(socket.getInputStream());

            out.writeObject(Message.broadcast(username, "HELLO"));
            out.flush();

            if (listener != null) listener.onConnected();
            new Thread(this::receiveLoop, "receive-thread").start();
            return true;
        } catch (IOException e) {
            if (listener != null) listener.onDisconnected("Could not connect: " + e.getMessage());
            return false;
        }
    }

    private void receiveLoop() {
        try {
            Message msg;
            while ((msg = (Message) in.readObject()) != null) {
                if (listener != null) {
                    if (msg.getType() == Message.Type.USER_LIST) {
                        String[] users = msg.getContent().isEmpty()
                                ? new String[0]
                                : msg.getContent().split(",");
                        listener.onUserListUpdated(users);
                    } else {
                        listener.onMessage(msg);
                    }
                }
            }
        } catch (EOFException | SocketException e) {
            if (listener != null) listener.onDisconnected("Disconnected from server.");
        } catch (IOException | ClassNotFoundException e) {
            if (listener != null) listener.onDisconnected("Connection error: " + e.getMessage());
        }
    }

    public void sendMessage(String text) {
        send(Message.broadcast(username, text));
    }

    public void sendPrivate(String receiver, String text) {
        send(Message.privateMsg(username, receiver, text));
    }

    private void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            if (listener != null) listener.onDisconnected("Send failed: " + e.getMessage());
        }
    }

    public void disconnect() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    public String getUsername() { return username; }
}
