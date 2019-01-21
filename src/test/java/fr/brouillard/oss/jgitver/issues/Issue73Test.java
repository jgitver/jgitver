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

import static fr.brouillard.oss.jgitver.impl.Lambdas.mute;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.LookupPolicy;
import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;

public class Issue73Test extends ScenarioTest {
    private static final String TAG_0_1_0 = "0.1.0";
    private static final String TAG_1_0_0 = "1.0.0";
    private static Scenarios.Scenario s;

    public Issue73Test() {
        super(
                Issue73Test::init_scenario,
                scenario -> scenario.setStrategy(Strategies.CONFIGURABLE)
                        .setLookupPolicy(LookupPolicy.LATEST)
                        .setAutoIncrementPatch(true)
        );
    }

    public static Scenarios.Scenario init_scenario() {
        Scenarios.ScenarioBuilder builder = new Scenarios.ScenarioBuilder()
                .commit("content", "A")
                .commit("content", "B")
                .commit("content", "C")
                .tag(TAG_1_0_0);

        // wait 2 seconds
        mute(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(2)));
        builder.tagLight(TAG_0_1_0, "B");
        builder.master();
        return builder.getScenario();
    }

    @Test
    public void evict_lightweight_tag_due_to_policy() {
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }
}
