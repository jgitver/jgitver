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
package fr.brouillard.oss.jgitver;

import static fr.brouillard.oss.jgitver.impl.Lambdas.mute;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.api.MergeCommand;
import org.junit.jupiter.api.Test;

public class NearestPolicyWithEqualDistanceTest extends ScenarioTest {
    public NearestPolicyWithEqualDistanceTest() {
        super(NearestPolicyWithEqualDistanceTest::buildScenario, calculator -> {
            calculator.setLookupPolicy(LookupPolicy.LATEST)
                    .setAutoIncrementPatch(false)
                    .setUseDistance(true)
                    .setStrategy(Strategies.CONFIGURABLE);
        });
    }

    /**
     * Builds the following repository.
     * <pre>
     * *   600a017 - (HEAD -> master) D :: merge C into B (17 seconds ago) <Matthieu Brouillard>
     * |\
     * | * eea9071 - (tag: 2.0, br) content C (17 seconds ago) <Matthieu Brouillard>
     * |/
     * * f57604c - (tag: 1.0) content B (17 seconds ago) <Matthieu Brouillard>
     * * 0cece48 - content A (17 seconds ago) <Matthieu Brouillard>
     * </pre>
     * @return the built scenario
     */
    private static Scenarios.Scenario buildScenario() {
        Scenarios.ScenarioBuilder builder = new Scenarios.ScenarioBuilder();

        builder.commit("content", "A")
                .commit("content", "B")
                .branchOnAppId("br", "B")
                .commit("content", "C")
                .master()
                .merge("C", "D", MergeCommand.FastForwardMode.NO_FF);

        builder.tag("1.0", "B");

        // wait 2 seconds
        mute(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(2)));

        builder.tag("2.0", "C");

        return builder.getScenario();
    }

    @Test
    public void nearest_policy_takes_mos_recent_tag_for_tags_at_equal_distance() {
        versionCalculator.setLookupPolicy(LookupPolicy.NEAREST);
        assertThat(versionCalculator.getVersion(), is("2.0.0-1"));
    }
}
