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
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios.Scenario;
import fr.brouillard.oss.jgitver.Scenarios.ScenarioBuilder;
import fr.brouillard.oss.jgitver.Strategies;

/**
 * Integration test class to test {@link DistanceCalculator} on a real repository. Adapt constants below manually and enable test.
 */
@Disabled
public class DistanceCalculatorIntegrationTest extends ScenarioTest {
    static Supplier<Scenario> intScenario;

    static final String REPO_PATH = "/path/to/repo/myrepo/.git";

    static final String REPO_BRANCH = "develop";

    static {
        ScenarioBuilder scenarioBuilder = new ScenarioBuilder(new File(REPO_PATH));
        scenarioBuilder.checkoutBranch(REPO_BRANCH);
        intScenario = () -> scenarioBuilder.getScenario();
    }

    public DistanceCalculatorIntegrationTest() {
        super(intScenario, calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void distance_must_match() throws Exception {
        ObjectId firstCommit = repository.resolve("4141110d44e1fb3e6ed4818277644182ac923a69");
        ObjectId secondCommit = repository.resolve("838b0ad411c23800f7778197eb037226509f6475");
        unchecked(() -> git.checkout().setName(firstCommit.name()).call());

        DistanceCalculator distanceCalculator = DistanceCalculator.create(firstCommit, repository);

        Optional<Integer> distanceTo = distanceCalculator.distanceTo(secondCommit);

        assertThat("distance to head should always return a value", distanceTo, notNullValue());
        assertThat(distanceTo.get(), is(49));
    }

    /**
     * Cleanup the whole junit scenario ; deletes the created git repository.
     */
    @AfterAll
    public static void cleanupClass() {
        // do nothing, e.g. don't delete directory like it is done in ScenarioTest
    }
}
