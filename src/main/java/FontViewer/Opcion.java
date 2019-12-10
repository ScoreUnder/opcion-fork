package FontViewer;

import FontViewer.windows.*;

import javax.swing.*;

public class Opcion {
    public static void main(String args[]) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Can't load native L&F. Not the end of the world.
            e.printStackTrace();
        }

        MainWindow mw = new MainWindow();
        mw.setVisible(true);
    }
}