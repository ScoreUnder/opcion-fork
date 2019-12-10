package FontViewer.windows.dialogs;
import FontViewer.components.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

public class TextAreaFromFileDialog extends javax.swing.JDialog {
    private String title;
    private String filename;
    
    public TextAreaFromFileDialog(JFrame parent, String title, String filename) {
        super(parent);
        this.title = title;
        this.filename = filename;
        initComponents();
    }
    
    public void setWrap(boolean state) {
        jTextArea1.setLineWrap(state);
    }

    private void initComponents() {
        JScrollPane jScrollPane1 = new JScrollPane();
        jTextArea1 = new TextAreaFromFile(filename);

        getContentPane().setLayout(new BorderLayout(0, 2));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                TextAreaFromFileDialog.this.dispose();
            }
        });

        jScrollPane1.setBorder(null);
        jTextArea1.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("windowBorder"));
        jTextArea1.setLineWrap(true);
        jTextArea1.setColumns(80);
        jTextArea1.setRows(20);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, BorderLayout.CENTER);

        JButton jButton1 = new JButton("OK");
        jButton1.addActionListener(evt -> dispose());

        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        jPanel1.add(jButton1);

        getContentPane().add(jPanel1, BorderLayout.SOUTH);

        pack();
    }

    private JTextArea jTextArea1;
}