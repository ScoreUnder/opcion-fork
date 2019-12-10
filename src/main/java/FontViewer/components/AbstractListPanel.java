package FontViewer.components;

import FontViewer.FontFile;

import javax.swing.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class AbstractListPanel extends JPanel implements ListPanel {
    public void selectNext() {
        int i = getCurrentItemNum();
        i = max(0, min(getNumItems() - 1, i + 1));
        selectItem(i);
    }

    public void selectPrev() {
        selectItem(max(0, getCurrentItemNum() - 1));
    }

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
