package com.example.beepbeepwin;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class FieldCanvas extends Canvas {
    public FieldCanvas() {
        setBackground(Color.pink);
        setSize(400,400);
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(75,75,150,75);
    }
}
