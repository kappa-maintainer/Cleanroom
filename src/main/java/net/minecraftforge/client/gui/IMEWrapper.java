package net.minecraftforge.client.gui;

import net.minecraft.client.gui.GuiTextField;
import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class IMEWrapper extends JFrame{
    public static JTextField text;

    public static IMEWrapper instance = new IMEWrapper();
    private static GuiTextField guiTextField = null;
    public static synchronized void setTextField(GuiTextField fieldIn) {
        guiTextField = fieldIn;
    }
    public IMEWrapper() {

        this.setVisible(false);
        this.setUndecorated(true);
        this.setType(Type.POPUP);
        this.setLocation(Display.getX(), Display.getY() + Display.getHeight());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        text = new JTextField();
        this.add(text);
        text.requestFocusInWindow();
        text.addActionListener(actionEvent -> {
            instance.setVisible(false);
            Keyboard.addKeyEvent(257, true);
            text.setText("");
        });
        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (guiTextField != null) {
                    guiTextField.setText(text.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (guiTextField != null) {
                    guiTextField.setText(text.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if (guiTextField != null) {
                    guiTextField.setText(text.getText());
                }
            }
        });


    }


}
