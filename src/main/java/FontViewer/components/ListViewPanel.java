package FontViewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ListViewPanel extends JPanel {
    private static final int NOT_FOUND = -1;
    private static final int COL_FONTNAME = 0;
    private static final int COL_FONTLOC = 1;

    private int rows;
    private int columns;
    private int position;
    private int fsize;

    private String sampleText;
    private ListPanel view;
    private FavouriteFontsPanel ffp;
    private AAToggleButton selectedButton;

    public ListViewPanel(JPanel ffp, int rows, int columns) {
        this.ffp = (FavouriteFontsPanel) ffp;
        this.rows = rows;
        this.columns = columns;

        position = 0;
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

    public void setView(JPanel p) {
        view = (ListPanel) p;
        if (view.getNumItems() == 0) {
            noDisplay();
        } else {
            position = max(0, view.getCurrentItemNum());
            updateDisplay();
        }
    }

    public void setPosition(int p) {
        if ((p < position) || ((position + rows) <= p)) {
            position = p;
            updateDisplay();
        }

        // Highlight selected font
        if (selectedButton != null)
            selectedButton.setBackground(Color.WHITE);
        selectedButton = (AAToggleButton) listPanel.getComponent(p - position);
        selectedButton.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("Table.selectionBackground"));

        // Scroll to selected font
        int spos = (p - position) * (listScrollPane.getVerticalScrollBar().getMaximum() / rows);
        spos -= (listScrollPane.getSize().height / 2);
        listScrollPane.getVerticalScrollBar().setValue(spos);
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
        listPanel.setVisible(false);

        int items = view.getNumItems();
        // Set when to stop drawing
        int last = min(position + rows, items);

        // Draw everything if less items than rows
        if (view.getNumItems() <= rows) {
            position = 0;
        }

        // Update drawing status
        navInfoLabel.setText(String.format("Font %d~%d of %d", position + 1, last, items));

        // Draw buttons
        for (int i = position; i < last; i++) {
            // Font item
            String[] font = view.getItem(i);

            // Assign font to variable, or create font if working with files
            String fontLoc = font[COL_FONTLOC];
            String fontName = font[COL_FONTNAME];
            Font f = getFontFromSpec(fontLoc, fontName);

            AAToggleButton tb;
            if (f == null) {
                tb = new AAToggleButton("Font could not be loaded.", "Font could not be loaded.", "N/A");
            } else {
                // Set up toggle buttons
                tb = new AAToggleButton(sampleText, fontName, fontLoc);
                tb.setBackground(Color.WHITE);
                tb.setFont(f.deriveFont(Font.PLAIN, (float) fsize));

                if (!(view instanceof FavouriteFontsPanel)) {
                    /* When in non-fav tab */
                    // Toggle button if this font has been selected before
                    if (ffp.getItemNumber(fontName, fontLoc) != NOT_FOUND) {
                        tb.setSelected(true);
                    }

                    // When a button is selected add the selected font to favs
                    // When a button is unselected remove the font from favs
                    tb.addActionListener(evt -> {
                        AAToggleButton source = (AAToggleButton) evt.getSource();
                        String fName = source.getFName();
                        String fLoc = source.getFLoc();
                        view.selectItem(fName, fLoc);
                        if (source.isSelected()) {
                            ffp.addToFav(fName, fLoc);
                        } else {
                            ffp.removeFromFav(fName, fLoc);
                        }
                    });
                } else {
                    /* When in fav tab */
                    // When a button is selected remove the font favs
                    tb.addActionListener(evt -> {
                        AAToggleButton source = (AAToggleButton) evt.getSource();
                        ffp.removeFromFav(source.getFName(), source.getFLoc());
                    });
                }
            }

            listPanel.add(tb);
        }

        listPanel.setVisible(true);
    }

    private Font getFontFromSpec(String fontLoc, String fontName) {
        Font f;
        if (fontLoc.equals("System Font")) {
            f = new Font(fontName, Font.PLAIN, fsize);
        } else {
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fontLoc + File.separator + fontName));
            } catch (Exception e) {
                f = null;
            }
        }
        return f;
    }

    public void nextPage() {
        // Change position
        if ((position + rows) < view.getNumItems()) {
            position += rows;
            updateDisplay();
        }
    }

    public void prevPage() {
        // Change position
        if ((position - rows) >= 0) {
            position -= rows;
            updateDisplay();
        } else if (((position - rows) < 0) && (position != 0)) {
            position = 0;
            updateDisplay();
        }
    }

    private void initComponents() {
        fontsPerPageTextField = new JTextField();
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

        fontsPerPageTextField.setText(String.valueOf(rows));
        fontsPerPageTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        rows = Integer.parseInt(fontsPerPageTextField.getText());
                        listPanel.setLayout(new GridLayout(rows, columns, 2, 0));
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

    private JTextField fontsPerPageTextField;
    private JPanel listPanel;
    private JScrollPane listScrollPane;
    private JLabel navInfoLabel;
}