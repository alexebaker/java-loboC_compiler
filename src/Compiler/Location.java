package Compiler;

public class Location {
    private String path;
    private long lineCount;
    private long charCount;

    public Location(CompilerState cs) {
        this.path = cs.getInputPath();
        this.lineCount = cs.getIO().getLineCount();
        this.charCount = cs.getIO().getCharCount();
    }

    public String getPath() {
        return path;
    }

    public long getLineCount() {
        return lineCount;
    }

    public long getCharCount() {
        return charCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            return equals((Location) obj);
        }
        return false;
    }

    public boolean equals(Location loc) {
        return path.equals(loc.getPath()) && lineCount == loc.getLineCount() && charCount == loc.getCharCount();
    }

    @Override
    public int hashCode() {
        return (path.hashCode() * (int) lineCount - (int) charCount) * 97;
    }
}
