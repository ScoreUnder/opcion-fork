package FontViewer.windows;

import FontViewer.FontFile;
import FontViewer.components.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainWindow extends javax.swing.JFrame {
    // Constants
    private final int[] FONT_SIZES = {6, 8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32, 36, 42, 48, 56, 72, 84};
    private static final String ADD = "Add to Favourites";
    private static final String REM = "Remove from Favourites";

    private FontFile currentFont;

    // List view properties
    private static final int ROWS = 10;
    private static final int COLUMNS = 1;

    public MainWindow() {
        initComponents();

        // Set current panel
        changeCurrentPanel(systemFontsPanel);
    }

    public void addToFav() {
        addToFav(currentFont);
    }

    public void removeFromFav() {
        removeFromFav(currentFont);
    }

    private void addToFav(FontFile font) {
        if (!favouriteFontsPanel.addToFav(font)) {
            JOptionPane.showMessageDialog(this, "Font already in favourites.", "Error!", JOptionPane.ERROR_MESSAGE);
        } else {
            listViewPanel.updateDisplay();
        }
    }

    private void removeFromFav(FontFile font) {
        if (!favouriteFontsPanel.removeFromFav(font)) {
            JOptionPane.showMessageDialog(this, "Font not found in favourites.", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setCurrentFont(FontFile font, int position) {
        if (position < 0) return;  // If nothing is selected, don't set anything
        assert font != null;

        currentFont = font;
        listViewPanel.setPosition(position);
        sampleTextPanel.setCurrentFont(font);
    }

    public void setFontSize(int s) {
        if (listViewPanel != null) {
            listViewPanel.setFontSize(s);
        }
    }

    private void initComponents() {
        systemFontsPanel = new SystemFontsPanel();
        favouriteFontsPanel = new FavouriteFontsPanel();
        var otherFontsPanel = new OtherFontsPanel();
        sampleTextPanel = new SampleTextPanel(this, FONT_SIZES);
        listViewPanel = new ListViewPanel(favouriteFontsPanel, ROWS, COLUMNS);

        for (var panel : Arrays.asList(systemFontsPanel, favouriteFontsPanel, otherFontsPanel)) {
            panel.addFontChangeListener(this::setCurrentFont);
            panel.addFontListUpdateListener(listViewPanel::updateDisplay);
        }

        getContentPane().setLayout(new java.awt.BorderLayout(0, 5));

        setTitle("Opcion Font Viewer - v" + ResourceBundle.getBundle("Opcion").getString("version"));
        setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));
        setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("icons/IconSmall.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("System Fonts", systemFontsPanel);
        tabbedPane.addTab("Other Fonts", otherFontsPanel);
        tabbedPane.addTab("Favourite Fonts", favouriteFontsPanel);
        tabbedPane.addChangeListener(evt -> changeCurrentPanel((ListPanel) tabbedPane.getSelectedComponent()));

        sampleTextPanel.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                changedUpdate(documentEvent);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                changedUpdate(documentEvent);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                Document doc = documentEvent.getDocument();
                try {
                    listViewPanel.setSampleText(doc.getText(0, doc.getLength()));
                } catch (BadLocationException ignored) {
                }
            }
        });


        JSplitPane quickViewSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, sampleTextPanel);
        quickViewSplitPane.setResizeWeight(0.5);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, quickViewSplitPane, listViewPanel);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setOneTouchExpandable(true);
        // Allow manual collapsing of both sides
        mainSplitPane.getLeftComponent().setMinimumSize(new Dimension());
        mainSplitPane.getRightComponent().setMinimumSize(new Dimension());

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void changeCurrentPanel(ListPanel newPanel) {
        // Update list
        listViewPanel.setView((JPanel) newPanel);

        // Update sampleTextPanel
        FontFile s = newPanel.getCurrentItem();
        if (s != null) {
            setCurrentFont(s, newPanel.getCurrentItemNum());
        }

        // Set fav button action
        if (newPanel instanceof FavouriteFontsPanel) {
            sampleTextPanel.setFavButtonAction(REM);
        } else {
            sampleTextPanel.setFavButtonAction(ADD);
        }
    }

    private FavouriteFontsPanel favouriteFontsPanel;
    private ListViewPanel listViewPanel;
    private SampleTextPanel sampleTextPanel;
    private SystemFontsPanel systemFontsPanel;
}