package FontViewer.components;

import FontViewer.FontFile;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.min;

public class FavouriteFontsPanel extends AbstractListPanel {
    private static final int COL_FONTNAME = 0;

    private final FontTableModel tm;
    private final int sortCol;
    private final boolean sortAscend;
    private JTable favouritesTable;

    public FavouriteFontsPanel() {
        tm = new FontTableModel();
        initComponents();

        // Init global variables
        sortCol = COL_FONTNAME;
        sortAscend = true;
    }

    public boolean addToFav(FontFile font) {
        // If font not already in favs, add to favs
        if (getItemIndex(font) == -1) {
            tm.addRow(font);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeFromFav(FontFile font) {
        boolean removed = false;
        int p = getItemIndex(font);

        // Remove item
        if (p != -1) {
            tm.removeRow(p);
            removed = true;
        }

        // Select next/prev item
        p = min(getNumItems() - 1, p);
        selectItem(p);

        // Update display
        fireFontListUpdate();

        return removed;
    }

    public FontFile getItem(int itemNumber) {
        if (itemNumber >= 0 && itemNumber < tm.getRowCount()) {
            return tm.getRow(itemNumber);
        }
        return null;
    }

    public int getNumItems() {
        return tm.getRowCount();
    }

    public FontFile getCurrentItem() {
        // Get current item
        FontFile s = super.getCurrentItem();

        // TODO: why is this done here?
        if (s != null) {
            // Sort table
            sortAllRowsBy(sortCol, sortAscend);

            // Select selected item
            favouritesTable.changeSelection(getItemIndex(s), 0, false, false);
        }

        return s;
    }

    public int getCurrentItemNum() {
        return favouritesTable.getSelectedRow();
    }

    protected void selectItem(int p) {
        int currItem = getCurrentItemNum();
        if (p != currItem)
            favouritesTable.changeSelection(p, 0, false, false);

        fireFontChange();
    }

    private void saveToFile(File f) {
        try {
            // Init stuff
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            // Sort favourites
            sortAllRowsBy(0, true);

            // Write favourites
            for (int i = 0; i < getNumItems(); i++) {
                FontFile s = getItem(i);
                bw.write(s.getLocation() + File.separator + s.getName());
                bw.newLine();
            }
            bw.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Cannot write to file.", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortAllRowsBy(int colIndex, boolean ascending) {
        tm.sortBy(colIndex, ascending);
    }

    public static class CaseInsensitiveOrder implements Comparator<String> {
        private final boolean ascending;

        CaseInsensitiveOrder(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(String a, String b) {
            String o1 = a;
            String o2 = b;

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

        favouritesTable.setModel(tm);
        favouritesTable.setDoubleBuffered(true);
        favouritesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                selectItem(favouritesTable.getSelectedRow());
            }
        });
        favouritesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                selectItem(favouritesTable.getSelectedRow());
            }
        });
        favouritesTable.setAutoCreateColumnsFromModel(false);

        JScrollPane favouritesScrollPane = new JScrollPane();
        favouritesScrollPane.setBorder(null);
        favouritesScrollPane.setViewportView(favouritesTable);
        add(favouritesScrollPane, BorderLayout.CENTER);

        JButton saveFaves = new JButton("Save to...");
        saveFaves.setMnemonic('s');
        saveFaves.addActionListener(evt -> {
            if (getNumItems() <= 0) {
                JOptionPane.showMessageDialog(this, "There are no favourite fonts to save.", "Error!", JOptionPane.ERROR_MESSAGE);
            } else {
                // Create new file chooser
                JFileChooser fc = new JFileChooser(new File(""));
                // Show save dialog; this method does not return until the dialog is closed
                fc.showSaveDialog(this);
                if (fc.getSelectedFile() != null) {
                    File f = fc.getSelectedFile();
                    if (!f.exists() || JOptionPane.showConfirmDialog(this, String.format("The file %s already exists, do you\nwant to overwrite?", f.getName()), "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        saveToFile(f);
                    }
                }
            }
        });
        add(saveFaves, BorderLayout.SOUTH);
    }

    private static class FontTableModel extends AbstractTableModel {
        private final List<FontFile> fonts = new ArrayList<>();

        @Override
        public int getRowCount() {
            return fonts.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0)
                return "Font Name";
            else
                return "Location";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            FontFile row = getRow(rowIndex);
            if (columnIndex == 0) return row.getName();
            else return row.getLocation();
        }

        FontFile getRow(int rowIndex) {
            return fonts.get(rowIndex);
        }

        void addRow(FontFile font) {
            fonts.add(font);
            fireTableDataChanged();
        }

        void removeRow(int index) {
            fonts.remove(index);
            fireTableRowsDeleted(index, index);
        }

        void sortBy(int colIndex, boolean ascending) {
            fonts.sort(new ColumnComparatorAdaptor(colIndex, new CaseInsensitiveOrder(ascending)));
            fireTableDataChanged();
        }

        @Override
        public void setValueAt(Object o, int rowIndex, int columnIndex) {
            throw new UnsupportedOperationException("Single-cell editing is not supported");
        }

        private static class ColumnComparatorAdaptor implements Comparator<FontFile> {
            private final int columnIndex;
            private final Comparator<String> columnComparator;

            ColumnComparatorAdaptor(int columnIndex, Comparator<String> columnComparator) {
                this.columnIndex = columnIndex;
                this.columnComparator = columnComparator;
            }

            @Override
            public int compare(FontFile a, FontFile b) {
                if (columnIndex == 0)
                    return columnComparator.compare(a.getName(), b.getName());
                else
                    return columnComparator.compare(a.getLocation(), b.getLocation());
            }
        }
    }
}