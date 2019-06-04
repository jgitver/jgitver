/**
 * Copyright (C) 2016 Matthieu Brouillard [http://oss.brouillard.fr/jgitver] (matthieu@brouillard.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.brouillard.oss.jgitver.strategy.maven.others;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.io.Files;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.Version;

public class OnANonGitDirectoryTest {
    private static File nonGitDirectory;

    /**
     * Initialiaze the whole junit class tests ; creates the empty directory.
     */
    @BeforeAll
    public static void init() {
        nonGitDirectory = Files.createTempDir();
        if (Misc.isDebugMode()) {
            System.out.println("directory created under: " + nonGitDirectory);
        }
    } 

    /**
     * Cleanup the whole junit scenario ; deletes the created directory.
     */
    @AfterAll
    public static void cleanupClass() {
        try {
            Misc.deleteDirectorySimple(nonGitDirectory);
        } catch (Exception ignore) {
            System.err.println("cannot remove " + nonGitDirectory);
        }
    }
    
    @Test
    public void version_correspond_to_a_non_git() throws Exception {
        try (GitVersionCalculator versionCalculator = GitVersionCalculator.location(nonGitDirectory).setMavenLike(true)) {
            assertThat(versionCalculator.getVersion(), is(Version.NOT_GIT_VERSION.toString()));
        }
    }
}
