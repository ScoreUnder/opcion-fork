package opcion;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

public class FontFile {
    private final String name;
    private String cachedName = null;
    private final String location;

    public FontFile(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Font load() {
        Font f;
        if (getLocation().equals("System Font")) {
            f = new Font(getName(), Font.PLAIN, 20);
        } else {
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(getLocation() + File.separator + getName()));
            } catch (Exception e) {
                f = null;
            }
        }
        return f;
    }

    public String getLogicalName() {
        if (cachedName == null) {
            try {
                final Font font = load();
                cachedName = font.getName() + " (" + font.getFamily() + " / " + font.getFontName() + ")";
            } catch (Exception ignored) {
            }
        }
        return cachedName == null ? name : cachedName;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getLocation());
    }
}
