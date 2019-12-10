package FontViewer.components;

import FontViewer.FontFile;

import javax.swing.*;

public abstract class AbstractListPanel extends JPanel implements ListPanel {
    @Override
    public FontFile getCurrentItem() {
        return getItem(getCurrentItemNum());
    }

    @Override
    public void selectItem(FontFile font) {
        selectItem(getItemIndex(font));
    }

    public int getItemIndex(FontFile font) {
        for (int i = 0; i < getNumItems(); i++) {
            if (getItem(i).equals(font)) {
                return i;
            }
        }

        return -1;
    }

    protected abstract void selectItem(int pos);
}
