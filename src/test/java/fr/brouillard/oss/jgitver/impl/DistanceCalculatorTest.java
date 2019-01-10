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
package fr.brouillard.oss.jgitver.impl;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;

public class DistanceCalculatorTest extends ScenarioTest {
    public DistanceCalculatorTest() {
        super(Scenarios::s4_linear_with_only_annotated_tags_and_branch, calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void distance_from_head_to_head_is_0() throws Exception {
        ObjectId eCommit = scenario.getCommits().get("E");
        unchecked(() -> git.checkout().setName(eCommit.name()).call());

        ObjectId headId = repository.resolve("HEAD");
        DistanceCalculator distanceCalculator = DistanceCalculator.create(headId, repository);

        Optional<Integer> distanceTo = distanceCalculator.distanceTo(headId);

        assertThat("distance to head should always return a value", distanceTo, notNullValue());
        assertTrue(distanceTo.isPresent(), "distance to head should always return a non empty value");
        assertThat(distanceTo.get(), is(0));
    }

    @Test
    public void distance_from_E_to_G_cannot_be_computed() throws Exception {
        ObjectId eCommit = scenario.getCommits().get("E");
        ObjectId gCommit = scenario.getCommits().get("G");
        unchecked(() -> git.checkout().setName(eCommit.name()).call());

        DistanceCalculator distanceCalculator = DistanceCalculator.create(eCommit, repository);

        Optional<Integer> distanceTo = distanceCalculator.distanceTo(gCommit);

        assertThat("distance to head should always return a value", distanceTo, notNullValue());
        assertFalse(distanceTo.isPresent(), "distance to unreachable commit should be empty");
    }

    @Test
    public void distance_from_E_to_A_is_4() throws Exception {
        ObjectId eCommit = scenario.getCommits().get("E");
        ObjectId aCommit = scenario.getCommits().get("A");
        unchecked(() -> git.checkout().setName(eCommit.name()).call());

        DistanceCalculator distanceCalculator = DistanceCalculator.create(eCommit, repository);

        Optional<Integer> distanceTo = distanceCalculator.distanceTo(aCommit);

        assertThat("distance to head should always return a value", distanceTo, notNullValue());
        assertThat(distanceTo.get(), is(4));
    }
}
