package opcion.components;

import opcion.FontFile;
import opcion.util.ScrollTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ListViewPanel extends JPanel {
    private int rows;
    private final int columns;
    private int pageStart;
    private int position;
    private int fsize;

    private String sampleText;
    private ListPanel view;
    private final FavouriteFontsPanel ffp;
    private AAToggleButton selectedButton;

    public ListViewPanel(JPanel ffp, int rows, int columns) {
        this.ffp = (FavouriteFontsPanel) ffp;
        this.rows = rows;
        this.columns = columns;

        pageStart = 0;
        fsize = 20;
        sampleText = java.util.ResourceBundle.getBundle("Opcion").getString("defaultSampleText");

        initComponents();
        listScrollPane.getVerticalScrollBar().setUnitIncrement(100);
    }

    public void setSampleText(String s) {
        sampleText = s;
        updateDisplay();
    }

    public void setFontSize(int s) {
        fsize = s;
        updateDisplay();
    }

    public void setView(ListPanel panel) {
        view = panel;
        if (panel.getNumItems() == 0) {
            noDisplay();
        } else {
            pageStart = max(0, panel.getCurrentItemNum());
            updateDisplay();
        }
    }

    public void setPosition(int position) {
        this.position = position;
        if (position < pageStart || position >= pageStart + rows) {
            pageStart = position / rows * rows;  // Scroll to whole pages only
            updateDisplay();
        }

        // Highlight selected font
        if (selectedButton != null)
            selectedButton.setBackground(Color.WHITE);
        selectedButton = (AAToggleButton) listPanel.getComponent(position - pageStart);
        selectedButton.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Table.selectionBackground"));

        // Scroll to selected font
        ScrollTools.scrollVerticallyTo(listScrollPane, position - pageStart, rows);
    }

    private void noDisplay() {
        // Clear list
        listPanel.removeAll();
        listPanel.setVisible(false);

        // Update drawing status
        navInfoLabel.setText("Font 0~0 of 0");

        // Show message
        listPanel.add(new JLabel("There are no fonts to display.", JLabel.CENTER));
        listPanel.setVisible(true);
    }

    public void updateDisplay() {
        // Clear list
        listPanel.removeAll();
        listPanel.setIgnoreRepaint(true);

        int items = view.getNumItems();
        // Set when to stop drawing
        int last = min(pageStart + rows, items);

        // Update drawing status
        navInfoLabel.setText(String.format("Font %d~%d of %d", pageStart + 1, last, items));

        // Draw buttons
        for (int i = pageStart; i < last; i++) {
            listPanel.add(createFontButton(view.getItem(i)));
        }

        listPanel.setIgnoreRepaint(false);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private AAToggleButton createFontButton(FontFile font) {
        // Load font with AWT if possible
        Font f = getFontFromSpec(font);

        AAToggleButton tb;
        if (f == null) {
            tb = new AAToggleButton("Font could not be loaded.", font);
        } else {
            tb = new AAToggleButton(sampleText, font);
            tb.setBackground(Color.WHITE);
            tb.setFont(f.deriveFont(Font.PLAIN, (float) fsize));

            if (view instanceof FavouriteFontsPanel) {
                /* When in fav tab */
                // When a button is selected remove the font from favs
                tb.addActionListener(evt -> {
                    AAToggleButton source = (AAToggleButton) evt.getSource();
                    ffp.removeFromFav(source.getFontFile());
                });
            } else {
                /* When in non-fav tab */
                // Toggle button if this font has been selected before
                if (ffp.getItemIndex(font) != -1) {
                    tb.setSelected(true);
                }

                // When a button is selected add the selected font to favs
                // When a button is unselected remove the font from favs
                tb.addActionListener(evt -> {
                    AAToggleButton source = (AAToggleButton) evt.getSource();
                    FontFile fontFileSelected = source.getFontFile();
                    view.selectItem(fontFileSelected);
                    if (source.isSelected()) {
                        ffp.addToFav(fontFileSelected);
                    } else {
                        ffp.removeFromFav(fontFileSelected);
                    }
                });
            }
        }
        return tb;
    }

    private Font getFontFromSpec(FontFile fontFile) {
        Font f;
        if (fontFile.getLocation().equals("System Font")) {
            f = new Font(fontFile.getName(), Font.PLAIN, fsize);
        } else {
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fontFile.getLocation() + File.separator + fontFile.getName()));
            } catch (Exception e) {
                f = null;
            }
        }
        return f;
    }

    private void nextPage() {
        // Change position
        if ((pageStart + rows) < view.getNumItems()) {
            pageStart += rows;
            updateDisplay();
        }
    }

    private void prevPage() {
        // Change position
        if ((pageStart - rows) >= 0) {
            pageStart -= rows;
            updateDisplay();
        } else if (((pageStart - rows) < 0) && (pageStart != 0)) {
            pageStart = 0;
            updateDisplay();
        }
    }

    private void initComponents() {
        listScrollPane = new JScrollPane();
        listPanel = new JPanel();
        JPanel navigationPanel = new JPanel();
        navInfoLabel = new JLabel();

        setLayout(new BorderLayout(2, 2));
        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(1, 0));

        JLabel fontsPerPageLabel = new JLabel("Fonts/Page", SwingConstants.CENTER);
        fontsPerPageLabel.setToolTipText("Fonts to show per page");
        optionsPanel.add(fontsPerPageLabel);

        var fontsPerPageTextField = new JTextField(String.valueOf(rows));
        fontsPerPageTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        rows = Integer.parseInt(fontsPerPageTextField.getText());
                        listPanel.setLayout(new GridLayout(rows, columns, 2, 0));
                        pageStart = position / rows * rows;
                        updateDisplay();
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(ListViewPanel.this, "Not a valid number.", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        optionsPanel.add(fontsPerPageTextField);

        add(optionsPanel, BorderLayout.NORTH);

        listScrollPane.setBorder(null);
        listScrollPane.setMinimumSize(new Dimension(300, 22));
        listPanel.setLayout(new GridLayout(1, 1));

        listPanel.setMinimumSize(new Dimension(300, 0));
        listPanel.setLayout(new GridLayout(rows, columns, 2, 0));
        listScrollPane.setViewportView(listPanel);

        add(listScrollPane, BorderLayout.CENTER);

        navigationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton prevButton = new JButton("<");
        prevButton.setToolTipText("Previous page");
        prevButton.setFocusPainted(false);
        prevButton.addActionListener(evt -> prevPage());

        navigationPanel.add(prevButton);

        navInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navInfoLabel.setText("Font 0~0 of 0");
        navigationPanel.add(navInfoLabel);

        JButton nextButton = new JButton(">");
        nextButton.setToolTipText("Next page");
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(evt -> nextPage());

        navigationPanel.add(nextButton);

        add(navigationPanel, BorderLayout.SOUTH);
    }

    private JPanel listPanel;
    private JScrollPane listScrollPane;
    private JLabel navInfoLabel;
}