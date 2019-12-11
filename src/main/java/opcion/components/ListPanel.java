package opcion.components;

import opcion.FontFile;

public interface ListPanel {
    int getNumItems();
    
    FontFile getItem(int itemNumber);

    FontFile getCurrentItem();
    
    int getCurrentItemNum();
    
    void selectItem(FontFile font);
}