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

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.TestsTypes;

@Tag(TestsTypes.SLOW)
public class NoTagsBigRepositoryTest extends ScenarioTest {
    private static int NB_COMMITS = 2500;

    /**
     * Initialize the whole junit class tests ; creates the git scenario.
     */
    public NoTagsBigRepositoryTest() {
        super(
                NoTagsBigRepositoryTest::buildScenario,
                calc -> calc.setStrategy(Strategies.CONFIGURABLE)
                            .setUseDistance(true)
        );
    }

    static Scenarios.Scenario buildScenario() {
        long start = System.currentTimeMillis();
        try {
            Scenarios.ScenarioBuilder builder = new Scenarios.ScenarioBuilder();
            for (int i = 0; i < NB_COMMITS; i++) {
                builder.commit("commit " + i, "A" + i);
            }

            return builder
                    .commit("last commit", "A")
                    .master()
                    .getScenario();
        } finally {
            long end = System.currentTimeMillis();
            System.out.printf(
                    "building big scenario %s with %d commits took %dms%n",
                    NoTagsBigRepositoryTest.class.getSimpleName(),
                    NB_COMMITS,
                    end - start
            );
        }
    }

    @Test
    public void can_compute_version() {
        assertThat(versionCalculator.getVersion(), is("0.0.0-" + NB_COMMITS));
    }

    @Test
    public void can_compute_version_width_max_depth() {
        int maxDepth = 10;
        versionCalculator.setMaxDepth(maxDepth);
        assertThat(versionCalculator.getVersion(), is("0.0.0-" + maxDepth));
    }
}
