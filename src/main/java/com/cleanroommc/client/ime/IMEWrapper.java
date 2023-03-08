package com.cleanroommc.client.ime;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjglx.opengl.Display;

import javax.swing.*;

// General IMEWrapper class
@SideOnly(Side.CLIENT)
public abstract class IMEWrapper extends JDialog{
    protected JTextField textField = new JTextField();

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> textField.setText(text));
    }

    public JTextField getTextField() {
        return textField;
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
        textField.setFocusTraversalKeysEnabled(false);
        textField.requestFocusInWindow();

    }
}
