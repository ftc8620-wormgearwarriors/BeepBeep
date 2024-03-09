package com.example.beepbeepwin;

import com.acmerobotics.roadrunner.Vector2d;
import com.example.wgwsimcore.WGWsimCore;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;


    // extending Frame class to our class AWTExample1
    public class BeepBeepWin extends JFrame {

        BufferedImage robotlayer = null;
        Graphics robotGraphic = null;

        // initializing using constructor
        BeepBeepWin() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // creating a button, set size/position, add to JFrame
            JButton b = new JButton("Click Me!!");
            b.setBounds(500,500,80,30);
            add(b);

            // create a graphic panel for the field and draw on it.
            FieldPanel fieldPanel = new FieldPanel();
            add(fieldPanel);
            BufferedImage image = null;

//            try {
////                image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/images/test.bmp")));
            File file = new File("D:/Users/Craug/StudioProjects/TestMultiGit/BeepBeepWin/src/main/java/com/example/beepbeepwin/test.bmp");
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Class c = getClass();
                URL url = c.getResource("../test.bmp");
//                if (url != null)
//                    image = ImageIO.read(url);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            fieldPanel.addLayer(FieldPanel.LayerNames.BACKGROUND, image);

            robotlayer = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
            robotGraphic = robotlayer.getGraphics();
//            robotGraphic.setColor(Color.black);
//            robotGraphic.drawOval(200,200,50, 50);

            fieldPanel.addLayer(FieldPanel.LayerNames.ROBOTS, robotlayer);

            // frame size 300 width and 300 height
            setSize(800,800);

            // setting the title of Frame
            setTitle("BeepBeepWin");

            // no layout manager
            setLayout(null);

            // now frame will be visible, by default it is not visible
            setVisible(true);

            //  todo - for test force a robot path
            wgwSimCore.clearRobotActions();
            wgwSimCore.setCurrentAction(1, wgwSimCore.getActionNames(1).get(1));
            wgwSimCore.startAction();

            // the timer variable must be a javax.swing.Timer
            // TIMER_DELAY is a constant int and = 35;
            new javax.swing.Timer((int)wgwSimCore.getTimerTickms(), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timerTick();
                }
            }).start();
        }
        private void timerTick() {
            // clear our transparent overlay to draw robots on.
            Graphics2D g2 = robotlayer.createGraphics();
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, robotlayer.getWidth(), robotlayer.getHeight());
            g2.dispose();

            wgwSimCore.timerTick();
            getContentPane().repaint(); // don't forget to repaint the container
        }


        // convert FTC field locaiton in inches to screen pixel locations
        Vector2d inch2Pixel (Vector2d inch) {
            int h = robotlayer.getHeight();
            int w = robotlayer.getWidth();

            double scale = Math.min(h, w) /  wgwSimCore.getFieldDimensionInches();
            int x = (int) (w / 2 + inch.x * scale);
            int y = (int) (h / 2 - inch.y * scale);

            return new Vector2d(x,y);
        }

        WGWsimCore wgwSimCore = new WGWsimCore() {
            @Override
            public void drawLineInches(Vector2d start, Vector2d end) {
                robotGraphic.drawLine((int)inch2Pixel(start).x, (int)inch2Pixel(start).y,
                        (int)inch2Pixel(end).x, (int)inch2Pixel(end).y);
            }

            @Override
            public void drawCircleInches(double xInches, double yInches, double rInches) {
                //todo
            }

            @Override
            public void setColor(String colorString) {
                Color color = Color.decode(colorString);
                robotGraphic.setColor(color);
            }

            @Override
            public void telemetryTextAddLine(String str) {
                // textView.append(str);
            }

            @Override
            public void telemetryTextClear() {
                // textView.setText("");  // got some new text so clear the display
            }
        };





        // main method
        public static void main(String args[]) {
            // creating instance of Frame class
            //BeepBeepWin f = new BeepBeepWin();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    BeepBeepWin f = new BeepBeepWin();
                }
            });
        }


    }