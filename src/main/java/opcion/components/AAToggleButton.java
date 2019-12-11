package opcion.components;

import opcion.FontFile;

import javax.swing.*;
import java.awt.*;

public class AAToggleButton extends JToggleButton {
    private final FontFile font;

    public AAToggleButton(String text, FontFile font) {
        super(text);
        this.font = font;
        this.setToolTipText(getFontFile().toString());
    }

    public FontFile getFontFile() {
        return font;
    }

    public void paintComponent(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        }
        super.paintComponent(g);
    }
}