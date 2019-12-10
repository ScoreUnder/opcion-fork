package FontViewer.windows.dialogs;
import FontViewer.components.*;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AboutDialog extends javax.swing.JDialog {
    private static final String HOMEPAGE = "http://opcion.sourceforge.net";
    private Icon logo = new ImageIcon(this.getClass().getClassLoader().getResource("icons/Logo.png"));
    private String title = "Opcion Font Viewer " + ResourceBundle.getBundle("Opcion").getString("version");

    public AboutDialog(JFrame parent) {
        super(parent, "About", Dialog.DEFAULT_MODALITY_TYPE);
        initComponents();
    }
    
    private void initComponents() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                AboutDialog.this.dispose();
            }
        });

        getContentPane().add(new JLabel(logo), BorderLayout.WEST);

        JPanel homepagePanel = new JPanel();
        homepagePanel.setBorder(new TitledBorder(title));
        homepagePanel.add(makeHomepageButton());
        homepagePanel.add(makeChangeLogButton());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(homepagePanel, BorderLayout.NORTH);

        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new javax.swing.BoxLayout(creditsPanel, javax.swing.BoxLayout.Y_AXIS));
        creditsPanel.setBorder(new TitledBorder("Credits"));
        creditsPanel.add(makeCreditsPane());

        contentPanel.add(creditsPanel, BorderLayout.CENTER);

        JPanel licensesPanel = new JPanel();
        licensesPanel.setBorder(new TitledBorder("Licenses"));
        licensesPanel.add(makeViewLicenseButton());
        contentPanel.add(licensesPanel, BorderLayout.SOUTH);

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel copyrightLabel = new JLabel("Copyright (c) 2004 Paul Chiu");
        copyrightLabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 10));
        copyrightLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(copyrightLabel, BorderLayout.SOUTH);

        pack();
    }

    private JScrollPane makeCreditsPane() {
        JScrollPane creditsScrollPane = new JScrollPane();
        creditsScrollPane.setBorder(null);

        JTextArea creditsTextArea = new TextAreaFromFile("credits.txt");
        creditsTextArea.setBackground((Color) UIManager.getDefaults().get("windowBorder"));
        creditsScrollPane.setViewportView(creditsTextArea);
        return creditsScrollPane;
    }

    private JButton makeViewLicenseButton() {
        JButton viewLicenseButton = new JButton("Opcion License (GPL)");
        viewLicenseButton.addActionListener(evt -> {
            TextAreaFromFileDialog taffd = new TextAreaFromFileDialog((JFrame) AboutDialog.this.getParent(), "Opcion License", "opcionLicense.txt");
            taffd.setWrap(false);
            taffd.setVisible(true);
        });
        return viewLicenseButton;
    }

    private static boolean showBrowserCrossPlatform(String where) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI.create(where));
                return true;
            } catch (IOException | UnsupportedOperationException ignored) {
            }
        }
        return false;
    }

    private static boolean showBrowserLinux(String where) {
        try {
            Runtime.getRuntime().exec(new String[] {"xdg-open", where});
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    private JButton makeHomepageButton() {
        JButton homepageButton = new JButton("Visit Homepage");
        homepageButton.setToolTipText(HOMEPAGE);
        homepageButton.addActionListener(evt -> {
            boolean shown = showBrowserCrossPlatform(HOMEPAGE);
            if (!shown) shown = showBrowserLinux(HOMEPAGE);
            if (!shown) JOptionPane.showMessageDialog(this, "Could not find your browser", "Error", JOptionPane.ERROR_MESSAGE);
        });
        return homepageButton;
    }

    private JButton makeChangeLogButton() {
        JButton changeLogButton = new JButton();
        changeLogButton.setText("View Change Log");
        changeLogButton.addActionListener(evt -> {
            TextAreaFromFileDialog taffd = new TextAreaFromFileDialog((JFrame) AboutDialog.this.getParent(), "Change Log", "changeLog.txt");
            taffd.setVisible(true);
        });
        return changeLogButton;
    }
}