import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * MULTITHREADED CHAT APPLICATION
 *
 * Deliverable: A functional chat application with a server
 * and multiple clients communicating in real time.
 *
 * Run Instructions:
 *   1. Compile: javac MultithreadedChatApplication.java
 *   2. Start server: java MultithreadedChatApplication server [port]
 *   3. Start clients: java MultithreadedChatApplication client <host> [port] [username]
 *
 * Features:
 *   - Broadcast chat messages
 *   - Private messages with "@username message"
 *   - List online users with "/who"
 *   - Show number of online users with "/count"
 *   - Disconnect with "/quit"
 */

public class MultithreadedChatApplication {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "server" -> {
                int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 12345;
                new ChatServer(port).start();
            }
            case "client" -> {
                if (args.length < 2) {
                    printUsage();
                    return;
                }
                String host = args[1];
                int port = (args.length >= 3) ? Integer.parseInt(args[2]) : 12345;
                String username = (args.length >= 4) ? args[3] : null;
                new ChatClient(host, port, username).start();
            }
            default -> printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("""
            Usage:
              Server: java MultithreadedChatApplication server [port]
              Client: java MultithreadedChatApplication client <host> [port] [username]
            """);
    }

    // ================= SERVER ==================
    static class ChatServer {
        private final int port;
        private final ExecutorService pool = Executors.newCachedThreadPool();
        private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

        ChatServer(int port) { this.port = port; }

        void start() {
            System.out.println("🚀 Server started on port " + port);
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    pool.submit(new ClientHandler(socket));
                }
            } catch (IOException e) {
                System.out.println("❌ Server error: " + e.getMessage());
            }
        }

        class ClientHandler implements Runnable {
            private final Socket socket;
            private String username;
            private PrintWriter out;
            private BufferedReader in;

            ClientHandler(Socket socket) { this.socket = socket; }

            @Override
            public void run() {
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);

                    // Ask username
                    out.println("Enter your username:");
                    username = in.readLine();
                    if (username == null || username.trim().isEmpty()) {
                        username = "User" + new Random().nextInt(1000);
                    }

                    clients.put(username, this);
                    broadcast("🔔 " + username + " joined the chat. 👥 (" + getClientCount() + " online)");

                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.equalsIgnoreCase("/quit")) {
                            break;
                        } else if (message.equalsIgnoreCase("/who")) {
                            out.println("👥 Online: " + String.join(", ", clients.keySet()));
                        } else if (message.equalsIgnoreCase("/count")) {
                            out.println("📊 Members online: " + getClientCount());
                        } else if (message.startsWith("@")) {
                            int space = message.indexOf(" ");
                            if (space > 1) {
                                String targetUser = message.substring(1, space);
                                String privateMsg = message.substring(space + 1);
                                sendPrivate(targetUser, "🕵️ (PM) " + username + ": " + privateMsg);
                            }
                        } else {
                            broadcast(username + ": " + message);
                        }
                    }
                } catch (IOException ignored) {
                } finally {
                    disconnect();
                }
            }

            void send(String msg) { out.println(msg); }

            void sendPrivate(String target, String msg) {
                ClientHandler targetClient = clients.get(target);
                if (targetClient != null) {
                    targetClient.send(msg);
                    if (targetClient != this) send(msg);
                } else {
                    send("❌ User not found: " + target);
                }
            }

            void broadcast(String msg) {
                for (ClientHandler c : clients.values()) {
                    c.send(msg);
                }
                System.out.println(msg);
            }

            void disconnect() {
                try { socket.close(); } catch (IOException ignored) {}
                if (username != null) {
                    clients.remove(username);
                    broadcast("🔕 " + username + " left the chat. 👥 (" + getClientCount() + " online)");
                }
            }
        }

        int getClientCount() {
            return clients.size();
        }
    }

    // ================= CLIENT ==================
    static class ChatClient {
        private final String host;
        private final int port;
        private final String preferredUsername;

        ChatClient(String host, int port, String username) {
            this.host = host;
            this.port = port;
            this.preferredUsername = username;
        }

        void start() {
            try (Socket socket = new Socket(host, port)) {
                System.out.println("✅ Connected to server " + host + ":" + port);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

                // Send username if provided
                String serverMsg = in.readLine();
                System.out.println(serverMsg);
                if (preferredUsername != null) {
                    out.println(preferredUsername);
                } else {
                    out.println(console.readLine());
                }

                // Thread to listen to server
                new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = in.readLine()) != null) {
                            System.out.println(msg);
                        }
                    } catch (IOException e) {
                        System.out.println("❌ Disconnected from server.");
                    }
                }).start();

                // Main loop: read user input
                String userMsg;
                while ((userMsg = console.readLine()) != null) {
                    out.println(userMsg);
                    if (userMsg.equalsIgnoreCase("/quit")) break;
                }
            } catch (IOException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
}
