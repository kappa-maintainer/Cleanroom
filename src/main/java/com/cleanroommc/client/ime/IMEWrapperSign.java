package com.cleanroommc.client.ime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextComponentString;
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
public class IMEWrapperSign extends JDialog{
    public static JTextField textField;

    public static IMEWrapperSign instance = new IMEWrapperSign();
    private static TileEntitySign tileEntitySign = null;
    private static GuiEditSign guiEditSign = null;

    public static void setSign(GuiEditSign gui, TileEntitySign tile) {
        SwingUtilities.invokeLater(() -> {
            guiEditSign = gui;
            tileEntitySign = tile;
        });
    }

    public static void setText(String text) {
        SwingUtilities.invokeLater(() -> textField.setText(text));
    }


    public IMEWrapperSign() {

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
                if (tileEntitySign != null) {
                    tileEntitySign.signText[guiEditSign.getEditLine()] = new TextComponentString(textField.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (tileEntitySign != null) {
                    tileEntitySign.signText[guiEditSign.getEditLine()] = new TextComponentString(textField.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if (tileEntitySign != null) {
                    tileEntitySign.signText[guiEditSign.getEditLine()] = new TextComponentString(textField.getText());
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
                    case KeyEvent.VK_TAB, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN -> {

                        if (guiEditSign != null) {
                            Minecraft.getMinecraft().addScheduledTask(() -> {
                                try {
                                    guiEditSign.invokeKeyTyped((char) 127, IMEHelper.translateFromAWT(keyEvent.getKeyCode()));
                                } catch (IOException ignored) {}
                                textField.setText(tileEntitySign.signText[guiEditSign.getEditLine()].getUnformattedText());
                                textField.setCaretPosition(textField.getText().length());
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
