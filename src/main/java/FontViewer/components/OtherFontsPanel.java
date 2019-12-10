package FontViewer.components;

import FontViewer.FontFile;
import FontViewer.windows.MainWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;

public class OtherFontsPanel extends AbstractListPanel {
    private File currentDirectory;
    private MainWindow mw;
    private JTextField locationTextField;
    private JList<FontFile> otherFontsList;
    private JScrollPane otherFontsScrollPane;

    public OtherFontsPanel(MainWindow mw) {
        this.mw = mw;

        initComponents();
    }

    public FontFile getItem(int itemNumber) {
        ListModel<FontFile> model = otherFontsList.getModel();
        if (itemNumber >= 0 && itemNumber < model.getSize()) {
            return model.getElementAt(itemNumber);
        } else {
            return null;
        }
    }

    public int getNumItems() {
        return otherFontsList.getModel().getSize();
    }

    public int getCurrentItemNum() {
        return otherFontsList.getSelectedIndex();
    }

    private static boolean isFontFilename(String path) {
        String pathUpper = path.toUpperCase();
        return pathUpper.endsWith(".TTF") || pathUpper.endsWith(".OTF");
    }

    private void updateDisplay() {
        String[] filenames = currentDirectory.list((dir, name) -> isFontFilename(name));
        assert filenames != null;
        Arrays.sort(filenames, String.CASE_INSENSITIVE_ORDER);

        FontFile[] files = Arrays.stream(filenames).map(it -> new FontFile(it, currentDirectory.toString())).toArray(FontFile[]::new);
        otherFontsList.setListData(files);

        if (files.length == 0) {
            otherFontsList.setEnabled(false);
        } else {
            otherFontsList.setEnabled(true);
        }

        mw.updateDisplay();
    }

    protected void selectItem(int itemPos) {
        ListModel<FontFile> model = otherFontsList.getModel();
        if (model.getSize() != 0) {
            int selected = getCurrentItemNum();
            if (itemPos != selected) {
                otherFontsList.setSelectedIndex(itemPos);
                int spos = itemPos * (otherFontsScrollPane.getVerticalScrollBar().getMaximum() / model.getSize());
                spos -= (otherFontsScrollPane.getSize().height / 2);
                otherFontsScrollPane.getVerticalScrollBar().setValue(spos);
            }

            if (itemPos >= 0)
                mw.setCurrentFont(model.getElementAt(itemPos), itemPos);
        }
    }

    private void initComponents() {
        JButton browseButton = new JButton();

        setLayout(new java.awt.BorderLayout(2, 2));

        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new java.awt.BorderLayout(4, 0));
        locationPanel.add(new JLabel("Location:"), java.awt.BorderLayout.WEST);

        locationTextField = new JTextField();
        locationTextField.setToolTipText("Enter the location where fonts you wish to view are stored here");
        locationTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                mw.setTyping(true);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                mw.setTyping(false);
            }
        });
        locationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    mw.setTyping(false);
                    File f = new File(locationTextField.getText());
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            currentDirectory = f;
                            updateDisplay();
                        } else {
                            showError("The location does not point to a directory.");
                        }
                    } else {
                        showError("Directory does not exist.");
                    }
                }
            }
        });

        locationPanel.add(locationTextField, java.awt.BorderLayout.CENTER);

        browseButton.setText("Browse");
        browseButton.setToolTipText("Browse for font directory");
        browseButton.addActionListener(evt -> {
            // Create new file chooser
            JFileChooser fc = new JFileChooser(new File(""));
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // Show open dialog; this method does not return until the dialog is closed
            fc.showOpenDialog(this);
            if (fc.getSelectedFile() != null) {
                currentDirectory = fc.getSelectedFile();
                locationTextField.setText(currentDirectory.toString());
                updateDisplay();
            }
        });

        locationPanel.add(browseButton, java.awt.BorderLayout.EAST);

        add(locationPanel, java.awt.BorderLayout.NORTH);

        otherFontsScrollPane = new JScrollPane();
        otherFontsScrollPane.setBorder(null);
        otherFontsList = new JList<>();
        otherFontsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                selectItem(otherFontsList.getSelectedIndex());
            }
        });
        otherFontsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectItem(otherFontsList.getSelectedIndex());
            }
        });

        otherFontsScrollPane.setViewportView(otherFontsList);

        add(otherFontsScrollPane, java.awt.BorderLayout.CENTER);

    }

    private void showError(String text) {
        JOptionPane.showMessageDialog(OtherFontsPanel.this, text, "Error!", JOptionPane.ERROR_MESSAGE);
    }
}