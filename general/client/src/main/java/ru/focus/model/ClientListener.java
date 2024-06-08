package ru.focus.model;

import java.util.Set;

public interface ClientListener {

    void clientsListChanged(Set<String> clientsList);

    void chatAreaChanged(String msg);

    void connectFrameClosed();

    void messageBoxShowed(String msg);

}
