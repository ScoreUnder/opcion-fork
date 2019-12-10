package FontViewer.components;
import FontViewer.FontFile;
import FontViewer.windows.*;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

public class SampleTextPanel extends JPanel {
    private final int BOLD = 0;
    private final int ITALIC = 1;
    private final int UNDERLINE = 2;
    
    private Vector<Integer> fontSizes;
    private static final String SAMPLE_TEXT = ResourceBundle.getBundle("Opcion").getString("defaultSampleText");
    
    private String fontName = "Default";
    private int fontSize = 20;
    
    private boolean[] fontProperties = {false, false, false};
    
    private MainWindow mw;
    
    public SampleTextPanel(MainWindow mw, int[] sizes) {
        this.mw = mw;

        initFontSizes(sizes);
        initComponents();
        
        // Set default settings
        fontSizeComboBox.setSelectedItem(fontSizes.get(9));
    }
    
    private void initFontSizes(int[] sizes) {
        Vector<Integer> vector = new Vector<>();
        for (int it : sizes) {
            vector.add(it);
        }
        fontSizes = vector;
    }

    public void addDocumentListener(DocumentListener listener) {
        previewTextPane.getDocument().addDocumentListener(listener);
    }

    public void setCurrentFont(FontFile font) {
        fontName = font.getName();
        String fontLocation = font.getLocation();

        if (!fontLocation.equals("System Font")) {
            try {
                fontName = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fontLocation + File.separator + fontName)).getName();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading font.", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        updateDisplay();
    }
    
    public void setFavButtonAction(String action) {
        addFavouritesButton.setText(action);
    }
    
    private void updateDisplay() {
        // Set font name
        selectedFontNameLabel.setText(fontName);
        selectedFontNameLabel.setToolTipText(fontName);
        
        // Set document style
        Style s = previewTextPane.getStyle("default");
        StyleConstants.setFontFamily(s, fontName);
        StyleConstants.setFontSize(s, fontSize);
        StyleConstants.setBold(s, fontProperties[BOLD]);
        StyleConstants.setItalic(s, fontProperties[ITALIC]);
        StyleConstants.setUnderline(s, fontProperties[UNDERLINE]);
        
        // Update text pane
        previewTextPane.setParagraphAttributes(s, true);
    }
    
    private void initComponents() {
        previewTextPane = new AATextPane();
        addFavouritesButton = new JButton();
        fontSizeComboBox = new JComboBox<>(fontSizes);
        selectedFontNameLabel = new JLabel();

        setLayout(new BorderLayout(2, 2));

        setBorder(new TitledBorder("Sample Text"));
        JScrollPane previewTextScrollPane = new JScrollPane();
        previewTextScrollPane.setBorder(null);
        previewTextPane.setBorder(null);
        previewTextPane.setText(SAMPLE_TEXT);
        previewTextScrollPane.setViewportView(previewTextPane);

        add(previewTextScrollPane, BorderLayout.CENTER);

        JToggleButton boldButton = new JToggleButton("B");
        boldButton.setToolTipText("Bold");
        boldButton.setMargin(new Insets(2, 2, 2, 2));
        boldButton.addActionListener(evt -> {
            fontProperties[BOLD] = boldButton.isSelected();
            updateDisplay();
        });

        JPanel fontPropertiesPanel = new JPanel(new java.awt.GridLayout(1, 3, 2, 0));
        fontPropertiesPanel.add(boldButton);

        JToggleButton italicsButton = new JToggleButton("I");
        italicsButton.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
        italicsButton.setToolTipText("Italic");
        italicsButton.setMargin(new Insets(2, 2, 2, 2));
        italicsButton.addActionListener(evt -> {
            fontProperties[ITALIC] = italicsButton.isSelected();
            updateDisplay();
        });

        fontPropertiesPanel.add(italicsButton);

        JToggleButton underlineButton = new JToggleButton("U");
        underlineButton.setToolTipText("Underline");
        underlineButton.setMargin(new Insets(2, 2, 2, 2));
        underlineButton.addActionListener(evt -> {
            fontProperties[UNDERLINE] = underlineButton.isSelected();
            updateDisplay();
        });

        fontPropertiesPanel.add(underlineButton);

        JPanel optionsPanel = new JPanel(new BorderLayout(2, 2));
        optionsPanel.add(fontPropertiesPanel, BorderLayout.WEST);

        addFavouritesButton.setText("Add to Favourites");
        addFavouritesButton.setMargin(new Insets(2, 2, 2, 2));
        addFavouritesButton.addActionListener(evt -> {
            if (addFavouritesButton.getText().charAt(0) == 'A') {
                mw.addToFav();
            } else {
                mw.removeFromFav();
            }
        });

        optionsPanel.add(addFavouritesButton, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new java.awt.GridLayout(2, 0, 0, 2));
        topPanel.add(optionsPanel);

        Font dialogFont = new Font("Dialog", Font.PLAIN, 12);
        JLabel fontSizeLabel = new JLabel("Font Size: ", SwingConstants.RIGHT);
        fontSizeLabel.setFont(dialogFont);
        JPanel fontSizePanel = new JPanel(new java.awt.FlowLayout(FlowLayout.LEFT));
        fontSizePanel.add(fontSizeLabel);

        fontSizeComboBox.setFont(dialogFont);
        fontSizeComboBox.addItemListener(evt -> {
            fontSize = fontSizes.get(fontSizeComboBox.getSelectedIndex());
            updateDisplay();
            mw.setFontSize(fontSize);
        });

        fontSizePanel.add(fontSizeComboBox);

        JPanel fontInfoPanel = new JPanel(new BorderLayout(2, 2));
        fontInfoPanel.add(fontSizePanel, BorderLayout.EAST);

        selectedFontNameLabel.setFont(dialogFont);
        selectedFontNameLabel.setText("No font selected");
        fontInfoPanel.add(selectedFontNameLabel, BorderLayout.CENTER);

        JLabel fontNameLabel = new JLabel("Font Name: ");
        fontNameLabel.setFont(dialogFont);
        fontInfoPanel.add(fontNameLabel, BorderLayout.WEST);

        topPanel.add(fontInfoPanel);

        add(topPanel, BorderLayout.NORTH);

    }

    private JButton addFavouritesButton;
    private JLabel selectedFontNameLabel;
    private JComboBox<Integer> fontSizeComboBox;
    private JTextPane previewTextPane;
}