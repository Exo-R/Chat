package ru.focus.view;

import ru.focus.view.listener.SendButtonEventListener;
import ru.focus.view.util.HiddenTextPlaceholder;
import ru.focus.view.util.TextLengthLimiter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Set;

public class ChatFrame extends JFrame {

    private static final int MAX_MESSAGE_LENGTH = 300;
    private static final String HIDDEN_MESSAGE_TEXT = "Type here... (max " + MAX_MESSAGE_LENGTH + " characters)";

    private final Container contentPane;
    private final GridBagLayout mainLayout;
    private JTextArea chatTextArea;
    private JTextArea messageTextArea;
    private JList<String> listUserNames;

    private SendButtonEventListener listener;


    public ChatFrame() {
        super("Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        contentPane = getContentPane();
        mainLayout = new GridBagLayout();
        contentPane.setLayout(mainLayout);

        addChatTextArea();
        JTextArea msgTextArea = addMsgTextArea();
        addButtonSender(msgTextArea);
        addListUserNames();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getHiddenMessageText() {
        return HIDDEN_MESSAGE_TEXT;
    }

    public void addTextToChatTextArea(String msg) {
        chatTextArea.append(msg);
    }

    public void setListUserNames(String[] userNames) {
        listUserNames.setListData(userNames);
    }

    public void setListUserNames(Set<String> userNames) {
        listUserNames.setListData(userNames.toArray(new String[0]));
    }

    public void clearMessageTextArea() {
        messageTextArea.setText("");
    }

    public void setSenderButtonListener(SendButtonEventListener listener) {
        this.listener = listener;
    }

    private void addChatTextArea() {
        chatTextArea = new JTextArea(30, 1);
        chatTextArea.setEditable(false);
        chatTextArea.setLineWrap(true);
        JScrollPane scrollPaneChatTextArea = new JScrollPane(
                chatTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 2;
        mainLayout.setConstraints(scrollPaneChatTextArea, c);
        contentPane.add(scrollPaneChatTextArea, c);
    }

    private JTextArea addMsgTextArea() {
        messageTextArea = new JTextArea(HIDDEN_MESSAGE_TEXT, 4, 40);
        HiddenTextPlaceholder.add(messageTextArea, HIDDEN_MESSAGE_TEXT);
        TextLengthLimiter.set(messageTextArea, MAX_MESSAGE_LENGTH);
        messageTextArea.setLineWrap(true);
        JScrollPane scrollPaneMsgTextArea = new JScrollPane(
                messageTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 10, 10);
        c.gridy = 2;
        c.gridwidth = 1;
        mainLayout.setConstraints(scrollPaneMsgTextArea, c);
        contentPane.add(scrollPaneMsgTextArea, c);
        return messageTextArea;
    }

    private void addButtonSender(JTextArea msgTextArea) {
        JButton senderButton = new JButton("Send");
        senderButton.addActionListener(e -> {
            if (listener != null) {
                listener.onButtonClick(msgTextArea.getText());
            }
        });
        senderButton.setPreferredSize(new Dimension(150, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 0, 10, 10);
        c.gridy = 2;
        c.gridx = 4;
        mainLayout.setConstraints(senderButton, c);
        contentPane.add(senderButton, c);
    }

    private void addListUserNames() {
        listUserNames = new JList<>();
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Participants");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        listUserNames.setBorder(titledBorder);
        JScrollPane scrollPaneListUserNames = new JScrollPane(
                listUserNames,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneListUserNames.setPreferredSize(new Dimension(150, 490));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 0, 10, 10);
        c.gridx = 4;
        c.gridy = 1;
        mainLayout.setConstraints(scrollPaneListUserNames, c);
        contentPane.add(scrollPaneListUserNames, c);
    }

}
