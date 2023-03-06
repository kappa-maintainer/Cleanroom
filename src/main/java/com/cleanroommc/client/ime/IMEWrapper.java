package com.cleanroommc.client.ime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.opengl.Display;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class IMEWrapper extends JDialog{
    public static JTextField textField;

    public static IMEWrapper instance = new IMEWrapper();
    private static GuiTextField guiTextField = null;

    // Some gui needs update in typing, should pass these method to this lambda instead
    public static Consumer<GuiScreen> updater = gui -> {};
    // For converting two sets of coordinates, saved here for convenient
    public static ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    // Thread safety!
    public static void setTextField(GuiTextField fieldIn) {
        SwingUtilities.invokeLater(() -> guiTextField = fieldIn);
    }

    public static void setText(String text) {
        SwingUtilities.invokeLater(() -> textField.setText(text));
    }


    public IMEWrapper() {

        this.setVisible(false);
        this.setAlwaysOnTop(true);
        this.setUndecorated(true); //Minimize to one pixel
        this.setOpacity(1.0F); //Try to make it invisible
        //JFrame.setDefaultLookAndFeelDecorated(true);
        this.setType(Type.POPUP);
        this.setLocation(Display.getX(), Display.getY() + Display.getHeight()); //Default location
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        textField = new JTextField(){
            //Prevent illegal movement exception
            @Override
            public void moveCaretPosition(int pos) {
                if (pos > this.getDocument().getLength()) {
                    pos = this.getDocument().getLength();
                } else if (pos < 0) {
                    pos = 0;
                }
                super.moveCaretPosition(pos);
            }
        };
        textField.setFocusTraversalKeysEnabled(false);
        textField.requestFocusInWindow();
        // Transfer all text changes to GuiTextField
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (guiTextField != null) {
                    guiTextField.setTextNoSync(textField.getText());
                    updater.accept(Minecraft.getMinecraft().currentScreen);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (guiTextField != null) {
                    guiTextField.setTextNoSync(textField.getText());
                    updater.accept(Minecraft.getMinecraft().currentScreen);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                if (guiTextField != null) {
                    guiTextField.setTextNoSync(textField.getText());
                    updater.accept(Minecraft.getMinecraft().currentScreen);
                }
            }
        });
        // Transfer all non-input key to GuiTextField. Use ScheduledTask to prevent cross thread crash
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_TAB, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN -> {

                        if (screen != null) {
                            Minecraft.getMinecraft().addScheduledTask(() -> {
                                try {
                                    screen.invokeKeyTyped(keyEvent.getKeyChar(), IMEHelper.translateFromAWT(keyEvent.getKeyCode()));
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
        // Cursor and selection syncing, may find better condition
        textField.addCaretListener(caretEvent -> {
            guiTextField.setCursorPositionNoSync(textField.getCaretPosition());
            if (!Objects.equals(textField.getSelectedText(), "")) {
                guiTextField.setSelection(textField.getCaret().getDot(), textField.getCaret().getMark());
            }

        });

        this.add(textField);

    }
}
