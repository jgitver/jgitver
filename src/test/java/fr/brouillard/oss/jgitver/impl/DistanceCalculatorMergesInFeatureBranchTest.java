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
import fr.brouillard.oss.jgitver.impl.jgit.root.RootCommit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistanceCalculatorMergesInFeatureBranchTest extends ScenarioTest {
    public DistanceCalculatorMergesInFeatureBranchTest() {
        super(Scenarios.Builders.s19_merges_in_feature_branch()::getScenario, calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void root_walk_finds_A() throws IOException {
        RootCommit.RootWalk walk = new RootCommit.RootWalk(repository);
        ObjectId headID = scenario.getCommits().get("E");
        walk.markStart(repository.parseCommit(headID));

        Iterator<RevCommit> rootsIt = walk.iterator();


        assertThat(rootsIt, notNullValue());
        assertTrue(rootsIt.hasNext());

        ObjectId aID = scenario.getCommits().get("A");
        ObjectId rootId = rootsIt.next().getId();

        assertEquals(aID, rootId);

        assertFalse(rootsIt.hasNext());
    }

    @Test
    public void distance_from_E_to_A_is_3() throws Exception {
        assertDistanceIs("E", "A", 3);
    }

    @Test
    public void distance_from_E_to_C1_is_5() throws Exception {
        assertDistanceIs("E", "C1", 5);
    }

    @Test
    public void distance_from_E_to_D1_is_5() throws Exception {
        assertDistanceIs("E", "D1", 5);
    }

    @Test
    public void distance_from_E_to_root() {
        Assertions.assertEquals(3, GitUtils.distanceToRoot(repository, scenario.getCommits().get("E")));
    }
}
