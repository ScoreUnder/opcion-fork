package opcion;

import opcion.windows.MainWindow;

import javax.swing.*;

public class Opcion {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Can't load cross-platform L&F. Not the end of the world.
            e.printStackTrace();
        }

        MainWindow mw = new MainWindow();
        mw.setVisible(true);
    }
}