package FontViewer.components;

import FontViewer.windows.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FavouriteFontsPanel extends AbstractListPanel {
    private static final int NOT_FOUND = -1;
    private static final int COL_FONTNAME = 0;

    private MainWindow mw;
    private DefaultTableModel tm;
    private int sortCol;
    private boolean sortAscend;
    private JTable favouritesTable;

    public FavouriteFontsPanel(MainWindow mw) {
        initComponents();

        // Init global variables
        this.mw = mw;
        tm = (DefaultTableModel) favouritesTable.getModel();
        sortCol = COL_FONTNAME;
        sortAscend = true;
        favouritesTable.setAutoCreateColumnsFromModel(false);
    }

    public boolean addToFav(String name, String loc) {
        // If font not already in favs, add to favs
        if (getItemNumber(name, loc) == NOT_FOUND) {
            tm.addRow(new Object[]{name, loc});
            return true;
        } else {
            return false;
        }
    }

    public boolean removeFromFav(String name, String loc) {
        boolean removed = false;
        int p = getItemNumber(name, loc);

        // Remove item
        if (p != NOT_FOUND) {
            tm.removeRow(p);
            removed = true;
        }

        // Select next/prev item
        p = min(getNumItems() - 1, p);
        setCurrentItem(p, true);

        // Update display
        mw.updateDisplay();

        return removed;
    }

    public int getItemNumber(String name, String loc) {
        int itemNum = -1;
        Object[] data = tm.getDataVector().toArray();
        String font = String.format("[%s, %s]", name, loc);

        // Check selected font is already in Favourites
        for (int i = 0; i < data.length; i++) {
            if (data[i].toString().equals(font)) {
                itemNum = i;
                break;
            }
        }

        return itemNum;
    }

    public String[] getItem(int itemNumber) {
        String[] s = new String[3];

        // Assign current item to s[]
        if ((itemNumber >= 0) && (itemNumber < tm.getRowCount())) {
            s[0] = tm.getValueAt(itemNumber, 0).toString();
            s[1] = tm.getValueAt(itemNumber, 1).toString();
            s[2] = String.valueOf(itemNumber);
        }

        return s;
    }

    public int getNumItems() {
        return tm.getRowCount();
    }

    public String[] getCurrentItem() {
        // Get current item
        String[] s = super.getCurrentItem();

        // Sort table
        sortAllRowsBy(sortCol, sortAscend);

        // Select selected item
        favouritesTable.changeSelection(getItemNumber(s[0], s[1]), 0, false, false);

        return s;
    }

    public int getCurrentItemNum() {
        return favouritesTable.getSelectedRow();
    }

    protected void setCurrentItem(int p, boolean updateUI) {
        if (updateUI)
            favouritesTable.changeSelection(p, 0, false, false);

        String[] s = getCurrentItem();
        try {
            if (p >= 0)
                mw.setCurrentFont(s[0], s[1], Integer.parseInt(s[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectItem(String name, String loc) {
        setCurrentItem(getItemNumber(name, loc), false);
    }

    // sortAllRowsBy taken from:
    //      http://javaalmanac.com/egs/javax.swing.table/Sorter.html
    public void sortAllRowsBy(int colIndex, boolean ascending) {
        Vector<Vector> data = tm.getDataVector();
        data.sort(new ColumnSorter(colIndex, ascending));
        tm.fireTableStructureChanged();
    }

    // ColumnSorter taken from:
    //      http://javaalmanac.com/egs/javax.swing.table/Sorter.html
    public static class ColumnSorter implements Comparator<Vector> {
        private int colIndex;
        private boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        public int compare(Vector a, Vector b) {
            String o1 = (String) a.get(colIndex);
            String o2 = (String) b.get(colIndex);

            // Treat nulls like empty strings
            if (o1 == null) o1 = "";
            if (o2 == null) o2 = "";

            // Sort empty strings so they appear last, regardless of sort order
            if (o1.isEmpty() && o2.isEmpty()) return 0;
            if (o1.isEmpty()) return 1;
            if (o2.isEmpty()) return -1;
            if (ascending) return o1.compareToIgnoreCase(o2);
            return o2.compareToIgnoreCase(o1);
        }
    }

    private void initComponents() {
        favouritesTable = new JTable();

        setLayout(new BorderLayout());

        favouritesTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Font Name", "Location"}
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        favouritesTable.setDoubleBuffered(true);
        favouritesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                setCurrentItem(favouritesTable.getSelectedRow(), false);
            }
        });
        favouritesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setCurrentItem(favouritesTable.getSelectedRow(), false);
            }
        });

        JScrollPane favouritesScrollPane = new JScrollPane();
        favouritesScrollPane.setBorder(null);
        favouritesScrollPane.setViewportView(favouritesTable);
        add(favouritesScrollPane, BorderLayout.CENTER);
    }
}