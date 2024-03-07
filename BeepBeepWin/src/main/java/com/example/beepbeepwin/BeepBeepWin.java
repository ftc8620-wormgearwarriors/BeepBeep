package com.example.beepbeepwin;

import java.awt.*;

    // extending Frame class to our class AWTExample1
    public class BeepBeepWin extends Frame {

        // initializing using constructor
        BeepBeepWin() {

            // creating a button
            Button b = new Button("Click Me!!");

            // setting button position on screen
            b.setBounds(30,100,80,30);

            // adding button into frame
            add(b);

            // frame size 300 width and 300 height
            setSize(800,800);

            // setting the title of Frame
            setTitle("BeepBeepWin");

            // no layout manager
            setLayout(null);

            // now frame will be visible, by default it is not visible
            setVisible(true);
        }

        // main method
        public static void main(String args[]) {
            // creating instance of Frame class
            BeepBeepWin f = new BeepBeepWin();
        }

    }