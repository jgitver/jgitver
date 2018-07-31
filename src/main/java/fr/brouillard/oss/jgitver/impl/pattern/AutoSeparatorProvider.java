package fr.brouillard.oss.jgitver.impl.pattern;

public class AutoSeparatorProvider {
    private String current;
    private boolean versionEnded = false;
    private boolean firstAfterVersion = true;

    public AutoSeparatorProvider() {
        this.current = ".";
    }

    public void major() {
    }
    public void minor() {
    }
    public void patch() {
        endVersion();
    }
    public void endVersion() {
        if (!this.versionEnded) {
            this.versionEnded = true;
        }
    }
    public void next() {
        if (!versionEnded) {
            this.current = ".";
        } else {
            if (firstAfterVersion) {
                this.current = "-";
                firstAfterVersion = false;
            } else {
                this.current = ".";
            }
        }
    }

    public String currentSeparator() {
        return this.current;
    }
}
