package FontViewer.components;

import FontViewer.windows.MainWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class OtherFontsPanel extends AbstractListPanel {
    private File currentDirectory;
    private String[] filenames = new String[0];
    private MainWindow mw;
    private JTextField locationTextField;
    private javax.swing.JList<String> otherFontsList;
    private JScrollPane otherFontsScrollPane;

    public OtherFontsPanel(MainWindow mw) {
        this.mw = mw;

        initComponents();
    }

    public String[] getItem(int itemNumber) {
        if (itemNumber >= 0 && itemNumber < filenames.length) {
            return new String[]{
                    filenames[itemNumber],
                    currentDirectory.toString(),
                    String.valueOf(itemNumber)
            };
        } else {
            return new String[3];
        }
    }

    public int getNumItems() {
        return filenames.length;
    }

    public int getCurrentItemNum() {
        return otherFontsList.getSelectedIndex();
    }

    private void updateDisplay() {
        filenames = currentDirectory.list((dir, name) -> name.toUpperCase().endsWith(".TTF"));
        assert filenames != null;
        Arrays.sort(filenames, String.CASE_INSENSITIVE_ORDER);

        if (filenames.length == 0) {
            String[] message = {"This folder does not contain any fonts."};
            otherFontsList.setListData(message);
            otherFontsList.setEnabled(false);
        } else {
            otherFontsList.setListData(filenames);
            otherFontsList.setEnabled(true);
        }

        mw.updateDisplay();
    }

    public void selectItem(String name, String loc) {
        otherFontsList.setSelectedValue(name, true);
        int p = otherFontsList.getSelectedIndex();
        if (p >= 0)
            mw.setCurrentFont(filenames[p], currentDirectory.toString(), p);
    }

    protected void setCurrentItem(int itemPos, boolean updateUI) {
        if (filenames.length != 0) {
            if (updateUI) {
                otherFontsList.setSelectedIndex(itemPos);
                int spos = itemPos * (otherFontsScrollPane.getVerticalScrollBar().getMaximum() / filenames.length);
                spos -= (otherFontsScrollPane.getSize().height / 2);
                otherFontsScrollPane.getVerticalScrollBar().setValue(spos);
            }

            if (itemPos >= 0)
                mw.setCurrentFont(filenames[itemPos], currentDirectory.toString(), itemPos);
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
                setCurrentItem(otherFontsList.getSelectedIndex(), false);
            }
        });
        otherFontsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                setCurrentItem(otherFontsList.getSelectedIndex(), false);
            }
        });

        otherFontsScrollPane.setViewportView(otherFontsList);

        add(otherFontsScrollPane, java.awt.BorderLayout.CENTER);

    }

    private void showError(String text) {
        JOptionPane.showMessageDialog(OtherFontsPanel.this, text, "Error!", JOptionPane.ERROR_MESSAGE);
    }
}