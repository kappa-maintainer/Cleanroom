package com.cleanroommc.client.ime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.opengl.Display;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class IMEWrapperBook extends JDialog{
    public static JTextField textField;

    public static IMEWrapperBook instance = new IMEWrapperBook();
    private static GuiScreenBook guiScreenBook = null;

    public static void setBookGui(GuiScreenBook gui) {
        SwingUtilities.invokeLater(() -> {
            guiScreenBook = gui;
        });
    }

    public static void setText(String text) {
        SwingUtilities.invokeLater(() -> {
            textField.setText(text);
            textField.setCaretPosition(text.length());
        });
    }

    public static void addReturn() {
        textField.setText(textField.getText() + "\n");
    }
    public IMEWrapperBook() {

        this.setVisible(false);
        this.setAlwaysOnTop(true);
        //this.setUndecorated(true);
        //this.setOpacity(0.98F);
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setType(Type.POPUP);
        this.setLocation(Display.getX(), Display.getY() + Display.getHeight());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        textField = new JTextField();
        textField.setFocusTraversalKeysEnabled(false);
        textField.requestFocusInWindow();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (guiScreenBook != null) {
                    guiScreenBook.pageSetCurrentNoSync(textField.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (guiScreenBook != null) {
                    guiScreenBook.pageSetCurrentNoSync(textField.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if (guiScreenBook != null) {
                    guiScreenBook.pageSetCurrentNoSync(textField.getText());
                }
            }
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_TAB, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_UP, KeyEvent.VK_DOWN
                            , KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> {
                        if (guiScreenBook != null) {
                            Minecraft.getMinecraft().addScheduledTask(() -> {
                                try {
                                    guiScreenBook.invokeKeyTyped(keyEvent.getKeyChar(), IMEHelper.translateFromAWT(keyEvent.getKeyCode()));
                                } catch (IOException ignored) {}
                            });
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        textField.addCaretListener(caretEvent -> {
            if(textField.getCaretPosition() < textField.getText().length())
                textField.setCaretPosition(textField.getText().length());

        });

        this.add(textField);

    }
}
