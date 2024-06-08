package ru.focus;

import ru.focus.model.Client;
import ru.focus.view.ChatView;

public class ClientMain {

    public static void main(String[] args) {

        Client client = new Client();
        new ChatView(client);

    }

}
