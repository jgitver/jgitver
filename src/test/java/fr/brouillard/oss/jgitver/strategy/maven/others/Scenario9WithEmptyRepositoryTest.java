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
package fr.brouillard.oss.jgitver.strategy.maven.others;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.ScenarioTest;

public class Scenario9WithEmptyRepositoryTest extends ScenarioTest {

    public Scenario9WithEmptyRepositoryTest() {
        super(
                Scenarios::s9_empty_repository,
                calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void call_on_empty_git_repository() {
        assertThat(versionCalculator.getVersion(), is(Version.EMPTY_REPOSITORY_VERSION.toString()));
    }
    
}
