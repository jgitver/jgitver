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
package fr.brouillard.oss.jgitver.strategy.configurable.others;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.ScenarioTest;

public class Scenario16UseDistanceWithMultiplePossiblePathsTest extends ScenarioTest {
    /**
     * Initialize the whole junit class tests ; creates the git scenario.
     */
    public Scenario16UseDistanceWithMultiplePossiblePathsTest() {
        super(
                Scenarios::s16_merges_with_short_path,
                calculator -> calculator.setStrategy(Strategies.CONFIGURABLE)
                    .setUseDistance(true)
        );
    }

    @Test
    public void version_of_I_commit() {
        ObjectId firstCommit = scenario.getCommits().get("I");

        // checkout I
        // possible paths
        // I -> B -> A
        // I -> E -> D -> C -> A
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-2"));
    }

    @Test
    public void version_of_J_commit() {
        ObjectId firstCommit = scenario.getCommits().get("J");

        // checkout J
        // possible paths
        // J -> I -> B -> A
        // J -> H -> G -> F -> A
        // J -> I -> E -> D -> C -> A
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-3"));
    }
}
