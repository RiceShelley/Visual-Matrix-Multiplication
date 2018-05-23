/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.linear;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Ganesh
 */
public class R3MatRender extends JPanel implements KeyListener {

    JFrame jf;

    ArrayList<Vector> verts;
    ArrayList<Point> edges;

    Vector cam;
    Vector camRot;

    Point winSize;

    double cx = 1000 / 2;
    double cy = 1000 / 2;
    int fnum = 0;

    public R3MatRender() {
        winSize = new Point(1000, 1000);
        fnum = (int) winSize.getX();
        jf = new JFrame();
        jf.setSize((int) winSize.getX(), (int) winSize.getY());
        jf.setResizable(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.addKeyListener(this);
        jf.setVisible(true);
        verts = new ArrayList<>();
        edges = new ArrayList<>();
        cam = new Vector(0, 0, 3);
        camRot = new Vector(.5, 0, 0);
        // add cube verts
        verts.add(new Vector(-1, -1, -1));
        verts.add(new Vector(1, -1, -1));
        verts.add(new Vector(1, 1, -1));
        verts.add(new Vector(-1, 1, -1));

        verts.add(new Vector(-1, -1, 1));
        verts.add(new Vector(1, -1, 1));
        verts.add(new Vector(1, 1, 1));
        verts.add(new Vector(-1, 1, 1));

        verts.add(new Vector(0, 0, 0));
        
        
        System.out.println("in");
        
        for (double i2 = 0; i2 < (2 * Math.PI); i2 += .1) {
            for (double i = 0; i < (2 * Math.PI); i += .06) {
                verts.add(new Vector((3 * Math.cos(i)) * Math.cos(i2), (3 * Math.sin(i)) * Math.cos(i2), 3 * Math.sin(i2)));
            }
        }
        
        System.out.println("out");

        edges.add(new Point(0, 1));
        edges.add(new Point(1, 2));
        edges.add(new Point(2, 3));
        edges.add(new Point(3, 0));

        edges.add(new Point(0, 4));
        edges.add(new Point(1, 5));
        edges.add(new Point(2, 6));
        edges.add(new Point(3, 7));

        edges.add(new Point(4, 5));
        edges.add(new Point(5, 6));
        edges.add(new Point(6, 7));
        edges.add(new Point(7, 4));

    }

    public Point translate3Dto2D(Vector v) {
        // multiply by -1 beacuse i belive into the screen should be negitive on zed axis

        double z = v.z + (cam.z * -1);
        double x = v.x + cam.x;
        double y = v.y + cam.y;

        double nz = z * Math.cos(camRot.y) + x * Math.sin(camRot.y);
        double nx = x * Math.cos(camRot.y) - z * Math.sin(camRot.y);
        z = nz;
        x = nx;

        nz = z * Math.cos(camRot.x) + y * Math.sin(camRot.x);
        double ny = y * Math.cos(camRot.x) - z * Math.sin(camRot.x);
        z = nz;
        y = ny;

        double f = fnum / z;
        x = (x * f) + cx;
        y = (y * f) + cy;

        return new Point((int) x, (int) y);
    }

    @Override
    public void paint(Graphics g) {

        g.clearRect(0, 0, (int) winSize.getX(), (int) winSize.getY());
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int) winSize.getX(), (int) winSize.getY());
        g.setColor(Color.GREEN);

        for (int i = 0; i < edges.size(); i++) {
            Point vertPair = edges.get(i);
            Vector v1 = verts.get((int) vertPair.getX());
            Vector v2 = verts.get((int) vertPair.getY());
            Point v1p = translate3Dto2D(v1);
            Point v2p = translate3Dto2D(v2);
            g.drawLine((int) v1p.getX() + 3, (int) v1p.getY() + 3, (int) v2p.getX() + 3, (int) v2p.getY() + 3);
        }

        for (int i = 0; i < verts.size(); i++) {
            Vector v = verts.get(i);
            if (v.z < 0) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.CYAN);
            }
            // draw point
            Point p = translate3Dto2D(v);
            g.fillOval((int) p.getX(), (int) p.getY(), 6, 6);
        }
    }

    public void run() {
        jf.add(this);
        while (true) {

            repaint();

            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                Logger.getLogger(R3MatRender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        switch (k) {
            case KeyEvent.VK_W:
                cam.z -= .05 * Math.cos(camRot.y);
                cam.x += .05 * Math.sin(camRot.y);
                break;
            case KeyEvent.VK_S:
                cam.z += .05;
                break;
            case KeyEvent.VK_A:
                cam.x -= .05;
                break;
            case KeyEvent.VK_D:
                cam.x += .05;
                break;
            case KeyEvent.VK_SPACE:
                cam.y += .05;
                break;
            case KeyEvent.VK_SHIFT:
                cam.y -= .05;
                break;
            case KeyEvent.VK_UP:
                camRot.x -= .05;
                break;
            case KeyEvent.VK_DOWN:
                camRot.x += .05;
                break;
            case KeyEvent.VK_LEFT:
                camRot.y -= .05;
                break;
            case KeyEvent.VK_RIGHT:
                camRot.y += .05;
                break;
            default:
                System.out.println("fda " + k);
                break;
        }
        System.out.println("x " + cam.x + "| y " + cam.y + "| z " + cam.z + "| y rot " + camRot.y);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
