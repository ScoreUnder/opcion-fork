package FontViewer.components;

public interface ListPanel {
    int getNumItems();
    
    String[] getItem(int itemNumber);
    
    String[] getCurrentItem();
    
    int getCurrentItemNum();
    
    void selectItem(String name, String loc);
    
    void selectNext();
    
    void selectPrev();
}