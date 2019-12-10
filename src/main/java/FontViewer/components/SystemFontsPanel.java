package FontViewer.components;

import FontViewer.FontFile;
import FontViewer.windows.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SystemFontsPanel extends AbstractListPanel {
    private MainWindow mw;
    private static final String LOCATION = "System Font";
    private JList<FontFile> systemFontsList;
    private JScrollPane systemFontsScrollPane;

    public SystemFontsPanel(MainWindow mw) {
        this.mw = mw;
        initComponents();
        initSystemFonts();
    }

    private void initSystemFonts() {
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        systemFontsList.setListData(Arrays.stream(names).map(it -> new FontFile(it, LOCATION)).toArray(FontFile[]::new));
    }

    public FontFile getItem(int itemNumber) {
        ListModel<FontFile> model = systemFontsList.getModel();
        if (itemNumber >= 0 && itemNumber < model.getSize()) {
            return model.getElementAt(itemNumber);
        } else return null;
    }

    public int getNumItems() {
        return systemFontsList.getModel().getSize();
    }

    public int getCurrentItemNum() {
        return systemFontsList.getSelectedIndex();
    }

    protected void selectItem(int p) {
        int numFonts = systemFontsList.getModel().getSize();
        if (numFonts != 0) {
            int selectedIndex = getCurrentItemNum();
            if (p != selectedIndex) {
                systemFontsList.setSelectedIndex(p);
                int spos = p * (systemFontsScrollPane.getVerticalScrollBar().getMaximum() / numFonts);
                spos -= (systemFontsScrollPane.getSize().height / 2);
                systemFontsScrollPane.getVerticalScrollBar().setValue(spos);
            }

            if (p >= 0)
                mw.setCurrentFont(systemFontsList.getModel().getElementAt(p), p);
        }
    }

    private void initComponents() {
        systemFontsScrollPane = new JScrollPane();
        systemFontsList = new JList<>();

        setLayout(new java.awt.BorderLayout());

        systemFontsScrollPane.setBorder(null);
        systemFontsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        systemFontsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                selectItem(systemFontsList.getSelectedIndex());
            }
        });
        systemFontsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectItem(systemFontsList.getSelectedIndex());
            }
        });

        systemFontsScrollPane.setViewportView(systemFontsList);

        add(systemFontsScrollPane, java.awt.BorderLayout.CENTER);

    }
}