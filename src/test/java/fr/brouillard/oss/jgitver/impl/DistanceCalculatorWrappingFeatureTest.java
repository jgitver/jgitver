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
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DistanceCalculatorWrappingFeatureTest extends ScenarioTest {
    public DistanceCalculatorWrappingFeatureTest() {
        super(Scenarios.Builders.s18_wrapped_feature_branches()::getScenario, calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void distance_from_H_to_E2_is_3() throws Exception {
        ObjectId hCommit = scenario.getCommits().get("H");
        ObjectId eCommit = scenario.getCommits().get("E2");

        assertDistanceIs(hCommit, eCommit, 3);
    }

    @Test
    public void distance_from_H_to_E1_is_4() throws Exception {
        ObjectId hCommit = scenario.getCommits().get("H");
        ObjectId eCommit = scenario.getCommits().get("E1");

        assertDistanceIs(hCommit, eCommit, 4);
    }

    @Test
    public void distance_from_H_to_C2_is_2() throws Exception {
        ObjectId hCommit = scenario.getCommits().get("H");
        ObjectId cCommit = scenario.getCommits().get("C2");

        assertDistanceIs(hCommit, cCommit, 2);
    }

    @Test
    public void distance_from_H_to_C1_is_3() throws Exception {
        ObjectId hCommit = scenario.getCommits().get("H");
        ObjectId cCommit = scenario.getCommits().get("C1");

        assertDistanceIs(hCommit, cCommit, 3);
    }

    @Test
    public void distance_from_H_to_A_is_5() throws Exception {
        ObjectId hCommit = scenario.getCommits().get("H");
        ObjectId cCommit = scenario.getCommits().get("A");

        assertDistanceIs(hCommit, cCommit, 5);
    }

    @Test
    public void distance_from_H_to_B_is_4() throws Exception {
        ObjectId hCommit = scenario.getCommits().get("H");
        ObjectId cCommit = scenario.getCommits().get("B");

        assertDistanceIs(hCommit, cCommit, 4);
    }
}
