package ru.focus.view;

import ru.focus.model.Client;
import ru.focus.model.ClientListener;
import ru.focus.view.util.MessageBoxUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class ChatView implements ClientListener {

    private final ChatFrame chatFrame;
    private final ConnectFrame connectFrame;
    private final Client client;

    public ChatView(Client client) {
        this.client = client;

        chatFrame = new ChatFrame();
        connectFrame = new ConnectFrame(chatFrame);

        this.client.registerListener(this);

        connectFrame.setConnectButtonListener((
                (networkAddress, clientName) -> {
                    if (
                            !networkAddress.trim().isEmpty() &&
                                    !networkAddress.equals(connectFrame.getHiddenNetworkAddressText()) &&
                                    !clientName.trim().isEmpty() &&
                                    !clientName.equals(connectFrame.getHiddenUsernameText())
                    ) {
                        this.client.setClientName(clientName.trim());

                        if (this.client.isConnected(networkAddress)) {
                            this.client.sendMessageToServerToAuth();
                        } else if (this.client.initialize(networkAddress)) {
                            this.client.sendMessageToServerToAuth();
                            this.client.startHandlingMessagesFromServer();
                        }
                    }

                }
        ));
        connectFrame.setExitButtonListener((e) -> chatFrame.dispose());
        connectFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                chatFrame.dispose();
            }
        });

        chatFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                client.sendMessageToServerToDisconnect();
                chatFrame.dispose();
            }
        });
        chatFrame.setSenderButtonListener((msg) -> {
            if (!msg.trim().isEmpty() && !msg.equals(chatFrame.getHiddenMessageText())) {
                client.sendRegularMessageToServer(msg);
                chatFrame.clearMessageTextArea();
            }
        });

        connectFrame.setVisible(true);
    }


    @Override
    public void clientsListChanged(Set<String> clientsList) {
        chatFrame.setListUserNames(clientsList);
    }

    @Override
    public void chatAreaChanged(String msg) {
        chatFrame.addTextToChatTextArea(msg);
    }

    @Override
    public void connectFrameClosed() {
        connectFrame.dispose();
    }

    @Override
    public void messageBoxShowed(String msg) {
        MessageBoxUtils.show(msg);
    }

}
