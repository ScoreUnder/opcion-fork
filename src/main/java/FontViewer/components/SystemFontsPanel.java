package FontViewer.components;

import FontViewer.FontFile;
import FontViewer.windows.MainWindow;

import java.awt.*;
import java.util.Arrays;

public class SystemFontsPanel extends AbstractJListFontPanel {
    private static final String LOCATION = "System Font";

    public SystemFontsPanel(MainWindow mw) {
        super(mw);

        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontList.setListData(Arrays.stream(names).map(it -> new FontFile(it, LOCATION)).toArray(FontFile[]::new));
    }
}