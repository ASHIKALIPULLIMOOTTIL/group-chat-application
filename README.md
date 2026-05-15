# ⚡ ChatApp — Java Socket Programming Chat Application

A **multi-client real-time chat application** built with Java TCP sockets and Swing GUI.

---

## 📁 Project Structure

```
ChatApp/
├── src/
│   └── chatapp/
│       ├── common/
│       │   └── Message.java       ← Serializable message model
│       ├── server/
│       │   ├── Server.java        ← Multi-threaded TCP server
│       │   └── ServerGUI.java     ← Server admin dashboard (Swing)
│       └── client/
│           ├── Client.java        ← Socket client logic
│           └── ClientGUI.java     ← Chat UI (Swing)
├── build.sh                       ← One-command build script
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher

### 1. Compile

**On Linux/macOS:**
```bash
chmod +x build.sh
./build.sh
```

**On Windows (manually):**
```cmd
mkdir out
javac -d out -sourcepath src src\chatapp\common\Message.java src\chatapp\server\Server.java src\chatapp\server\ServerGUI.java src\chatapp\client\Client.java src\chatapp\client\ClientGUI.java
```

### 2. Start the Server
```bash
java -cp out chatapp.server.ServerGUI
```
The server GUI opens and begins listening on **port 5000**.

### 3. Start One or More Clients
```bash
java -cp out chatapp.client.ClientGUI
```
- Enter your username, server host (`localhost`), and port (`5000`)
- Open multiple terminals / windows to simulate multiple users

---

## ✨ Features

| Feature | Description |
|---|---|
| 🌐 Multi-client | Server handles many clients simultaneously via threads |
| 💬 Broadcast chat | Messages sent to everyone in the room |
| 🔒 Private messages | Send DMs to a specific online user |
| 👥 Online user list | Live updated list of connected users |
| 🖥️ Server dashboard | GUI showing connected users and server logs |
| 📦 Serialized messages | Structured `Message` objects over `ObjectOutputStream` |
| 🎨 Dark UI | Modern dark theme with Swing components |

---

## 🏗️ Architecture

```
Client A ──┐
Client B ──┤──► Server (port 5000) ──► Broadcasts to all clients
Client C ──┘      (1 thread per client)
```

**Key classes:**
- `Message` — Serializable DTO with type (TEXT, PRIVATE, JOIN, LEAVE, USER_LIST, SERVER)
- `Server` — `ServerSocket` accept loop + per-client `ClientHandler` threads
- `Client` — Connects via `Socket`, reads messages on a background thread
- `ServerGUI` / `ClientGUI` — Swing frontends wiring UI events to network calls

---

## 🛠️ How It Works

1. Server starts a `ServerSocket` on port 5000 and loops calling `accept()`
2. Each accepted connection gets a `ClientHandler` running on its own thread
3. Client sends its username as the first message (handshake)
4. Server stores `username → ClientHandler` in a `ConcurrentHashMap`
5. Broadcast: server iterates all handlers and calls `send()`
6. Private: server looks up the target handler by username and sends only to them
7. On disconnect: server removes client, notifies others, updates user list

---

## 📖 Extending the Project

- Add a database (SQLite/MySQL) to persist chat history
- Add file/image sharing via `DataOutputStream`
- Add message encryption (AES/RSA)
- Replace Swing with JavaFX for a modern UI
- Add rooms/channels support
