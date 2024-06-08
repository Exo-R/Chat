package ru.focus;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
public class Server {

    private final int port;
    private final Map<String, MessageSocket> clients;
    private boolean isRunning;

    public Server(int port) {
        this.port = port;
        clients = new HashMap<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            log.info("Server shutting down...");

            isRunning = false;
            closeMessageSocketForAllClients();
        }));
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            log.info("Server has been initialized! Port: {}", port);

            isRunning = true;
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, this);
            }
        } catch (IOException e) {
            log.error("IOException error starting server! {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception error starting server! {}", e.getMessage());
        }
    }

    public Set<String> getClientNamesList() {
        synchronized (clients) {
            return clients.keySet();
        }
    }

    public void sendMessageToAllClients(Message msg) {
        synchronized (clients) {
            try {
                for (String clientName : clients.keySet()) {
                    clients.get(clientName).sendMessage(msg);
                }
            } catch (IOException e) {
                log.error("Error sending message to clients! {}", e.getMessage());
            }
        }
    }

    public void addClient(String name, MessageSocket clientSocket) {
        synchronized (clients) {
            clients.put(name, clientSocket);
            log.info("Client '{}' added.", name);
        }
    }

    public void removeClient(String name) {
        synchronized (clients) {
            MessageSocket clientSocket = clients.remove(name);
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                    log.info("Client '{}' disconnected.", name);
                } catch (IOException e) {
                    log.info("Error closing client socket: {}", e.getMessage());
                }
            }
        }
    }

    public boolean containsClient(String name) {
        synchronized (clients) {
            return clients.containsKey(name);
        }
    }

    private void closeMessageSocketForAllClients() {
        synchronized (clients) {
            for (MessageSocket clientSocket : clients.values()) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    log.info("Error closing client socket: {}", e.getMessage());
                }
            }
            clients.clear();
        }
    }

}