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
package fr.brouillard.oss.jgitver.strategy.configurable.inc;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.impl.GitUtils;
import fr.brouillard.oss.jgitver.ScenarioTest;

public class Scenario4AutoIncDetachedHeadOnBranchWithExternalBranchTest extends ScenarioTest {

    public Scenario4AutoIncDetachedHeadOnBranchWithExternalBranchTest() {
        super(
                Scenarios::s4_linear_with_only_annotated_tags_and_branch,
                calculator -> calculator
                        .setStrategy(Strategies.CONFIGURABLE)
                        .setAutoIncrementPatch(true));
    }

    @BeforeEach
    public void init() throws IOException {
        ObjectId gCommit = scenario.getCommits().get("G");
        unchecked(() -> git.checkout().setName(gCommit.name()).call());
    }

    @Test
    public void head_is_detached() throws Exception {
        assertTrue(GitUtils.isDetachedHead(repository));
    }

    @Test
    public void version_of_with_branch_issue_10_provided_via_properties() {
        // checkout the commit in scenario
        ObjectId gCommit = scenario.getCommits().get("G");
        unchecked(() -> git.checkout().setName(gCommit.name()).call());

        String sp = "jgitver.branch";
        String old = System.getProperty(sp);
        try {
            System.setProperty(sp, "issue-10");
            assertThat(versionCalculator.getVersion(), is("1.0.1-3-issue_10"));
        } finally {
            if (old == null) {
                System.clearProperty(sp);
            } else {
                System.setProperty(sp, old);
            }
        }
    }
}
