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

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DistanceCalculatorShortFeatureBranchesTest extends ScenarioTest {
    public DistanceCalculatorShortFeatureBranchesTest() {
        super(Scenarios.Builders.s17_feature_branches_with_shorter_path()::getScenario, calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void distance_from_E_to_G_cannot_be_computed() throws Exception {
        ObjectId eCommit = scenario.getCommits().get("E");
        ObjectId gCommit = scenario.getCommits().get("G");

        DistanceCalculator distanceCalculator = DistanceCalculator.create(eCommit, repository);

        Optional<Integer> distanceTo = distanceCalculator.distanceTo(gCommit);

        assertThat("distanceTo should always return a value", distanceTo, notNullValue());
        assertFalse(distanceTo.isPresent(), "distance to unreachable commit should be empty");
    }

    @Test
    public void distance_from_D_to_A_is_3() throws Exception {
        assertDistance(scenario.getCommits().get("D"), scenario.getCommits().get("A"), 3);
    }

    @Test
    public void distance_from_E_on_b1_to_A_is_1() throws Exception {
        assertDistance(scenario.getCommits().get("E"), scenario.getCommits().get("A"), 1);
    }

    @Test
    public void distance_from_M1_to_A_is_4() throws Exception {
        assertDistance(scenario.getCommits().get("M1"), scenario.getCommits().get("A"), 4);
    }

    @Test
    public void distance_from_F_on_b2_to_A_is_1() throws Exception {
        assertDistance(scenario.getCommits().get("F"), scenario.getCommits().get("A"), 1);
    }

    @Test
    public void distance_from_H_to_A_is_6() throws Exception {
        assertDistance(scenario.getCommits().get("H"), scenario.getCommits().get("A"), 6);
    }

    @Test
    public void distance_from_M2_to_A_is_7() throws Exception {
        assertDistance(scenario.getCommits().get("M2"), scenario.getCommits().get("A"), 7);
    }

    @Test
    public void distance_from_I_to_A_is_8() throws Exception {
        assertDistance(scenario.getCommits().get("I"), scenario.getCommits().get("A"), 8);
    }

    @Test
    public void master_distance_to_root() throws Exception {
        Ref master = git.checkout().setName("master").call();
        int distance = GitUtils.distanceToRoot(repository, master.getObjectId());
        assertEquals(8, distance);
    }

	private void assertDistance(ObjectId eCommit, ObjectId aCommit, int distance) {
        DistanceCalculator distanceCalculator = DistanceCalculator.create(eCommit, repository);

        Optional<Integer> distanceTo = distanceCalculator.distanceTo(aCommit);

        assertThat("distance to head should always return a value", distanceTo, notNullValue());
        assertThat(distanceTo.get(), is(distance));
	}
    
}
