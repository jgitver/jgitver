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
package fr.brouillard.oss.jgitver.strategy.maven.defaults;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;


public class Scenario20WithDefaultsTest extends ScenarioTest {

    public Scenario20WithDefaultsTest() {
        super(
                Scenarios.Builders.s20_release_on_head()::getScenario,
                calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void head_is_on_master_by_default() throws Exception {
        assertThat(repository.getBranch(), is("master"));
    }
    
    @Test
    public void version_of_master() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("master").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }
    
    @Test
    public void version_of_master_in_dirty_state() throws IOException {
        File dirtyFile = null;
        try {
            // checkout the commit in scenario
            unchecked(() -> git.checkout().setName("master").call());
            dirtyFile = scenario.makeDirty();
            assertThat(versionCalculator.getVersion(), is("1.0.1-SNAPSHOT"));
        } finally {
            if (dirtyFile != null) {
                dirtyFile.delete();
            }
        }
    }
}
