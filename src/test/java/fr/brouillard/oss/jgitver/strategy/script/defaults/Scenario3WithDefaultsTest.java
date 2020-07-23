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
package fr.brouillard.oss.jgitver.strategy.script.defaults;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.ScenarioTest;

public class Scenario3WithDefaultsTest extends ScenarioTest {

    public Scenario3WithDefaultsTest() {
        super(
                Scenarios::s3_linear_with_snapshots_light_tags,
                calculator -> calculator.setStrategy(Strategies.SCRIPT));
    }

    @Test
    public void head_is_on_master_by_default() throws Exception {
        assertThat(repository.getBranch(), is("master"));
    }

    @Test
    public void version_of_A_commit() {
        ObjectId firstCommit = scenario.getCommits().get("A");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("0.0.0-0"));
    }

    @Test
    public void version_of_B_commit() {
        ObjectId bCommit = scenario.getCommits().get("B");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(bCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }

    @Test
    public void version_of_C_commit() {
        ObjectId cCommit = scenario.getCommits().get("C");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.1.0-1"));
    }

    @Test
    public void version_of_D_commit() {
        ObjectId dCommit = scenario.getCommits().get("D");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(dCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("2.0.0"));
    }

    @Test
    public void version_of_E_commit() {
        ObjectId eCommit = scenario.getCommits().get("E");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(eCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("3.0.0-1"));
    }
    
    @Test
    public void version_of_F_commit() {
        ObjectId fCommit = scenario.getCommits().get("F");
        
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(fCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("3.0.0-2"));
    }

    @Test
    public void version_of_annotated_tags() {
        Arrays.asList("1.0.0", "2.0.0").forEach(tag -> {
            // when tag is checkout
            unchecked(() -> git.checkout().setName(tag).call());
            // the version matches the tag
            assertThat(versionCalculator.getVersion(), is(tag));
        });
    }

    @Test
    public void version_of_light_1_1_0_Snapshot() {
        // we checkout light tag 1.1.0
        unchecked(() -> git.checkout().setName("1.1.0-SNAPSHOT").call());
        // which is on a commit with annotated tag 1.0.0 that has precedence
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }
    
    @Test
    public void version_of_light_3_0_0_Snapshot() {
        // we checkout light tag 1.1.0
        unchecked(() -> git.checkout().setName("3.0.0-SNAPSHOT").call());
        // which is on a commit with annotated tag 1.0.0
        // that must have precedence
        assertThat(versionCalculator.getVersion(), is("2.0.0"));
    }
    
    @Test
    public void version_of_master() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("master").call());
        assertThat(versionCalculator.getVersion(), is("3.0.0-2"));

        assertThat(versionCalculator.meta(Metadatas.NEXT_MAJOR_VERSION).get(), is("3.0.0"));
        assertThat(versionCalculator.meta(Metadatas.NEXT_MINOR_VERSION).get(), is("3.0.0"));
        assertThat(versionCalculator.meta(Metadatas.NEXT_PATCH_VERSION).get(), is("3.0.0"));
    }
}
