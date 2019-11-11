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
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CLIQualifierBranchPoliciesTest extends ScenarioTest {

    public CLIQualifierBranchPoliciesTest() {
        super(
                Scenarios::s13_gitflow,
                calculator -> calculator.setStrategy(Strategies.CONFIGURABLE));
    }

    @Test
    public void version_of_feature() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call();
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2-feature_add_sso"));
    }

    @Test
    public void version_of_feature_other_pattern() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--branchPolicyPattern=release/(.*)");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2-feature_add_sso"));
    }

    @Test
    public void version_of_feature_branch_part() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--branchPolicyPattern=feature/(.*)");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2-add_sso"));
    }

    @Test
    public void version_of_feature_branch_part_extra_patterns() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--branchPolicyPattern=feature/(.*)",
                "--branchPolicyPattern=release/(.*)",
                "--branchPolicyPattern=foo");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2-add_sso"));
    }

    @Test
    public void version_of_feature_ignore() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--branchPolicyPattern=feature/(.*)", "--branchPolicyTransformations=IGNORE");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2"));
    }

    @Test
    public void version_of_feature_ignore_reversed() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call("--branchPolicyTransformations=IGNORE", "--branchPolicyPattern=feature/(.*)");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2"));
    }

    @Test
    public void version_of_feature_group_with_default() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call(
                "--branchPolicyPattern=feature/(.*)",
                "--branchPolicyPattern=release/(.*)",
                "--branchPolicyTransformations=IGNORE");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2-add_sso"));
    }

    @Test
    public void version_of_feature_group_with_default_elsewhere() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call(
                "--branchPolicyPattern=feature/(.*)",
                "--branchPolicyTransformations=IGNORE",
                "--branchPolicyPattern=release/(.*)");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2"));
    }

    @Test
    public void version_of_feature_groups() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        cliCaller.call(
                "--branchPolicyPattern=feature/(.*)",
                "--branchPolicyTransformations=IDENTITY,UPPERCASE",
                "--branchPolicyPattern=release/(.*)",
                "--branchPolicyTransformations=IGNORE");
        List<String> lines = cliCaller.getLines();
        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("1.0.1-2-ADD-SSO"));
    }

    @Test
    public void dangling_transformation_exception() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        assertThrows(
                IllegalArgumentException.class,
                () -> cliCaller.call("--branchPolicyTransformations=IGNORE")
        );
    }

    @Test
    public void dangling_transformations_exception() {
        unchecked(() -> git.checkout().setName("feature/add-sso").call());

        CLICaller cliCaller = new CLICaller(scenario.getRepositoryLocation());
        assertThrows(
                IllegalArgumentException.class,
                () -> cliCaller.call("--branchPolicyPattern=feature/(.*)",
                        "--branchPolicyTransformations=IDENTITY,UPPERCASE",
                        "--branchPolicyTransformations=IGNORE")
        );
    }
}
