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

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.BranchingPolicy;
import fr.brouillard.oss.jgitver.BranchingPolicy.BranchNameTransformations;
import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.ScenarioTest;

public class Scenario13GitflowWithNonQualifierAndPartialNameTest extends ScenarioTest {

    public Scenario13GitflowWithNonQualifierAndPartialNameTest() {
        super(
                Scenarios::s13_gitflow,
                calculator -> calculator
                        .setStrategy(Strategies.MAVEN)
                        .setQualifierBranchingPolicies(
                                BranchingPolicy.ignoreBranchName("master"),
                                BranchingPolicy.fixedBranchName("develop"),
                                new BranchingPolicy("release/(.*)", Collections.singletonList(BranchNameTransformations.IGNORE.name())),
                                new BranchingPolicy("feature/(.*)", Arrays.asList(
                                        BranchNameTransformations.REMOVE_UNEXPECTED_CHARS.name(),
                                        BranchNameTransformations.LOWERCASE_EN.name())
                                )
                        )
                        .setUseDefaultBranchingPolicy(false));
    }

    @Test
    public void head_is_on_master_by_default() throws Exception {
        assertThat(repository.getBranch(), is("master"));
    }
    
    @Test
    public void version_of_master() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("master").call());
        assertThat(versionCalculator.getVersion(), is("3.0.0"));

        assertThat(versionCalculator.meta(Metadatas.NEXT_MAJOR_VERSION).get(), is("4.0.0"));
        assertThat(versionCalculator.meta(Metadatas.NEXT_MINOR_VERSION).get(), is("3.1.0"));
        assertThat(versionCalculator.meta(Metadatas.NEXT_PATCH_VERSION).get(), is("3.0.1"));
    }
    
    @Test
    public void version_of_branch_release_1x() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("release/1.x").call());
        assertThat(versionCalculator.getVersion(), is("1.0.1-SNAPSHOT"));
    }
    
    @Test
    public void version_of_branch_release_2x() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("release/2.x").call());
        assertThat(versionCalculator.getVersion(), is("2.0.0-SNAPSHOT"));
    }
    
    @Test
    public void version_of_branch_develop() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("develop").call());
        assertThat(versionCalculator.getVersion(), is("1.0.1-develop-SNAPSHOT"));
    }
    
    @Test
    public void version_of_a_feature_branch() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("feature/add-sso").call());
        assertThat(versionCalculator.getVersion(), is("1.0.1-addsso-SNAPSHOT"));
    }
}
