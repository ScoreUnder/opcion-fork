package FontViewer.components;

import FontViewer.FontFile;

public interface ListPanel {
    int getNumItems();
    
    FontFile getItem(int itemNumber);

    FontFile getCurrentItem();
    
    int getCurrentItemNum();
    
    void selectItem(FontFile font);
}