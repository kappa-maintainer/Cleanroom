package com.cleanroommc.client.ime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class IMEWrapperBook extends IMEWrapper{
    private JTextArea textArea;

    public static IMEWrapperBook instance = new IMEWrapperBook();
    private GuiScreenBook guiScreenBook = null;

    public void setBookGui(GuiScreenBook gui) {
        SwingUtilities.invokeLater(() -> {
            guiScreenBook = gui;
        });
    }

    @Override
    public void setText(String text) {
        SwingUtilities.invokeLater(() -> {
            textArea.setText(text);
            textArea.setCaretPosition(text.length());
        });
    }

    public IMEWrapperBook() {
        super();
        textArea = new JTextArea();
        textArea.setFocusTraversalKeysEnabled(false);
        textArea.requestFocusInWindow();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (guiScreenBook != null) {
                    String s = textArea.getText();
                    if(guiScreenBook.canPageSet(s)) {
                        guiScreenBook.pageSetCurrent(s);
                    } else {
                        textArea.setText(s.substring(0, s.length() - documentEvent.getLength()));
                    }
                }
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (guiScreenBook != null) {
                    String s = textArea.getText();
                    guiScreenBook.pageSetCurrent(s);

                }
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if (guiScreenBook != null) {
                    String s = textArea.getText();
                    if(guiScreenBook.canPageSet(s)) {
                        guiScreenBook.pageSetCurrent(s);
                    } else {
                        textArea.setText(s.substring(0, s.length() - documentEvent.getLength()));
                    }
                }
            }
        });
        textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_TAB, KeyEvent.VK_ESCAPE, KeyEvent.VK_UP, KeyEvent.VK_DOWN
                            , KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> {
                        if (guiScreenBook != null) {

                            Minecraft.getMinecraft().addScheduledTask(() -> {
                                try {
                                    guiScreenBook.invokeKeyTyped('\0', IMEHelper.translateFromAWT(keyEvent.getKeyCode()));
                                } catch (IOException ignored) {}
                            });

                        }
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent keyEvent) {}
        });
        textArea.addCaretListener(caretEvent -> {
            if(textArea.getCaretPosition() < textArea.getText().length())
                textArea.setCaretPosition(textArea.getText().length());
        });
        this.add(textArea);
    }
}
