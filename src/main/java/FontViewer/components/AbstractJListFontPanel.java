package FontViewer.components;

import FontViewer.FontFile;
import FontViewer.windows.MainWindow;

import javax.swing.*;
import java.awt.*;

public class AbstractJListFontPanel extends AbstractListPanel {
    protected final JList<FontFile> fontList = new JList<>();
    private final JScrollPane fontScrollPane = new JScrollPane();
    protected final MainWindow mw;

    protected AbstractJListFontPanel(MainWindow mw) {
        this.mw = mw;

        setLayout(new BorderLayout());
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(evt -> selectItem(fontList.getSelectedIndex()));
        fontScrollPane.setViewportView(fontList);
        add(fontScrollPane, BorderLayout.CENTER);
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
                int spos = itemPos * (fontScrollPane.getVerticalScrollBar().getMaximum() / model.getSize());
                spos -= (fontScrollPane.getSize().height / 2);
                fontScrollPane.getVerticalScrollBar().setValue(spos);
            }

            if (itemPos >= 0)
                mw.setCurrentFont(model.getElementAt(itemPos), itemPos);
        }
    }
}
