package ru.focus.view.util;

import lombok.experimental.UtilityClass;

import javax.swing.text.JTextComponent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@UtilityClass
public class HiddenTextPlaceholder {

    public static void add(JTextComponent textComponent, String placeholderText){
        textComponent.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textComponent.getText().equals(placeholderText)) {
                    textComponent.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textComponent.getText().isEmpty()) {
                    textComponent.setText(placeholderText);
                }
            }
        });
    }

}
