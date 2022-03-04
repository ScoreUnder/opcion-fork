package opcion.components;

import opcion.FontFile;

import java.awt.*;
import java.util.Arrays;

public class SystemFontsPanel extends AbstractJListFontPanel {
    private static final String LOCATION = "System Font";

    public SystemFontsPanel() {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        setFontList(Arrays.stream(names).map(it -> new FontFile(it, LOCATION)).toArray(FontFile[]::new));
    }
}