package com.example.beepbeepwin;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

// concept taking from https://stackoverflow.com/questions/35974972/multiple-independent-layers-in-graphics

public class FieldPanel extends JPanel {
    public enum LayerNames {BACKGROUND, ROBOTS, GRID}

    LinkedHashMap<LayerNames, BufferedImage> layers;

    public FieldPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.pink);
        setSize(400,400);
        setMinimumSize(getSize());
        setPreferredSize(getSize());
        layers = new LinkedHashMap<>();
    }

    public void addLayer(LayerNames layer, BufferedImage image){
        layers.put(layer, image);
    }

//    public Dimension getPreferredSize() {
//        return new Dimension(100,1000);
//    }


    public void paint(Graphics g) {
        super.paintComponent(g);
//        g.setColor(Color.BLUE);
//        g.fillOval(75,75,150,75);
//        g.setColor(Color.green);
//        g.drawString("WGW Sim Test", 100,100);
        for(LayerNames i : layers.keySet())  //render all layers
            g.drawImage(layers.get(i), 0, 0, getWidth(), getHeight(), null);
    }


}
