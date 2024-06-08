package ru.focus.view.util;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;

@UtilityClass
public class MessageBoxUtils {

    private static final Component PARENT_COMPONENT = null;
    private static final String TITLE = "Error";
    private static final int MESSAGE_TYPE = JOptionPane.ERROR_MESSAGE;

    public static void show(String msg) {
        JOptionPane.showMessageDialog(PARENT_COMPONENT, msg, TITLE, MESSAGE_TYPE);
    }

}
