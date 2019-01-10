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
import static org.hamcrest.Matchers.containsString;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.Scenarios.Scenario;
import fr.brouillard.oss.jgitver.Scenarios.ScenarioBuilder;
import fr.brouillard.oss.jgitver.impl.GitUtils;

public class Issue22Test {
    private static Scenario scenario;
    private static Instant firstCommitInstant;
    private static Instant headCommitInstant;
    
    private Git git;
    private Repository repository;
    private GitVersionCalculator versionCalculator;

    @BeforeAll
    public static void init_scenario() {
        ScenarioBuilder sb = new ScenarioBuilder();

        sb =  sb.commit("content", "A");
        firstCommitInstant = Instant.now().with(ChronoField.MILLI_OF_SECOND, 0l);
        
        // let'scenario wait 2 seconds
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));
        } catch (InterruptedException ignore) {}

        scenario = sb.commit("content", "B").getScenario();
        headCommitInstant = Instant.now().with(ChronoField.MILLI_OF_SECOND, 0l);

        if (Misc.isDebugMode()) {
            System.out.println("git repository created under: " + scenario.getRepositoryLocation());
        }
    }
    
    @BeforeEach
    public void init() throws IOException {
        repository = new FileRepositoryBuilder().setGitDir(scenario.getRepositoryLocation()).build();
        git = new Git(repository);
        versionCalculator = GitVersionCalculator.location(scenario.getRepositoryLocation())
                .setMavenLike(false)
                .setUseGitCommitTimestamp(true);
    }

    @Test
    public void check_timestamp_qualifier_is_present_on_head() throws IOException {
        // reset the head to master
        unchecked(() -> git.checkout().setName("master").call());

        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit rc = walk.parseCommit(scenario.getCommits().get("B"));
            String expectedHeadTimestampQualifier = GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant());
            assertThat(versionCalculator.getVersion(), containsString(expectedHeadTimestampQualifier));
        }
    }

    @Test
    public void check_timestamp_qualifier_is_present_on_commit_A() throws IOException {
        // checkout the first commit in scenario
        ObjectId firstCommit = scenario.getCommits().get("A");
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());

        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit rc = walk.parseCommit(scenario.getCommits().get("A"));
            String expectedHeadTimestampQualifier = GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant());
            assertThat(versionCalculator.getVersion(), containsString(expectedHeadTimestampQualifier));
        }
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
            Misc.deleteDirectorySimple(scenario.getRepositoryLocation());
        } catch (Exception ignore) {
            System.err.println("cannot remove " + scenario.getRepositoryLocation());
        }
    }
}
