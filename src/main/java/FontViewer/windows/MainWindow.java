package FontViewer.windows;

import FontViewer.components.*;
import FontViewer.windows.dialogs.AboutDialog;
import FontViewer.windows.dialogs.TextAreaFromFileDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow extends javax.swing.JFrame {
    // Constants
    private final int[] FONT_SIZES = {6, 8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32, 36, 42, 48, 56, 72, 84};
    private final String ADD = "Add to Favourites";
    private final String REM = "Remove from Favourites";

    // Variables
    private ListPanel currentPanel;
    private String fname;
    private String floc;
    private boolean typingLoc;

    // List view properties
    private static final int ROWS = 10;
    private static final int COLUMNS = 1;

    public MainWindow() {
        initComponents();

        menuBar.requestFocus();

        // Flag for whether user is typing in location field
        typingLoc = false;

        // Disable hidden menu (used to catch keystrokes)
        hiddenMenu.setVisible(false);
        hiddenMenu.setEnabled(false);

        // Set current panel
        changeCurrentPanel(systemFontsPanel);
    }

    public void addToFav() {
        addToFav(fname, floc);
    }

    public void removeFromFav() {
        removeFromFav(fname, floc);
    }

    public void addToFav(String name, String loc) {
        if (!favouriteFontsPanel.addToFav(name, loc)) {
            JOptionPane.showMessageDialog(this, "Font already in favourites.", "Error!", JOptionPane.ERROR_MESSAGE);
        } else {
            updateDisplay();
        }
    }

    public void removeFromFav(String name, String loc) {
        if (!favouriteFontsPanel.removeFromFav(name, loc)) {
            JOptionPane.showMessageDialog(this, "Font not found in favourites.", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCurrentFont(String name, String loc, int position) {
        fname = name;
        floc = loc;
        listViewPanel.setPosition(position);
        sampleTextPanel.setCurrentFont(name, loc);
    }

    public void setFontSize(int s) {
        if (listViewPanel != null) {
            listViewPanel.setFontSize(s);
        }
    }

    public void updateDisplay() {
        listViewPanel.updateDisplay();
    }

    public void setTyping(boolean t) {
        typingLoc = t;
        if (!t) {
            menuBar.requestFocus();
        }
    }

    private void saveFavToFile(File f) {
        try {
            // Init stuff
            FavouriteFontsPanel fav = favouriteFontsPanel;
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            // Sort favourites
            fav.sortAllRowsBy(0, true);

            // Write favourites
            for (int i = 0; i < fav.getNumItems(); i++) {
                String[] s = fav.getItem(i);
                bw.write(s[1] + File.separator + s[0]);
                bw.newLine();
            }
            bw.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Cannot write to file.", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        systemFontsPanel = new SystemFontsPanel(this);
        favouriteFontsPanel = new FavouriteFontsPanel(this);
        sampleTextPanel = new SampleTextPanel(this, FONT_SIZES);
        listViewPanel = new ListViewPanel(favouriteFontsPanel, ROWS, COLUMNS);
        menuBar = new JMenuBar();
        hiddenMenu = new JMenu();

        getContentPane().setLayout(new java.awt.BorderLayout(0, 5));

        setTitle("Opcion Font Viewer");
        setFont(new java.awt.Font("Dialog", Font.PLAIN, 10));
        setIconImage(new ImageIcon(this.getClass().getClassLoader().getResource("FontViewer/resources/icons/IconSmall.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("System Fonts", systemFontsPanel);
        tabbedPane.addTab("Other Fonts", new OtherFontsPanel(this));
        tabbedPane.addTab("Favourite Fonts", favouriteFontsPanel);
        tabbedPane.addChangeListener(evt -> changeCurrentPanel((ListPanel) tabbedPane.getSelectedComponent()));

        JSplitPane quickViewSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, sampleTextPanel);
        quickViewSplitPane.setBorder(null);
        quickViewSplitPane.setDividerSize(5);
        quickViewSplitPane.setResizeWeight(0.5);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, quickViewSplitPane, listViewPanel);
        //mainSplitPane.setBorder(null);
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setOneTouchExpandable(true);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        JMenuItem savFavsMenuItem = new JMenuItem("Save Favourites");
        savFavsMenuItem.setMnemonic('s');
        savFavsMenuItem.addActionListener(evt -> {
            if (((ListPanel) favouriteFontsPanel).getNumItems() <= 0) {
                JOptionPane.showMessageDialog(this, "There are no favourite fonts to save.", "Error!", JOptionPane.ERROR_MESSAGE);
            } else {
                // Create new file chooser
                JFileChooser fc = new JFileChooser(new File(""));
                // Show save dialog; this method does not return until the dialog is closed
                fc.showSaveDialog(this);
                if (fc.getSelectedFile() != null) {
                    File f = fc.getSelectedFile();
                    if (!f.exists() || JOptionPane.showConfirmDialog(this, String.format("The file %s already exists, do you\nwant to overwrite?", f.getName()), "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        saveFavToFile(f);
                    }
                }
            }
        });

        fileMenu.add(savFavsMenuItem);

        JMenuItem setSampleTextMenuItem = new JMenuItem("Set Sample Text", 't');
        setSampleTextMenuItem.addActionListener(evt -> {
            String t = JOptionPane.showInputDialog(this, "Set sample text as:", "Change Sample Text", JOptionPane.QUESTION_MESSAGE);
            if (t != null) {
                sampleTextPanel.setSampleText(t);
                listViewPanel.setSampleText(t);
            }
        });
        fileMenu.add(setSampleTextMenuItem);

        fileMenu.add(new JSeparator());

        JMenuItem quitMenuItem = new JMenuItem("Quit", 'q');
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        quitMenuItem.addActionListener(evt -> System.exit(0));

        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        JMenu viewsMenu = new JMenu("Views");
        viewsMenu.setMnemonic('v');
        JCheckBoxMenuItem listViewCheckBoxMenuItem = new JCheckBoxMenuItem();
        listViewCheckBoxMenuItem.setMnemonic('l');
        listViewCheckBoxMenuItem.setSelected(true);
        listViewCheckBoxMenuItem.setText("List View");
        listViewCheckBoxMenuItem.addActionListener(evt -> {
            if (listViewCheckBoxMenuItem.isSelected()) {
                listViewPanel.setVisible(true);
                mainSplitPane.setDividerLocation(mainSplitPane.getLastDividerLocation());
                mainSplitPane.setEnabled(true);
            } else {
                mainSplitPane.setDividerLocation(1.0);
                mainSplitPane.setEnabled(false);
                listViewPanel.setVisible(false);
            }
        });

        viewsMenu.add(listViewCheckBoxMenuItem);

        menuBar.add(viewsMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');

        JMenuItem addToFavHelpMenuItem = new JMenuItem("Add Font to Favourites", 'f');
        addToFavHelpMenuItem.addActionListener(evt -> showTextHelp("Help - Add Font to Favourites", "addfavHelp.txt"));
        helpMenu.add(addToFavHelpMenuItem);

        JMenuItem installFontsMenuItem = new JMenuItem("Installing Fonts", 'i');
        installFontsMenuItem.addActionListener(evt -> showTextHelp("Help - Installing Fonts", "installHelp.txt"));
        helpMenu.add(installFontsMenuItem);

        JMenuItem shortcutsMenuItem = new JMenuItem("Shortcut Keys", 's');
        shortcutsMenuItem.addActionListener(evt -> showTextHelp("Help - Shortcut Keys", "shortcutsHelp.txt"));
        helpMenu.add(shortcutsMenuItem);

        helpMenu.add(new JSeparator());

        JMenuItem aboutMenuItem = new JMenuItem("About", 'a');
        aboutMenuItem.addActionListener(evt -> new AboutDialog(this).setVisible(true));
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        hiddenMenu.setText("hidden");
        JMenuItem prevPageMenuItem = new JMenuItem("prevPage");
        prevPageMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0));
        prevPageMenuItem.addActionListener(evt -> {
            if (!typingLoc) {
                menuBar.requestFocus();
                listViewPanel.prevPage();
            }
        });

        hiddenMenu.add(prevPageMenuItem);

        JMenuItem nextPageMenuItem = new JMenuItem("nextPage");
        nextPageMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0));
        nextPageMenuItem.addActionListener(evt -> {
            if (!typingLoc) {
                menuBar.requestFocus();
                listViewPanel.nextPage();
            }
        });

        hiddenMenu.add(nextPageMenuItem);

        JMenuItem upMenuItem = new JMenuItem("up");
        upMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, 0));
        upMenuItem.addActionListener(evt -> {
            if (!typingLoc) {
                menuBar.requestFocus();
                currentPanel.selectPrev();
            }
        });

        hiddenMenu.add(upMenuItem);

        JMenuItem downMenuItem = new JMenuItem("down");
        downMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
        downMenuItem.addActionListener(evt -> {
            if (!typingLoc) {
                menuBar.requestFocus();
                currentPanel.selectNext();
            }
        });

        hiddenMenu.add(downMenuItem);

        JMenuItem addOrRemMenuItem = new JMenuItem("addOrRem");
        addOrRemMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        addOrRemMenuItem.addActionListener(evt -> {
            if (!typingLoc) {
                menuBar.requestFocus();
                if (currentPanel instanceof FavouriteFontsPanel) {
                    removeFromFav();
                } else {
                    if (!favouriteFontsPanel.addToFav(fname, floc)) {
                        removeFromFav();
                    } else {
                        updateDisplay();
                    }
                }
            }
        });

        hiddenMenu.add(addOrRemMenuItem);

        menuBar.add(hiddenMenu);

        setJMenuBar(menuBar);

        pack();
    }

    private void changeCurrentPanel(ListPanel newPanel) {
        currentPanel = newPanel;

        // Update list
        listViewPanel.setView((JPanel) newPanel);

        // Update sampleTextPanel
        String[] s = newPanel.getCurrentItem();
        if (s[0] != null) {
            setCurrentFont(s[0], s[1], Integer.parseInt(s[2]));
        }

        // Set fav button action
        if (newPanel instanceof FavouriteFontsPanel) {
            sampleTextPanel.setFavButtonAction(REM);
        } else {
            sampleTextPanel.setFavButtonAction(ADD);
        }
    }

    private void showTextHelp(String title, String filename) {
        TextAreaFromFileDialog taffd = new TextAreaFromFileDialog(this, title, filename);
        taffd.setWrap(false);
        taffd.setVisible(true);
    }

    private FavouriteFontsPanel favouriteFontsPanel;
    private JMenu hiddenMenu;
    private ListViewPanel listViewPanel;
    private JMenuBar menuBar;
    private SampleTextPanel sampleTextPanel;
    private SystemFontsPanel systemFontsPanel;
}