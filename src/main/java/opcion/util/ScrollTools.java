package opcion.util;

import javax.swing.*;

public class ScrollTools {
    public static void scrollVerticallyTo(JScrollPane pane, int pos, int max) {
        int spos = pos * pane.getVerticalScrollBar().getMaximum() / max;
        spos -= pane.getSize().height / 2;
        pane.getVerticalScrollBar().setValue(spos);
    }
}
