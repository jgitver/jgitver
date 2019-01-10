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

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.BranchingPolicy;
import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.Scenarios.Scenario;
import fr.brouillard.oss.jgitver.Scenarios.ScenarioBuilder;

public class Issue23Test {
    private static final String BRANCH_NAME = "my-branch";
    private static final String BASE_TAG = "1.0.0";
    private Scenario s;

    @BeforeEach
    public void init_scenario() {
        s = new ScenarioBuilder()
                .commit("content", "A")
                .tag(BASE_TAG)
                .branchOnAppId(BRANCH_NAME, "A")
                .commit("content", "B")
                .getScenario();

        if (Misc.isDebugMode()) {
            System.out.println("git repository created under: " + s.getRepositoryLocation());
        }
    }

    @Test
    public void check_version_using_IDENTITY_branch_transformation() {
        GitVersionCalculator gvc = GitVersionCalculator.location(s.getRepositoryLocation());
        gvc.setMavenLike(false);
        gvc.setAutoIncrementPatch(false);
        gvc.setUseDistance(false);
        gvc.setUseDirty(false);
        gvc.setUseGitCommitId(false);
        gvc.setGitCommitIdLength(20);
        gvc.setNonQualifierBranches("");
        gvc.setUseDefaultBranchingPolicy(false);
        gvc.setQualifierBranchingPolicies(new BranchingPolicy("(.*)", Arrays.asList("IDENTITY")));

        assertThat(gvc.getVersion(), CoreMatchers.is(BASE_TAG + "-" + BRANCH_NAME));
    }

    @AfterEach
    public void cleanup() {
        Misc.deleteDirectorySimple(s.getRepositoryLocation());
    }
}
