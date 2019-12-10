package FontViewer.components;

import FontViewer.FontFile;

import javax.swing.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class AbstractListPanel extends JPanel implements ListPanel {
    public void selectNext() {
        int i = getCurrentItemNum();
        i = max(0, min(getNumItems() - 1, i + 1));
        setCurrentItem(i, true);
    }

    public void selectPrev() {
        setCurrentItem(max(0, getCurrentItemNum() - 1), true);
    }

    @Override
    public FontFile getCurrentItem() {
        return getItem(getCurrentItemNum());
    }

    protected abstract void setCurrentItem(int pos, boolean updateUI);
}
