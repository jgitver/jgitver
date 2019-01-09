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
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class LookupPolicyTest extends ScenarioTest {
    public LookupPolicyTest() {
        super(LookupPolicyTest::buildScenario, calculator -> {
            calculator.setLookupPolicy(LookupPolicy.LATEST)
                    .setAutoIncrementPatch(false)
                    .setStrategy(Strategies.CONFIGURABLE);
        });
    }

    private static Scenarios.Scenario buildScenario() {
        Scenarios.ScenarioBuilder builder = new Scenarios.ScenarioBuilder();

        builder.commit("content", "A")
                .commit("content", "B")
                .commit("content", "C")
                .commit("content", "D");

        builder.tag("1.0", "C");
        builder.tag("3.0", "A");

        // wait 2 seconds
        mute(() -> Thread.sleep(TimeUnit.SECONDS.toMillis(2)));

        builder.tag("2.0", "B");

        return builder.getScenario();
    }

    @Test
    public void latest_policy_took_most_recent_tag_by_date_of_tag() {
        assertThat(versionCalculator.getVersion(), is("2.0.0-2"));
    }

    @Test
    public void max_policy_takes_max_tag_by_date_of_tag() {
        versionCalculator.setLookupPolicy(LookupPolicy.MAX);
        assertThat(versionCalculator.getVersion(), is("3.0.0-3"));
    }
}
