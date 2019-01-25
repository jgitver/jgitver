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
package fr.brouillard.oss.jgitver.issues;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Version;

public class Issue65NoFailTest {
    private static File wktreeDirectory;
    private static Scenarios.Scenario scenario;

    public static Scenarios.Scenario init_scenario() {
        Scenarios.ScenarioBuilder builder = new Scenarios.ScenarioBuilder()
                .commit("content", "A")
                .commit("content", "B")
                .commit("content", "C");
        builder.branchOnAppId("another", "C");
        return builder.getScenario();
    }

    @BeforeAll
    public static void init() {
        scenario = init_scenario();
        wktreeDirectory = init_worktree();
    }

    @AfterAll
    public static void cleanup() {
        try {
            Misc.deleteDirectorySimple(scenario.getRepositoryLocation());
        } catch (Exception ignore) {
            System.err.println("cannot remove " + scenario.getRepositoryLocation());
        }
        try {
            Misc.deleteDirectorySimple(wktreeDirectory);
        } catch (Exception ignore) {
            System.err.println("cannot remove worktree at: " + wktreeDirectory);
        }
    }

    private static File init_worktree() {
        File generatedProjectDirectory = scenario.getRepositoryLocation().getParentFile();
        File wkDir = new File(generatedProjectDirectory.getParent(), "wktree");
        try {
            Process p =new ProcessBuilder("git", "worktree", "add", wkDir.getAbsolutePath().toString(), "master")
                    .directory(generatedProjectDirectory)
                    .start();

            try {
                p.waitFor();
            } catch (InterruptedException ignore) {}

        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        return wkDir;
    }

    @Test
    public void can_execute_jgitver_on_worktree() {
        GitVersionCalculator worktreeCalculator = GitVersionCalculator.location(wktreeDirectory);
        String version = worktreeCalculator.getVersion();
        assertThat(version, is(Version.NO_WORKTREE_AND_INDEX.toString()));
    }
}
