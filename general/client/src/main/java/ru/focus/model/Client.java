package ru.focus.model;

import lombok.extern.log4j.Log4j2;
import ru.focus.Message;
import ru.focus.MessageSocket;
import ru.focus.MessageType;
import ru.focus.model.util.DateTimeUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
public class Client {

    private static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss 'UTC+00:00'";
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_DATE_TIME);

    private int port;
    private String address;
    private String clientName;
    private MessageSocket messageSocket;
    private final Socket socket;
    private final List<ClientListener> listeners;

    public Client() {
        socket = new Socket();
        listeners = new ArrayList<>();
    }

    public boolean initialize(String networkAddress) {
        try {
            address = getAddress(networkAddress);
            port = getPort(networkAddress);
            socket.connect(new InetSocketAddress(address, port));
            messageSocket = new MessageSocket(socket);

            log.info("'Client' has been initialized!");

            return true;
        } catch (NumberFormatException e) {
            notifyCallMessageBox("The server port is incorrect!");
            log.error("The server port is incorrect! {}", e.getMessage());
        } catch (Exception e) {
            notifyCallMessageBox("The address server data is incorrect!");
            log.error("The address server data is incorrect! {}", e.getMessage());
        }
        return false;
    }

    public boolean isConnected(String newNetworkAddress) {
        String[] partsNetworkAddress = newNetworkAddress.split(":");
        if (partsNetworkAddress.length == 2) {
            return partsNetworkAddress[0].equals(address) &&
                    partsNetworkAddress[1].equals(String.valueOf(port)) &&
                    socket.isConnected();
        }
        return false;
    }

    public void startHandlingMessagesFromServer() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message msg = messageSocket.receiveMessage();
                    handleMessage(msg);
                } catch (IllegalArgumentException e) {
                    log.error("Received an illegal argument exception: {}", e.getMessage());
                    closeMessageSocket();
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    log.error("Error receiving message from server: {}", e.getMessage());
                    closeMessageSocket();
                    notifyCallMessageBox("You have been disconnected from the server due to technical problems!");
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void sendRegularMessageToServer(String msg) {
        Message regularMsg = createMessage(
                MessageType.MESSAGE,
                clientName,
                msg,
                DateTimeUtils.getCurrentDateTime(dateTimeFormatter)
        );
        sendMessage(regularMsg, "Error when sending message to the server: ");
    }

    public void sendMessageToServerToDisconnect() {
        Message discMsg = createMessage(
                MessageType.DISCONNECT,
                clientName,
                "has disconnected!",
                DateTimeUtils.getCurrentDateTime(dateTimeFormatter)
        );
        sendMessage(discMsg, "Error when disconnecting from the server: ");
    }

    public void sendMessageToServerToAuth() {
        Message authMsg = createMessage(
                MessageType.TRYING_TO_LOGIN,
                clientName,
                "has joined!",
                DateTimeUtils.getCurrentDateTime(dateTimeFormatter)
        );
        sendMessage(authMsg, "Error connecting to the server: ");
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void registerListener(ClientListener listener) {
        listeners.add(listener);
    }

    private String getAddress(String networkAddress) {
        return networkAddress.split(":")[0];
    }

    private int getPort(String networkAddress) {
        return Integer.parseInt(networkAddress.split(":")[1]);
    }

    private void handleMessage(Message msg){
        switch (msg.getMessageType()) {
            case MESSAGE -> {
                notifyAboutChatAreaChanged(convertMessage(msg));
            }
            case LOGGED -> {
                if (Objects.equals(msg.getClientName(), clientName)) {
                    notifyAboutConnectFrameClosed();
                }
                notifyAboutChatAreaChanged(convertMessage(msg));
                notifyAboutClientsListChanged(msg.getClientsList());
            }
            case CLIENT_NAME_TAKEN -> {
                notifyCallMessageBox("This name already exists on the server!");
            }
            case DISCONNECT -> {
                notifyAboutChatAreaChanged(convertMessage(msg));
                notifyAboutClientsListChanged(msg.getClientsList());
            }
            default -> {
                log.warn("An unused case when handling a message!");
            }
        }
    }

    private String convertMessage(Message msg) {
        StringBuilder convertedMsg = new StringBuilder();
        switch (msg.getMessageType()) {
            case MESSAGE -> {
                convertedMsg.append(msg.getClientName())
                        .append(":")
                        .append(System.lineSeparator())
                        .append(msg.getMessage());
            }
            case LOGGED, DISCONNECT -> {
                convertedMsg.append(msg.getClientName())
                        .append(" ")
                        .append(msg.getMessage());
            }
            default -> {
                log.warn("An unused case when converting a message!");
            }
        }
        return convertedMsg.append(System.lineSeparator())
                .append(msg.getCurrentDateTime())
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .toString();
    }

    private void closeMessageSocket(){
        try {
            messageSocket.close();
        } catch (IOException e) {
            log.error("Error closing messageSocket: {}", e.getMessage());
        }
    }

    private Message createMessage(MessageType msgType, String clientName, String msg, String currentDateTime){
        Message message = new Message();
        message.setMessageType(msgType);
        message.setClientName(clientName);
        message.setMessage(msg);
        message.setCurrentDateTime(currentDateTime);
        return message;
    }

    private void sendMessage(Message msg, String logErrorMsg) {
        try {
            messageSocket.sendMessage(msg);
        } catch (IOException e) {
            log.error(logErrorMsg, e.getMessage());
        }
    }

    private void notifyAboutClientsListChanged(Set<String> clientsList) {
        for (ClientListener listener : listeners) {
            listener.clientsListChanged(clientsList);
        }
    }

    private void notifyAboutChatAreaChanged(String msg) {
        for (ClientListener listener : listeners) {
            listener.chatAreaChanged(msg);
        }
    }

    private void notifyAboutConnectFrameClosed() {
        for (ClientListener listener : listeners) {
            listener.connectFrameClosed();
        }
    }

    private void notifyCallMessageBox(String msg) {
        for (ClientListener listener : listeners) {
            listener.messageBoxShowed(msg);
        }
    }

}
