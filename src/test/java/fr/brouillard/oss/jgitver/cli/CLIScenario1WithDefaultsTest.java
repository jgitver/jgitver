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
package fr.brouillard.oss.jgitver.cli;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;


public class CLIScenario1WithDefaultsTest extends ScenarioTest {

    public CLIScenario1WithDefaultsTest() {
        super(
                Scenarios::s1_linear_with_only_annotated_tags,
                calculator -> calculator.setStrategy(Strategies.CONFIGURABLE));
    }

    @Test
    public void head_is_on_master_by_default() throws Exception {
        assertThat(repository.getBranch(), is("master"));
    }
    
    @Test
    public void version_on_normal_tag_is_tag_value() {
        Arrays.asList("1.0.0", "2.0.0").forEach(tag -> {
            // when tag is checkout
            unchecked(() -> git.checkout().setName(tag).call());

            CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
            cliCaller.call();
            List<String> lines = cliCaller.getLines();
            assertThat(lines.size(), is(1));
            // the version matches the tag
            assertThat(lines.get(0), is(tag));
        });
    }
    
    @Test
    public void version_of_A_commit() {
        ObjectId cCommit = scenario.getCommits().get("A");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call();
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the tag
        assertThat(lines.get(0), is("0.0.0-0"));
    }

    @Test
    public void version_of_B_commit() {
        ObjectId cCommit = scenario.getCommits().get("B");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call();
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the tag
        assertThat(lines.get(0), is("1.0.0"));
    }

    @Test
    public void version_of_C_commit() {
        ObjectId cCommit = scenario.getCommits().get("C");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call();
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the tag
        assertThat(lines.get(0), is("1.0.1-1"));
    }

    @Test
    public void version_of_D_commit() {
        ObjectId cCommit = scenario.getCommits().get("D");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call();
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the tag
        assertThat(lines.get(0), is("2.0.0"));
    }

    @Test
    public void version_of_E_commit() {
        ObjectId cCommit = scenario.getCommits().get("E");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call();
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the tag
        assertThat(lines.get(0), is("2.0.1-1"));
    }

    @Test
    public void version_of_master() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("master").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--metas=CALCULATED_VERSION,NEXT_MAJOR_VERSION,NEXT_MINOR_VERSION,NEXT_PATCH_VERSION");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(4));

        assertThat(lines.get(0), is("CALCULATED_VERSION=2.0.1-1"));
        assertThat(lines.get(1), is("NEXT_MAJOR_VERSION=3.0.0"));
        assertThat(lines.get(2), is("NEXT_MINOR_VERSION=2.1.0"));
        assertThat(lines.get(3), is("NEXT_PATCH_VERSION=2.0.1"));
    }
}
