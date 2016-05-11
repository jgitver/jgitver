package fr.brouillard.oss.jgitver;

import java.io.File;

public class UsageExample {
    /**
     * Display the calculated version of the working directory, using jgitver in mode 'maven like'.
     */
    public static void main(String[] args) throws Exception {
        File workDir = new File(System.getProperty("user.dir"));
        try (GitVersionCalculator jgitver = GitVersionCalculator.location(workDir).setMavenLike(true)) {
            System.out.println(jgitver.getVersion());
        }
    }
}
