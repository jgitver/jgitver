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

import static fr.brouillard.oss.jgitver.Lambdas.mute;
import static fr.brouillard.oss.jgitver.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.brouillard.oss.jgitver.Scenarios.Scenario;


public class Scenario3SnapshotsWithDefaultsTest {
    private static Scenario scenario;
    private Repository repository;
    private Git git;
    private GitVersionCalculator versionCalculator;

    /**
     * Initialiaze the whole junit class tests ; creates the git scenario.
     */
    @BeforeClass
    public static void initClass() {
        scenario = Scenarios.s3_linear_with_snapshots_light_tags();
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
    public void init() throws IOException {
        repository = new FileRepositoryBuilder().setGitDir(scenario.getRepositoryLocation()).build();
        git = new Git(repository);
        versionCalculator = GitVersionCalculator.location(scenario.getRepositoryLocation());
        
        // reset the head to master
        unchecked(() -> git.checkout().setName("master").call());
    }

    /**
     * Cleanups after each tests.
     */
    @After
    public void clean() {
        mute(() -> git.close());
        mute(() -> repository.close());
        mute(() -> versionCalculator.close());
    }
    
    @Test
    public void head_is_on_master_by_default() throws Exception {
        assertThat(repository.getBranch(), is("master"));
    }
    
    @Test
    public void version_on_normal_tag_is_tag_value() {
        Arrays.asList("1.0.0", "2.0.0").forEach(tag -> {
            // when tag is checkout
            unchecked(() -> git.checkout().setName(tag).call());
            // the version matches the tag
            assertThat(versionCalculator.getVersion(), is(tag));
        });
    }
    
    @Test
    public void version_of_first_commit_without_ancestor_tag() {
        ObjectId firstCommit = scenario.getCommits().get("A");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is(Version.DEFAULT_VERSION.toString()));
    }

    @Test
    public void version_of_C_commit() {
        ObjectId cCommit = scenario.getCommits().get("C");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.1.0-SNAPSHOT"));
    }
    
    @Test
    public void version_of_E_commit() {
        ObjectId cCommit = scenario.getCommits().get("E");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("3.0.0-SNAPSHOT"));
    }
    
    @Test
    public void version_of_F_commit() {
        ObjectId cCommit = scenario.getCommits().get("F");
        
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("3.0.0-SNAPSHOT"));
    }
}
