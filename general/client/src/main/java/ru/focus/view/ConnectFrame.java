package ru.focus.view;

import ru.focus.view.listener.ConnectButtonEventListener;
import ru.focus.view.util.HiddenTextPlaceholder;
import ru.focus.view.util.TextLengthLimiter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnectFrame extends JDialog {

    private static final int MAX_USERNAME_LENGTH = 25;
    private static final int MAX_NETWORK_ADDRESS_LENGTH = 100;
    private static final String HIDDEN_NETWORK_ADDRESS_TEXT = "example: localhost:8080";
    private static final String HIDDEN_USERNAME_TEXT = "example: username";

    private final Container contentPane;
    private final GridBagLayout mainLayout;
    private JButton exitButton;

    private ConnectButtonEventListener listener;


    public ConnectFrame(JFrame clientFrame) {
        super(clientFrame, "Connecting to the chat", true);
        setResizable(false);

        contentPane = getContentPane();
        mainLayout = new GridBagLayout();
        contentPane.setLayout(mainLayout);

        addAddressNameLabel();
        JTextField networkAddressTextField = addNetworkAddressTextField();
        addUserNameLabel();
        JTextField userNameTextField = addUserNameTextField();
        addExitButton();
        addConnectButton(networkAddressTextField, userNameTextField);

        pack();
        setLocationRelativeTo(null);
    }

    public String getHiddenNetworkAddressText(){
        return HIDDEN_NETWORK_ADDRESS_TEXT;
    }

    public String getHiddenUsernameText(){
        return HIDDEN_USERNAME_TEXT;
    }

    public void setExitButtonListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }

    public void setConnectButtonListener(ConnectButtonEventListener listener) {
        this.listener = listener;
    }

    private void addAddressNameLabel() {
        JLabel addressNameLabel = new JLabel("Server address:", SwingConstants.CENTER);
        addressNameLabel.setPreferredSize(new Dimension(230, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 0);
        c.gridy = 0;
        mainLayout.setConstraints(addressNameLabel, c);
        contentPane.add(addressNameLabel, c);
    }

    private JTextField addNetworkAddressTextField() {
        JTextField networkAddressTextField = new JTextField(20);
        HiddenTextPlaceholder.add(networkAddressTextField, HIDDEN_NETWORK_ADDRESS_TEXT);
        TextLengthLimiter.set(networkAddressTextField, MAX_NETWORK_ADDRESS_LENGTH);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 0, 10, 10);
        c.gridy = 0;
        mainLayout.setConstraints(networkAddressTextField, c);
        contentPane.add(networkAddressTextField, c);
        networkAddressTextField.setText(HIDDEN_NETWORK_ADDRESS_TEXT);
        return networkAddressTextField;
    }

    private JTextField addUserNameTextField() {
        JTextField userNameTextField = new JTextField(20);
        HiddenTextPlaceholder.add(userNameTextField, HIDDEN_USERNAME_TEXT);
        TextLengthLimiter.set(userNameTextField, MAX_USERNAME_LENGTH);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 0, 10, 10);
        c.gridy = 1;
        mainLayout.setConstraints(userNameTextField, c);
        contentPane.add(userNameTextField, c);
        userNameTextField.setText(HIDDEN_USERNAME_TEXT);
        return userNameTextField;
    }

    private void addUserNameLabel() {
        JLabel userNameLabel = new JLabel("Username:", SwingConstants.CENTER);
        userNameLabel.setPreferredSize(new Dimension(230, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 0);
        c.gridy = 1;
        mainLayout.setConstraints(userNameLabel, c);
        contentPane.add(userNameLabel, c);
    }

    private void addExitButton() {
        exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(230, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 0);
        c.gridy += 3;
        mainLayout.setConstraints(exitButton, c);
        contentPane.add(exitButton, c);
    }

    private void addConnectButton(JTextField addressNameTextField, JTextField userNameTextField) {
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> {
            if (listener != null) {
                listener.onButtonClick(addressNameTextField.getText(), userNameTextField.getText());
            }
        });
        connectButton.setPreferredSize(new Dimension(230, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 0, 10, 10);
        c.gridx = 1;
        mainLayout.setConstraints(connectButton, c);
        contentPane.add(connectButton, c);
    }

}
