package FontViewer.components;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class TextAreaFromFile extends JTextArea {
    private String filename;

    public TextAreaFromFile(String filename) {
        super();
        super.setEditable(false);
        super.setLineWrap(true);
        super.setWrapStyleWord(true);
        this.filename = filename;

        try {
            setContent();
        } catch (IOException ioe) {
            super.setText("Error loading " + filename);
        }
    }

    private void setContent() throws IOException {
        String pathInJar = "FontViewer/resources/texts/" + filename;
        URL resource = this.getClass().getClassLoader().getResource(pathInJar);
        if (resource == null) {
            setText(String.format("Could not find JAR resource %s", pathInJar));
            return;
        }

        try (InputStream is = resource.openStream()) {
            setText(new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n")));
        }
    }
}