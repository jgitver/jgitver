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
package fr.brouillard.oss.jgitver.strategy.pattern.others;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.impl.VersionCalculationException;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Scenario1WithInvalidPatternConfigurationTest {

    @Nested
    class NonSemverPattern extends ScenarioTest {

        NonSemverPattern() {
            super(
                    Scenarios::s1_linear_with_only_annotated_tags,
                    calculator -> calculator.setStrategy(Strategies.PATTERN)
                            .setVersionPattern("nonSemver"));
        }

        @Test
        void versionCalculationException() {
            ObjectId commit = scenario.getCommits().get("A");

            // checkout the commit in scenario
            unchecked(() -> git.checkout().setName(commit.name()).call());
            final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> versionCalculator.getVersion());

            assertThat(illegalStateException.getCause(), instanceOf(VersionCalculationException.class));
        }
    }

    @Nested
    class NonSemverTagPattern extends ScenarioTest {

        NonSemverTagPattern() {
            super(
                    Scenarios::s1_linear_with_only_annotated_tags,
                    calculator -> calculator.setStrategy(Strategies.PATTERN)
                            .setTagVersionPattern("nonSemverTagVersionPattern"));
        }

        @Test
        void versionCalculationException() {
            ObjectId commit = scenario.getCommits().get("B");

            // checkout the commit in scenario
            unchecked(() -> git.checkout().setName(commit.name()).call());
            final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> versionCalculator.getVersion());

            assertThat(illegalStateException.getCause(), instanceOf(VersionCalculationException.class));
        }
    }
}
