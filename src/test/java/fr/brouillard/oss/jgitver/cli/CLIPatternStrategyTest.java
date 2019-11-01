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

import fr.brouillard.oss.jgitver.ScenarioTest;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;


public class CLIPatternStrategyTest extends ScenarioTest {

    public CLIPatternStrategyTest() {
        super(
                Scenarios::s1_linear_with_only_annotated_tags,
                calculator -> calculator.setStrategy(Strategies.PATTERN));
    }

    @Test
    public void default_tag_version_pattern_repeats_tag() {
        Arrays.asList("1.0.0", "2.0.0").forEach(tag -> {
            // when tag is checkout
            unchecked(() -> git.checkout().setName(tag).call());

            CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
            cliCaller.call("--strategy=PATTERN");
            List<String> lines = cliCaller.getLines();
            assertThat(lines.size(), is(1));
            // the version matches the tag
            assertThat(lines.get(0), is(tag));
        });
    }

    @Test
    public void version_pattern_overridden_on_tag() {
        Arrays.asList("1.0.0", "2.0.0").forEach(tag -> {
            // when tag is checkout
            unchecked(() -> git.checkout().setName(tag).call());

            CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
            cliCaller.call("--strategy=PATTERN", "--versionPattern=0.0.0");
            List<String> lines = cliCaller.getLines();
            assertThat(lines.size(), is(1));
            // the version matches the tag
            assertThat(lines.get(0), is(tag));
        });
    }

    @Test
    public void custom_tag_version_pattern() {
        Arrays.asList("1.0.0", "2.0.0").forEach(tag -> {
            // when tag is checkout
            unchecked(() -> git.checkout().setName(tag).call());

            CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
            cliCaller.call("--strategy=PATTERN", "--tagVersionPattern=${meta.HEAD_TAGS}-test");
            List<String> lines = cliCaller.getLines();
            assertThat(lines.size(), is(1));
            // the version matches the tag plus "-test"
            assertThat(lines.get(0), is(tag + "-test"));
        });
    }

    @Test
    public void custom_version_pattern_on_commit() {
        ObjectId cCommit = scenario.getCommits().get("C");

        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--strategy=PATTERN", "--versionPattern=${v}${<meta.BRANCH_NAME}${<meta.GIT_SHA1_8}");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the pattern, with the branch name omitted because there isn't one
        assertThat(lines.get(0), startsWith("1.0.1-"));
        assertThat(lines.get(0).length(), is(14));
    }

    @Test
    public void custom_version_pattern_on_branch() {
        // checkout master in scenario
        unchecked(() -> git.checkout().setName("master").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--strategy=PATTERN", "--versionPattern=${v}${<meta.BRANCH_NAME}${<meta.GIT_SHA1_8}", "--autoIncrementPatch=false");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        // the version matches the pattern
        assertThat(lines.get(0), startsWith("2.0.0-master."));
        assertThat(lines.get(0).length(), is(21));
    }
}
