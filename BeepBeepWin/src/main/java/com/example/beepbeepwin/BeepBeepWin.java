package com.example.beepbeepwin;

import com.acmerobotics.roadrunner.Vector2d;
import com.example.wgwsimcore.WGWsimCore;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;


    // extending Frame class to our class AWTExample1
    public class BeepBeepWin extends JFrame {

        BufferedImage robotlayer;
        Graphics robotGraphic;
        JTextArea textView;

        // initializing using constructor
        BeepBeepWin() {
            final int FIELD_GRID_SIZE = 6;

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GridBagLayout grid = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            setLayout(grid);
            GridBagLayout layout = new GridBagLayout();
            this.setLayout(layout);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = FIELD_GRID_SIZE;
            this.add(new JButton("Button One"), gbc);
            gbc.gridx = 1;
            gbc.gridy = FIELD_GRID_SIZE;
            this.add(new JButton("Button two"), gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.ipady = 20;
            gbc.gridx = 2;
            gbc.gridy = FIELD_GRID_SIZE;
            this.add(new JButton("Button Three"), gbc);
            gbc.gridx = 3;
            gbc.gridy = FIELD_GRID_SIZE;
            this.add(new JButton("Button Four"), gbc);
            gbc.gridx = 0;
            gbc.gridy = FIELD_GRID_SIZE+1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridwidth = 2;
            this.add(new JButton("Button Five"), gbc);

            // create a graphic panel for the field and draw on it.
            FieldPanel fieldPanel = new FieldPanel();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = FIELD_GRID_SIZE;
            gbc.gridheight = FIELD_GRID_SIZE;
            this.add(fieldPanel,gbc);

            textView = new JTextArea("Robot text will show here");
            textView.setSize(400,400);
            textView.setPreferredSize(textView.getSize());
            textView.setMinimumSize(textView.getSize());
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = FIELD_GRID_SIZE;
            gbc.gridy = FIELD_GRID_SIZE;
            gbc.gridwidth = FIELD_GRID_SIZE;
            gbc.gridheight = FIELD_GRID_SIZE;
            this.add(textView);


            BufferedImage image;
            // todo fix this file load
            File file = new File("D:/Users/Craug/StudioProjects/TestMultiGit/BeepBeepWin/src/main/java/com/example/beepbeepwin/test.bmp");
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fieldPanel.addLayer(FieldPanel.LayerNames.BACKGROUND, image);

            robotlayer = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
            robotGraphic = robotlayer.getGraphics();
            fieldPanel.addLayer(FieldPanel.LayerNames.ROBOTS, robotlayer);

            // frame size 300 width and 300 height
            setSize(900,600);
            setMinimumSize(getSize());
//            setPreferredSize(getSize());

            // setting the title of Frame
            setTitle("BeepBeepWin");


            // now frame will be visible, by default it is not visible
            setVisible(true);

            //  todo - force test force a robot path
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
                 textView.append(str);
            }

            @Override
            public void telemetryTextClear() {
                textView.setText("");  // got some new text so clear the display
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