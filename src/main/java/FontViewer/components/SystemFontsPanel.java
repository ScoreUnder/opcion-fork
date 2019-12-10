package FontViewer.components;

import FontViewer.windows.MainWindow;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SystemFontsPanel extends AbstractListPanel {
    private MainWindow mw;
    private String[] names = new String[0];
    private String location;
    private JList<String> systemFontsList;
    private JScrollPane systemFontsScrollPane;

    public SystemFontsPanel(MainWindow mw) {
        this.mw = mw;
        initComponents();
        initSystemFonts();
    }

    private void initSystemFonts() {
        names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        location = "System Font";
        systemFontsList.setListData(names);
    }

    public String[] getItem(int itemNumber) {
        if (itemNumber >= 0 && itemNumber < names.length) {
            return new String[]{
                    names[itemNumber],
                    location,
                    String.valueOf(itemNumber),
            };
        } else return new String[3];
    }

    public int getNumItems() {
        return names.length;
    }

    public int getCurrentItemNum() {
        return systemFontsList.getSelectedIndex();
    }

    public void selectItem(String name, String loc) {
        systemFontsList.setSelectedValue(name, true);
        int p = systemFontsList.getSelectedIndex();
        if (p >= 0)
            mw.setCurrentFont(names[p], location, p);
    }

    protected void setCurrentItem(int p, boolean updateUI) {
        if (updateUI) {
            systemFontsList.setSelectedIndex(p);
            int spos = p * (systemFontsScrollPane.getVerticalScrollBar().getMaximum() / names.length);
            spos -= (systemFontsScrollPane.getSize().height / 2);
            systemFontsScrollPane.getVerticalScrollBar().setValue(spos);
        }

        if (p >= 0)
            mw.setCurrentFont(names[p], location, p);
    }

    private void initComponents() {
        systemFontsScrollPane = new JScrollPane();
        systemFontsList = new JList<>();

        setLayout(new java.awt.BorderLayout());

        systemFontsScrollPane.setBorder(null);
        systemFontsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        systemFontsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                setCurrentItem(systemFontsList.getSelectedIndex(), false);
            }
        });
        systemFontsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setCurrentItem(systemFontsList.getSelectedIndex(), false);
            }
        });

        systemFontsScrollPane.setViewportView(systemFontsList);

        add(systemFontsScrollPane, java.awt.BorderLayout.CENTER);

    }
}