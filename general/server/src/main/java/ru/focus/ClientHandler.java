package ru.focus;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

@Log4j2
public class ClientHandler implements Runnable{

    private final Socket clientSocket;
    private final Server server;
    private final Thread currentThread;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        currentThread = new Thread(this);
        currentThread.start();
    }

    @Override
    public void run() {
        try (MessageSocket msgSocket = new MessageSocket(clientSocket)) {

            log.info("Start client handler");

            while (!currentThread.isInterrupted()) {
                try {
                    Message newMsg = msgSocket.receiveMessage();
                    handleClientMessage(msgSocket, newMsg);
                } catch (SocketException e) {
                    log.info("Client socket closed: {}", e.getMessage());
                    currentThread.interrupt();
                } catch (IOException e) {
                    log.info("Error receiving message: {}", e.getMessage());
                    currentThread.interrupt();
                }
            }
        } catch (IOException e) {
            log.info("IOException error initializing client handler: {}", e.getMessage());
        } finally {
            closeClientSocket();
        }
    }

    private void handleClientMessage(MessageSocket msgSocket, Message newMsg) throws IOException{
        switch (newMsg.getMessageType()) {
            case MESSAGE -> server.sendMessageToAllClients(newMsg);
            case TRYING_TO_LOGIN -> {
                String clientName = newMsg.getClientName();
                if (!server.containsClient(clientName)) {
                    server.addClient(clientName, msgSocket);

                    newMsg.setMessageType(MessageType.LOGGED);
                    newMsg.setClientsList(server.getClientNamesList());

                    server.sendMessageToAllClients(newMsg);
                } else {
                    newMsg.setMessageType(MessageType.CLIENT_NAME_TAKEN);
                    msgSocket.sendMessage(newMsg);
                }
            }
            case DISCONNECT -> {
                String clientName = newMsg.getClientName();
                server.removeClient(clientName);

                newMsg.setClientsList(server.getClientNamesList());

                server.sendMessageToAllClients(newMsg);

                msgSocket.close();
                currentThread.interrupt();
            }
            default -> {
                log.warn("An unused case when handling a message!");
            }
        }
    }

    private void closeClientSocket(){
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            log.info("Error closing client socket: {}", e.getMessage());
        }
    }

}
