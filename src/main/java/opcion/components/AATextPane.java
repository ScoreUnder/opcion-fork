package opcion.components;

import javax.swing.*;
import java.awt.*;

public class AATextPane extends JTextPane {
    public void paintComponent(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        }
        super.paintComponent(g);
    }
}