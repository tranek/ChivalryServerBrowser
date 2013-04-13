package test;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import javax.swing.JLabel;
import java.awt.Color;

@SuppressWarnings("serial")
public class ImagePanel extends Panel {
    // The Image to store the background image in.
    Image img;
    public ImagePanel()
    {
    	
    	JLabel lblHelloWorld = new JLabel("Hello World!");
    	lblHelloWorld.setForeground(Color.WHITE);
    	add(lblHelloWorld);
        // Loads the background image and stores in img object.
        img = Toolkit.getDefaultToolkit().createImage(ImagePanel.class.getResource("\\images\\chiv_bg.jpg").toExternalForm());
    }

    public void paint(Graphics g)
    {
        // Draws the img to the BackgroundPanel.
        g.drawImage(img, 0, 0, null);
    }
}
