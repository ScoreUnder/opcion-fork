package FontViewer.components;

import FontViewer.FontFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractListPanel extends JPanel implements ListPanel {
    private List<BiConsumer<FontFile, Integer>> fontChangeListeners = new ArrayList<>();
    private List<Runnable> fontListUpdateListeners = new ArrayList<>();

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

    protected void fireFontChange() {
        var font = getCurrentItem();
        int index = getCurrentItemNum();
        for (var listener : fontChangeListeners)
            listener.accept(font, index);
    }

    public void addFontChangeListener(BiConsumer<FontFile, Integer> listener) {
        fontChangeListeners.add(listener);
    }

    protected void fireFontListUpdate() {
        for (var listener : fontListUpdateListeners)
            listener.run();
    }

    public void addFontListUpdateListener(Runnable listener) {
        fontListUpdateListeners.add(listener);
    }
}
