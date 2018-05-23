/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.linear;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Ganesh
 */
public class Main extends JPanel implements MouseListener {

    /* settings object -> Settings.java is the 
    * programms front end. About 90% of it is auto generated
    * GUI builder code (rly no reason to look over there
    * all the magic happens in here) 
     */
    private Settings settings;

    // Grid size stuff
    public static int w = 800;
    public static int h = 800;
    // 20 pixels = 1 unit
    public static int gridScale = 20;

    // Global refrence of background cartesian plane so that we only have to render it once. 
    private final BufferedImage bg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

    // Basis vectors <i, j>
    public static double bas_i[] = {1.0, 0};
    public static double bas_j[] = {0, 1.0};

    // Vectors that will be displayed as a linear combination of bas_i and bas_j (these r the lil green guys)
    private final ArrayList<double[]> vectors = new ArrayList<>();

    // Toggle to see basis vector
    public static boolean showBasis = false;
    // Toggle to rotate basis vectors
    public static boolean rot = false;
    // Toggle to render lines from the origin to vectors
    public static boolean showLine = false;
    // Toggle to place vectors at custom locations (by means of left clicking) 
    public static boolean placeVector = false;
    // Adjusts the speed of the basis vector rotation 
    public static double rotSpeed = .05;

    public static void main(String[] args) {
        //R3MatRender r2mr = new R3MatRender();
        //r2mr.run();
        Main m = new Main();
        m.run();
    }

    public Main() {
        /* draw background cartesian plane
        * (is this the most inefficent way of drawing lines possible? No.
        * is it cooler than calling Graphics.drawline()? Of course it is. 
         */
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // Draw grid lines every n pixles (n = gridScale) 
                if (i % gridScale != 0 && j % gridScale != 0) {
                    bg.setRGB(i, j, 0x00000000);
                } else {
                    bg.setRGB(i, j, 0x000000FF);
                }
            }
        }
        // Init front end stuff on another thread
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                settings = new Settings();
                settings.setVisible(true);
            }
        });
    }

    /*
    * These methods have to do with
    * Rendering stuff
     */
    public void render(Graphics g) {
        // draw orgin 
        g.setColor(Color.RED);
        g.fillRect((w / 2) - 2, (h / 2) - 2, 4, 4);

        // Draw v as linear combination of i and j
        for (int n = 0; n < vectors.size(); n++) {
            drawVector(mult(vectors.get(n)), g, Color.GREEN);
        }

        if (showBasis) {
            // Draw basis 
            drawVector(bas_i, g, Color.YELLOW);
            drawVector(bas_j, g, Color.YELLOW);
        }
    }

    private void drawVector(double[] vec, Graphics g, Color c) {
        g.setColor(c);
        double i = (((double) w / 2.0) + vec[0] * (double) gridScale);
        double j = (((double) h / 2.0) - vec[1] * (double) gridScale);
        g.fillOval((int) (i - 4), (int) (j - 4), 8, 8);
        if (showLine || c == Color.YELLOW) {
            g.drawLine(w / 2, h / 2, (int) i, (int) j);
        }
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, w, h);
        g.drawImage(bg, 0, 0, null);
        render(g);
    }

    /*
    * methods that have to do with 
    * managing the vectors
     */
    public final void genVectorCube(int size) {
        size /= 2;
        vectors.clear();
        for (double j = -size; j <= size; j += 1) {
            for (double i = -size; i <= size; i += 1) {
                if (!(i == 0 && j == 0)) {
                    vectors.add(new double[]{i, j});
                }
            }
        }
    }

    public void clearVectors() {
        vectors.clear();
    }

    /*
    * methods that do math stuff
     */
    // Returns angle between two vectors
    private double ang(double u[], double v[]) {
        return Math.acos(dotp(u, v) / (mag(u) * mag(v)));
    }

    // Takes the dot product of two vectors 
    private double dotp(double u[], double v[]) {
        return (u[0] * v[0]) + (u[1] * v[1]);
    }

    // Finds the magnitude of a vector
    private double mag(double v[]) {
        return Math.sqrt(Math.pow(v[0], 2) + Math.pow(v[1], 2));
    }

    // Returns linear combination of <i, j> <- generic matrix multiplication of a (2x2) * (2x1) matrix
    private double[] mult(double v[]) {
        double nv[] = {0, 0};
        nv[0] = bas_i[0] * v[0];
        nv[1] = bas_i[1] * v[0];
        nv[0] += bas_j[0] * v[1];
        nv[1] += bas_j[1] * v[1];
        return nv;
    }

    // Runtime loop
    private void run() {
        // Wait for front end to finish loading if it has not yet
        while (true) {
            if (settings != null) {
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Add our display (the JPanel super class) to a scroll pane in settings
        settings.getScrollPane().getViewport().add(this);
        // Give settings a refrence to this object (so some buttons can call methods such as clearVector())
        settings.setMain(this);
        // Add mouse listener so user can place custom vectors
        settings.getScrollPane().addMouseListener(this);

        // Angle of rotation (Note: all angles are in radians) 
        double angle = 0;
        while (true) {
            // if rotation
            if (rot) {
                // get current angle between <bas_i, bas_j>
                double angBtwn = ang(bas_i, bas_j);
                // find magnitude of bas_i vector
                double mag = mag(bas_i);
                // rotate bas_i and scale by bas_i's original magnitude
                bas_i[0] = Math.cos(angle) * mag;
                bas_i[1] = Math.sin(angle) * mag;

                // find magnitude of bas_j vector
                mag = mag(bas_j);
                /* rotate bas_i by angle - angBtwn so that the angle between bas_i and bas_j
                * is preserved and scale by bas_j's original magnitude
                 */
                bas_j[0] = Math.cos(angle - angBtwn) * mag;
                bas_j[1] = Math.sin(angle - angBtwn) * mag;
                // Move the angle forward by value of rotate speed
                angle += rotSpeed;
            }
            // Update display
            repaint();
            // Let CPU rest a bit
            try {
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
    * Mouse listiner stuff
    */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (placeVector) {
            // place a vector at the spot where the user just clicked
            vectors.add(new double[]{(e.getX() - (w / 2)) / (double) gridScale, ((h / 2) - e.getY()) / (double) gridScale});
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
