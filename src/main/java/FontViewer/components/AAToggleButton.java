package FontViewer.components;

import javax.swing.*;
import java.awt.*;

public class AAToggleButton extends JToggleButton {
    String fname;
    String floc;

    public AAToggleButton(String s, String fname, String floc) {
        super(s);
        this.fname = fname;
        this.floc = floc;
        this.setToolTipText(fname + " (" + floc + ")");
    }

    public String getFName() {
        return fname;
    }

    public String getFLoc() {
        return floc;
    }

    public void paintComponent(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        }
        super.paintComponent(g);
    }
}