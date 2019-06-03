
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
package fr.brouillard.oss.jgitver;

import static fr.brouillard.oss.jgitver.impl.Lambdas.mute;
import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.brouillard.oss.jgitver.impl.DistanceCalculator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import fr.brouillard.oss.jgitver.Scenarios.Scenario;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScenarioTest {
    protected static Scenario scenario;
    protected Repository repository;
    protected Git git;
    protected GitVersionCalculator versionCalculator;

    private Consumer<GitVersionCalculator> versionCalculatorCustomizer;

    /**
     * Initialize the whole junit class tests ; creates the git scenario.
     */
    public ScenarioTest(Supplier<Scenario> scenarioSupplier, Consumer<GitVersionCalculator> versionCalculatorCustomizer) {
        scenario = scenarioSupplier.get();
        this.versionCalculatorCustomizer = versionCalculatorCustomizer;
        if (Misc.isDebugMode()) {
            System.out.println("git repository created under: " + scenario.getRepositoryLocation());
            printRepository(System.out, scenario.getRepositoryLocation());
            System.out.println();
            System.out.flush();
        }
    }

    private void printRepository(PrintStream out, File repositoryLocation) {
        ProcessBuilder gitLogGraphPB = new ProcessBuilder("git", "log", "--graph", "--pretty=format:%h -%d %s", "--abbrev-commit");
        gitLogGraphPB.inheritIO();
        gitLogGraphPB.directory(repositoryLocation);
        try {
            Process gitLogGraph = gitLogGraphPB.start();
            gitLogGraph.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleanup the whole junit scenario ; deletes the created git repository.
     */
    @AfterAll
    public static void cleanupClass() {
        try {
            Misc.deleteDirectorySimple(scenario.getRepositoryLocation());
        } catch (Exception ignore) {
            System.err.println("cannot remove " + scenario.getRepositoryLocation());
        }
    }
    
    /**
     * Prepare common variables to access the git repository.
     * @throws IOException if a disk error occurred
     */
    @BeforeEach
    public void initVersionCalculator() throws IOException {
        repository = new FileRepositoryBuilder().setGitDir(scenario.getRepositoryLocation()).build();
        git = new Git(repository);
        versionCalculator = GitVersionCalculator.location(scenario.getRepositoryLocation());
        versionCalculatorCustomizer.accept(versionCalculator);
        
        // reset the head to master
        if (!scenario.getCommits().isEmpty()) {
            unchecked(() -> git.checkout().setName("master").call());
        }
    }

    /**
     * Cleanups after each tests.
     */
    @AfterEach
    public void cleanVersionCalculator() {
        mute(() -> git.close());
        mute(() -> repository.close());
        mute(() -> versionCalculator.close());
    }

    protected void assertDistanceIs(String fromCommitName, String toCommitName, int expectedDistance) {
        Objects.requireNonNull(fromCommitName);
        Objects.requireNonNull(toCommitName);

        ObjectId startCommitId = scenario.getCommits().get(fromCommitName);
        ObjectId toCommitId = scenario.getCommits().get(toCommitName);

        assertNotNull(startCommitId, fromCommitName + " commit was not found");
        assertNotNull(toCommitId, toCommitName + " commit was not found");

        assertDistanceIs(startCommitId, toCommitId, expectedDistance);
    }

    protected void assertDistanceIs(ObjectId startCommitId, ObjectId toCommitId, int expectedDistance) {
        Objects.requireNonNull(startCommitId);
        Objects.requireNonNull(toCommitId);

        Optional<Integer> computedDistance = DistanceCalculator.create(startCommitId, repository).distanceTo(toCommitId);
        assertThat("distanceTo must always send back a result", computedDistance, notNullValue());
        assertThat("distance should have been computed", computedDistance.isPresent(), is(true));
        assertThat("wrong distance computed", computedDistance.get(), is(expectedDistance));
    }
}
