# MULTITHREADED-CHAT-APPLICATION

*COMPANY*: CODETECH IT SOLUTIONS

*NAME*: SHAIK RAYEES

*INTERN ID*: CT04DY392

*DOMAIN*: JAVA PROGRAMMING

*DURATION*: 4 WEEKS

*MENTOR*: NEELA SANTHOSH KUMAR


## DESCRIPTON


### 📡 Multithreaded Chat Application

A Java-based multithreaded client-server chat application built using sockets and concurrency, capable of handling multiple clients simultaneously in real-time.

This project was created as part of CODTECH Internship – Task 3.
The deliverable is a fully functional chat system where multiple clients can connect to a single server, exchange messages, send private messages, and see who is online.

### 🚀 Features

Multi-client support: Server handles unlimited clients using multithreading.

Broadcast messaging: Messages are shared with all connected users.

Private chat: Use @username <message> to send direct messages.

User list: Type /who to see all online members.

Online count: Server shows number of users when someone joins or leaves.

Graceful exit: Clients can leave the chat with /quit.

Real-time communication: Messages are instantly delivered across clients.

### 🛠️ Technologies Used

Java Sockets (TCP/IP) for network communication

Multithreading for handling multiple users concurrently

Collections (ConcurrentHashMap) for thread-safe client management

### 📂 File Structure
MultithreadedChatApplication.java


A single file containing both:

ChatServer (server logic)

ChatClient (client logic)

### ▶️ How to Run
#### 1. Compile
javac MultithreadedChatApplication.java

#### 2. Start the Server
java MultithreadedChatApplication server 12345


#### Output:

🚀 Server started on port 12345

#### 3. Start Clients
java MultithreadedChatApplication client 127.0.0.1 12345 Alice
java MultithreadedChatApplication client 127.0.0.1 12345 Bob

#### 4. Example Chat Flow
👤 Alice joined. (1 members online)
👤 Bob joined. (2 members online)

Alice: Hello Bob!
Bob: Hi Alice!

/who
👥 Online: Alice, Bob

@Alice How are you?   (private message from Bob to Alice)

👋 Bob left. (1 members online)

### 📜 Commands

Normal text → Broadcast to all users

/who → List all online users

@username <msg> → Send private message

/quit → Leave the chat

### 🎯 Learning Outcomes

✔ Understanding of client-server communication using sockets
✔ Implementing multithreading in Java
✔ Using synchronized collections for safe concurrent access
✔ Building a real-time chat application from scratch









