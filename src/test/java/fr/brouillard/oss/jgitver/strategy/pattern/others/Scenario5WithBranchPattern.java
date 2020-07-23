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
package fr.brouillard.oss.jgitver.strategy.pattern.others;

import fr.brouillard.oss.jgitver.BranchingPolicy;
import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Scenario5WithBranchPattern extends ScenarioTest {

    public Scenario5WithBranchPattern() {
        super(
                Scenarios::s5_several_branches,
                calculator -> calculator.setStrategy(Strategies.PATTERN)
                        .setVersionPattern("${v}-DEFAULT")
                        .setQualifierBranchingPolicies(new BranchingPolicy("(int)", "${v}-INT")));
    }

    @Test
    public void version_of_branch_master() {
        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName("master").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-DEFAULT"));
    }

    @Test
    public void version_of_branch_int() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("int").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-INT"));
    }

    @Test
    public void version_of_branch_dev() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("dev").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-DEFAULT"));
    }

    @Test
    public void version_of_A_commit() {
        ObjectId firstCommit = scenario.getCommits().get("A");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }

    @Test
    public void version_of_B_commit() {
        ObjectId bCommit = scenario.getCommits().get("B");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(bCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-DEFAULT"));
    }
}
