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


import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.LookupPolicy;
import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import java.util.Optional;
import org.eclipse.jgit.api.MergeCommand;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for Issue #137.
 */
public class Issue137Test extends ScenarioTest {

    private static final String BASE_TAG_STR = "1.2.3";
    public Issue137Test() {
        super(Issue137Test::init_scenario,
                scenario -> scenario
                        .setStrategy(Strategies.CONFIGURABLE)  // Need to use a strategy which actually records the commit distance
                        .setLookupPolicy(LookupPolicy.MAX)
        );
    }

    // Build a scenario where commit E has two parents: C and D.
    // When FirstParentWalkDistanceCalculator gets to the point where it
    // analyzes E it will already have analysed its first parent, C,
    // via a different path (because C is also a child of F).
    // See graph on GitHub issue.
    // Until the fix for Issue-137 the commit D would never be analyzed.
    // This scenario will test if FirstParentWalkDistanceCalculator will
    // ever get to analyzing commit D as this is where we have the base tag.
    public static Scenarios.Scenario init_scenario() {
        return new Scenarios.ScenarioBuilder()
                .commit("content", "A")
                .branchOnAppId("branch1", "A")
                .branchOnAppId("branch2", "A")
                .master()
                .commit("content", "B")
                .checkoutBranch("branch1")
                .commit("content", "C")
                .checkoutBranch("branch2")
                .commit("content", "D").tag(BASE_TAG_STR)
                .branchOnAppId("branch3", "C")
                .merge("D", "E", MergeCommand.FastForwardMode.NO_FF)
                .master()
                .merge("C", "F", MergeCommand.FastForwardMode.NO_FF)
                .merge("E", "G", MergeCommand.FastForwardMode.NO_FF)
                .getScenario();
    }


    @Test
    public void check_commit_distance() {
        Optional<String> optDistance = versionCalculator.meta(Metadatas.COMMIT_DISTANCE);
        assertTrue(optDistance.isPresent());
        assertThat(optDistance.get(), is("2"));
    }


    @Test
    public void check_base_tag() {
        Optional<String> optBaseTag = versionCalculator.meta(Metadatas.BASE_TAG);
        assertTrue(optBaseTag.isPresent());
        assertThat(optBaseTag.get(), is(BASE_TAG_STR));
    }

}
