package FontViewer.components;

import FontViewer.FontFile;

import java.awt.*;
import java.util.Arrays;

public class SystemFontsPanel extends AbstractJListFontPanel {
    private static final String LOCATION = "System Font";

    public SystemFontsPanel() {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontList.setListData(Arrays.stream(names).map(it -> new FontFile(it, LOCATION)).toArray(FontFile[]::new));
    }
}