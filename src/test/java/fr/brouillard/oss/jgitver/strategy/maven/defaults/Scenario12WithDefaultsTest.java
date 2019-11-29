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
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.ScenarioTest;

public class Scenario12WithDefaultsTest extends ScenarioTest {

    public Scenario12WithDefaultsTest() {
        super(
                Scenarios::s12_linear_with_RC_tags,
                calculator -> calculator.setStrategy(Strategies.MAVEN));
    }

    @Test
    public void head_is_on_master_by_default() throws Exception {
        assertThat(repository.getBranch(), is("master"));
    }

    @Test
    public void version_of_A_commit() {
        ObjectId aCommit = scenario.getCommits().get("A");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(aCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-SNAPSHOT"));
    }

    @Test
    public void version_of_B_commit() {
        ObjectId bCommit = scenario.getCommits().get("B");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(bCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-SNAPSHOT"));
    }

    @Test
    public void version_of_C_commit() {
        ObjectId cCommit = scenario.getCommits().get("C");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(cCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-rc01"));
    }

    @Test
    public void version_of_D_commit() {
        ObjectId dCommit = scenario.getCommits().get("D");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(dCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-SNAPSHOT"));
    }

    @Test
    public void version_of_E_commit() {
        ObjectId eCommit = scenario.getCommits().get("E");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(eCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-rc02"));
    }

    @Test
    public void version_of_F_commit() {
        ObjectId fCommit = scenario.getCommits().get("F");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(fCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-SNAPSHOT"));
    }

    @Test
    public void version_of_G_commit() {
        ObjectId gCommit = scenario.getCommits().get("G");

        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(gCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }

    @Test
    public void version_of_master() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("master").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));

        assertThat(versionCalculator.meta(Metadatas.CALCULATED_VERSION).get(), is("1.0.0"));
        assertThat(versionCalculator.meta(Metadatas.BRANCH_NAME).get(), is("master"));
        assertThat(versionCalculator.meta(Metadatas.BASE_TAG).get(), is("1.0.0"));
        assertThat(versionCalculator.meta(Metadatas.DIRTY).get(), is("false"));
        
        // as the repo is clean there must be no meta for DIRTY_TEXT 
        assertFalse(versionCalculator.meta(Metadatas.DIRTY_TEXT).isPresent());

        // TODO open a defect in jgit, order of tags is not respected between v1.0.0 & 1.0.0
        // assertThat(versionCalculator.meta(Metadatas.ALL_TAGS).get(), is("v2.0.0,1.0.0,1.0.0-rc02,1.0.0-rc01,v1.0.0"));
        // assertThat(versionCalculator.meta(Metadatas.ALL_ANNOTATED_TAGS).get(), is("1.0.0,1.0.0-rc02,1.0.0-rc01"));
        assertThat(versionCalculator.meta(Metadatas.ALL_LIGHTWEIGHT_TAGS).get(), is("v2.0.0,v1.0.0"));
        // assertThat(versionCalculator.meta(Metadatas.ALL_VERSION_TAGS).get(), is("v2.0.0,1.0.0,1.0.0-rc02,1.0.0-rc01,v1.0.0"));
        // assertThat(versionCalculator.meta(Metadatas.ALL_VERSION_ANNOTATED_TAGS).get(), is("1.0.0,1.0.0-rc02,1.0.0-rc01"));
        assertThat(versionCalculator.meta(Metadatas.ALL_VERSION_LIGHTWEIGHT_TAGS).get(), is("v2.0.0,v1.0.0"));
        assertThat(versionCalculator.meta(Metadatas.HEAD_TAGS).get(), is("v2.0.0,1.0.0"));
        assertThat(versionCalculator.meta(Metadatas.HEAD_ANNOTATED_TAGS).get(), is("1.0.0"));
        assertThat(versionCalculator.meta(Metadatas.HEAD_LIGHTWEIGHT_TAGS).get(), is("v2.0.0"));

        ObjectId headCommit = scenario.getCommits().get("G");
        assertThat(versionCalculator.meta(Metadatas.GIT_SHA1_FULL).get(), is(headCommit.name()));
        assertThat(versionCalculator.meta(Metadatas.GIT_SHA1_8).get(), is(headCommit.abbreviate(8).name()));

        assertThat(versionCalculator.meta(Metadatas.NEXT_MAJOR_VERSION).get(), is("2.0.0"));
        assertThat(versionCalculator.meta(Metadatas.NEXT_MINOR_VERSION).get(), is("1.1.0"));
        assertThat(versionCalculator.meta(Metadatas.NEXT_PATCH_VERSION).get(), is("1.0.1"));
    }
    
    @Test
    public void version_of_master_in_dirty_state() throws IOException {
        File dirtyFile = null;
        try {
            // checkout the commit in scenario
            unchecked(() -> git.checkout().setName("master").call());
            dirtyFile = scenario.makeDirty();
            assertThat(versionCalculator.getVersion(), is("2.0.0-SNAPSHOT"));
            assertThat(versionCalculator.meta(Metadatas.DIRTY).get(), is("true"));
            assertThat(versionCalculator.meta(Metadatas.DIRTY_TEXT).get(), is("dirty"));
        } finally {
            if (dirtyFile != null) {
                dirtyFile.delete();
            }
        }
    }
}
