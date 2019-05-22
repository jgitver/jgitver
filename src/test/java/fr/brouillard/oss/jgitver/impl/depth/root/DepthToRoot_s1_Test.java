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
package fr.brouillard.oss.jgitver.impl.depth.root;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.impl.GitUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepthToRoot_s1_Test extends ScenarioTest {
    public DepthToRoot_s1_Test() {
        super(Scenarios::s1_linear_with_only_annotated_tags, calculator -> calculator.setStrategy(Strategies.CONFIGURABLE).setUseDistance(true));
    }

    @Test
    public void commit_distances_to_root() {
        assertEquals(0, GitUtils.distanceToRoot(repository, scenario.getCommits().get("A")));
        assertEquals(1, GitUtils.distanceToRoot(repository, scenario.getCommits().get("B")));
        assertEquals(2, GitUtils.distanceToRoot(repository, scenario.getCommits().get("C")));
        assertEquals(3, GitUtils.distanceToRoot(repository, scenario.getCommits().get("D")));
        assertEquals(4, GitUtils.distanceToRoot(repository, scenario.getCommits().get("E")));
    }
}
