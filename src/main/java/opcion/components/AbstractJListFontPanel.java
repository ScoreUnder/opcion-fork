package opcion.components;

import opcion.FontFile;
import opcion.util.ScrollTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class AbstractJListFontPanel extends AbstractListPanel {
    private final JList<FontFile> fontList = new JList<>();
    private FontFile[] internalFontList = new FontFile[0];
    private final JScrollPane fontScrollPane = new JScrollPane();

    protected AbstractJListFontPanel() {
        setLayout(new BorderLayout());
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(evt -> selectItem(fontList.getSelectedIndex()));
        fontScrollPane.setViewportView(fontList);

        JPanel searchableFontPanel = new JPanel(new BorderLayout(0, 0));
        JTextField searchField = new JTextField();
        searchField.setToolTipText("Search for font by name");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER || internalFontList.length < 1000) {
                    String search = searchField.getText().toLowerCase();
                    FontFile[] filtered = Arrays.stream(internalFontList)
                            .filter(it -> it.getLogicalName().toLowerCase().contains(search))
                            .toArray(FontFile[]::new);
                    fontList.setListData(filtered);
                    fireFontListUpdate();
                }
            }
        });

        searchableFontPanel.add(searchField, BorderLayout.NORTH);
        searchableFontPanel.add(fontScrollPane, BorderLayout.CENTER);
        add(searchableFontPanel, BorderLayout.CENTER);
    }

    protected void setFontList(FontFile[] files) {
        internalFontList = files;
        fontList.setListData(files);
        fontList.setEnabled(files.length != 0);

        fireFontListUpdate();
    }

    public int getNumItems() {
        return fontList.getModel().getSize();
    }

    public int getCurrentItemNum() {
        return fontList.getSelectedIndex();
    }

    public FontFile getItem(int itemNumber) {
        ListModel<FontFile> model = fontList.getModel();
        if (itemNumber >= 0 && itemNumber < model.getSize()) {
            return model.getElementAt(itemNumber);
        } else {
            return null;
        }
    }

    protected void selectItem(int itemPos) {
        ListModel<FontFile> model = fontList.getModel();
        if (model.getSize() != 0) {
            int selected = getCurrentItemNum();
            if (itemPos != selected) {
                fontList.setSelectedIndex(itemPos);
                ScrollTools.scrollVerticallyTo(fontScrollPane, itemPos, model.getSize());
            }
        }

        fireFontChange();
    }
}
