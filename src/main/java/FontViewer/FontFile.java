package FontViewer;

public class FontFile {
    private final String name;
    private final String location;

    public FontFile(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getLocation());
    }
}
