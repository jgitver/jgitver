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

import static fr.brouillard.oss.jgitver.impl.Lambdas.mute;
import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.Scenarios.Scenario;
import fr.brouillard.oss.jgitver.Scenarios.ScenarioBuilder;
import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public class Issue32Test {
    private static final String BASE_TAG = "1.0.0";
    private static Scenario s;

    private Git git;
    private Repository repository;
    private GitVersionCalculator versionCalculator;
    
    @BeforeAll
    public static void init_scenario() {
        s = new ScenarioBuilder()
                .commit("content", "A")
                .tag(BASE_TAG)
                .commit("content", "B")
                .commit("content", "C")
                .getScenario();

        if (Misc.isDebugMode()) {
            System.out.println("git repository created under: " + s.getRepositoryLocation());
        }
    }

    @BeforeEach
    public void init() throws IOException {
        repository = new FileRepositoryBuilder().setGitDir(s.getRepositoryLocation()).build();
        git = new Git(repository);
        versionCalculator = GitVersionCalculator.location(s.getRepositoryLocation())
                .setMavenLike(false)
                .setUseDistance(true);
    }

    @Test
    public void check_commit_ahead_distance() throws Exception {
        // reset the head to master
        unchecked(() -> git.checkout().setName("master").call());

        Version vo = versionCalculator.getVersionObject();

        Optional<String> optDistance = versionCalculator.meta(Metadatas.COMMIT_DISTANCE);
        assertTrue(optDistance.isPresent());
        assertThat(optDistance.get(), CoreMatchers.is("2"));
    }    
    
    @Test
    public void check_distance_on_tag() {
        // reset the head to master
        unchecked(() -> git.checkout().setName(BASE_TAG).call());

        Version vo = versionCalculator.getVersionObject();

        Optional<String> optDistance = versionCalculator.meta(Metadatas.COMMIT_DISTANCE);
        assertTrue(optDistance.isPresent());
        assertThat(optDistance.get(), CoreMatchers.is("0"));
    }

    @Test
    public void check_distance_not_set_with_maven() throws Exception {
        // reset the head to master
        unchecked(() -> git.checkout().setName(BASE_TAG).call());

        GitVersionCalculator gvc = GitVersionCalculator.location(s.getRepositoryLocation())
                .setMavenLike(true)
                .setUseDistance(true);
        
        Optional<String> optDistance = gvc.meta(Metadatas.COMMIT_DISTANCE);
        assertFalse(optDistance.isPresent());
        gvc.close();
    }

    @AfterEach
    public void cleanup() {
        mute(() -> git.close());
        mute(() -> repository.close());
        mute(() -> versionCalculator.close());
    }

    @AfterAll
    public static void delete() {
        try {
            Misc.deleteDirectorySimple(s.getRepositoryLocation());
        } catch (Exception ignore) {
            System.err.println("cannot remove " + s.getRepositoryLocation());
        }
    }
}
