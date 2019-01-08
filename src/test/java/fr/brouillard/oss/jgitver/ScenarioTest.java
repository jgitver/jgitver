
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

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import fr.brouillard.oss.jgitver.Scenarios.Scenario;

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
        }
    }

    /**
     * Cleanup the whole junit scenario ; deletes the created git repository.
     */
    @AfterClass
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
    @Before
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
    @After
    public void cleanVersionCalculator() {
        mute(() -> git.close());
        mute(() -> repository.close());
        mute(() -> versionCalculator.close());
    }
    
}
