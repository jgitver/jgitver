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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.TestsTypes;

@Tag(TestsTypes.SLOW)
public class BigRepositoryWithMultiBranchesTest extends ScenarioTest {
    private static int NB_COMMITS_PER_BRANCH = 2500;

    /**
     * Initialize the whole junit class tests ; creates the git scenario.
     */
    public BigRepositoryWithMultiBranchesTest() {
        super(
                BigRepositoryWithMultiBranchesTest::buildScenario,
                calc -> calc.setStrategy(Strategies.CONFIGURABLE)
                            .setAutoIncrementPatch(true)
                            .setUseDistance(true)
        );
    }

    /**
     * Builds repository with many commits and a tag on the branch
     * <pre>
     * {@code
     * *   318c3c9 (HEAD -> master) C :: merge BL into master
     * |\
     * | * 4d841f6 (branchB) 'last B' BL
     * | . ...
     * | * xxxxxxx (branchB) commit x Bx
     * | * a50dde7 (branchB, tag: 2.0.0) 'start of branch B' B
     * . | ...
     * * | xxxxxxx commit x Ax
     * |/
     * * d74b251 (tag: 1.0.0) start A
     * }
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    static Scenarios.Scenario buildScenario() {
        long start = System.currentTimeMillis();
        try {
            Scenarios.ScenarioBuilder builder = new Scenarios.ScenarioBuilder();
            builder.commit("start", "A");
            builder.tag("1.0.0");

            for (int i = 0; i < NB_COMMITS_PER_BRANCH; i++) {
                builder.commit("commit " + i, "A" + i);
            }

            builder.branchOnAppId("branchB", "A");
            builder.commit("start of branch B", "B");
            builder.tag("2.0.0");

            for (int i = 0; i < NB_COMMITS_PER_BRANCH; i++) {
                builder.commit("commit on branch B" + i, "B" + i);
            }
            builder.commit("last B", "BL");

            builder.master();
            builder.merge("BL", "C");

            return builder
                    .getScenario();
        } finally {
            long end = System.currentTimeMillis();
            System.out.printf("building big scenario %s took %dms%n", BigRepositoryWithMultiBranchesTest.class.getSimpleName(), end - start);
        }
    }

    @Test
    public void can_compute_version() {
        int expectedDistance = NB_COMMITS_PER_BRANCH
                                + 1         // last commit in branch B
                                + 1;         // merge commit;
        assertThat(versionCalculator.getVersion(), is("2.0.1-" + expectedDistance));
    }
}
