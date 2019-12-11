package FontViewer.components;

import FontViewer.FontFile;
import FontViewer.windows.MainWindow;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;

public class OtherFontsPanel extends AbstractJListFontPanel {
    public OtherFontsPanel(MainWindow mw) {
        super(mw);

        initComponents();
    }

    private static boolean isFontFilename(String path) {
        String pathUpper = path.toUpperCase();
        return pathUpper.endsWith(".TTF") || pathUpper.endsWith(".OTF");
    }

    private void browseTo(File directory) {
        String[] filenames = directory.list((dir, name) -> isFontFilename(name));
        assert filenames != null;
        Arrays.sort(filenames, String.CASE_INSENSITIVE_ORDER);

        FontFile[] files = Arrays.stream(filenames).map(it -> new FontFile(it, directory.toString())).toArray(FontFile[]::new);
        fontList.setListData(files);
        fontList.setEnabled(files.length != 0);

        mw.updateDisplay();
    }

    private void initComponents() {
        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BorderLayout(4, 0));
        locationPanel.add(new JLabel("Location:"), BorderLayout.WEST);

        JTextField locationTextField = new JTextField();
        locationTextField.setToolTipText("Enter the location where fonts you wish to view are stored here");
        locationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    File f = new File(locationTextField.getText());
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            browseTo(f);
                        } else {
                            showError("The location does not point to a directory.");
                        }
                    } else {
                        showError("Directory does not exist.");
                    }
                }
            }
        });

        locationPanel.add(locationTextField, BorderLayout.CENTER);

        JButton browseButton = new JButton("Browse");
        browseButton.setToolTipText("Browse for font directory");
        browseButton.addActionListener(evt -> {
            // Create new file chooser
            JFileChooser fc = new JFileChooser(new File(""));
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // Show open dialog; this method does not return until the dialog is closed
            fc.showOpenDialog(this);
            File selectedDirectory = fc.getSelectedFile();
            if (selectedDirectory != null) {
                locationTextField.setText(selectedDirectory.toString());
                browseTo(selectedDirectory);
            }
        });

        locationPanel.add(browseButton, BorderLayout.EAST);

        add(locationPanel, BorderLayout.NORTH);
    }

    private void showError(String text) {
        JOptionPane.showMessageDialog(OtherFontsPanel.this, text, "Error!", JOptionPane.ERROR_MESSAGE);
    }
}