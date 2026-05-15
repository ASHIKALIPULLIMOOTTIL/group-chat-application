import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    public static final int PORT = 5000;

    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private ServerListener listener;

    public interface ServerListener {
        void onLog(String message);
        void onClientConnected(String username);
        void onClientDisconnected(String username);
    }

    public void setListener(ServerListener l) { this.listener = l; }

    public void start() {
        log("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                log("New connection from " + socket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            log("Server error: " + e.getMessage());
        }
    }

    public void broadcast(Message msg, String excludeUser) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (excludeUser == null || !entry.getKey().equals(excludeUser)) {
                entry.getValue().send(msg);
            }
        }
    }

    private void broadcastUserList() {
        String csv = String.join(",", clients.keySet());
        broadcast(Message.userList(csv), null);
    }

    private void log(String msg) {
        System.out.println("[SERVER] " + msg);
        if (listener != null) listener.onLog(msg);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private String username;

        ClientHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in  = new ObjectInputStream(socket.getInputStream());

                Message hello = (Message) in.readObject();
                username = hello.getSender();

                if (clients.containsKey(username)) {
                    send(Message.serverInfo("Username already taken. Please reconnect with a different name."));
                    socket.close();
                    return;
                }

                clients.put(username, this);
                log(username + " connected.");
                if (listener != null) listener.onClientConnected(username);

                broadcast(Message.join(username), username);
                broadcastUserList();
                send(Message.serverInfo("Welcome to the chat, " + username + "!"));

                Message msg;
                while ((msg = (Message) in.readObject()) != null) {
                    handleMessage(msg);
                }

            } catch (EOFException | SocketException e) {
                // client disconnected
            } catch (IOException | ClassNotFoundException e) {
                log("Error with client " + username + ": " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        private void handleMessage(Message msg) {
            switch (msg.getType()) {
                case TEXT:
                    log(username + ": " + msg.getContent());
                    broadcast(msg, null);
                    break;
                case PRIVATE:
                    String target = msg.getReceiver();
                    ClientHandler targetHandler = clients.get(target);
                    if (targetHandler != null) {
                        targetHandler.send(msg);
                        send(msg);
                    } else {
                        send(Message.serverInfo("User '" + target + "' is not online."));
                    }
                    break;
                default:
                    break;
            }
        }

        void send(Message msg) {
            try {
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                log("Failed to send to " + username);
            }
        }

        private void disconnect() {
            if (username != null) {
                clients.remove(username);
                log(username + " disconnected.");
                if (listener != null) listener.onClientDisconnected(username);
                broadcast(Message.leave(username), username);
                broadcastUserList();
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
