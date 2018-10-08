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
package fr.brouillard.oss.jgitver.strategy.configurable.defaults;

import static fr.brouillard.oss.jgitver.Lambdas.unchecked;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;

import fr.brouillard.oss.jgitver.Scenarios;
import fr.brouillard.oss.jgitver.Strategies;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.strategy.ScenarioTest;

public class Scenario12WithDefaultsTest extends ScenarioTest {

    public Scenario12WithDefaultsTest() {
        super(
                Scenarios::s12_linear_with_RC_tags,
                calculator -> calculator.setStrategy(Strategies.CONFIGURABLE));
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
        assertThat(versionCalculator.getVersion(), is("1.0.0-0"));
    }
    
    @Test
    public void version_of_B_commit() {
        ObjectId bCommit = scenario.getCommits().get("B");
        
        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(bCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0-1"));
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
        assertThat(versionCalculator.getVersion(), is("1.0.0-rc01-1"));
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
        assertThat(versionCalculator.getVersion(), is("1.0.0-rc02-1"));
    }
    
    @Test
    public void version_of_G_commit() {
        ObjectId gCommit = scenario.getCommits().get("G");
        
        // checkout the first commit in scenario
        unchecked(() -> git.checkout().setName(gCommit.name()).call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }
    
    @Test
    public void version_of_tag_1_0_0() {
        unchecked(() -> git.checkout().setName("1.0.0").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));
    }

    @Test
    public void version_of_master() {
        // checkout the commit in scenario
        unchecked(() -> git.checkout().setName("master").call());
        assertThat(versionCalculator.getVersion(), is("1.0.0"));

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
            assertThat(versionCalculator.getVersion(), is("2.0.0-0"));
            versionCalculator.setUseDirty(true);
            assertThat(versionCalculator.getVersion(), is("2.0.0-0-dirty"));
        } finally {
            if (dirtyFile != null) {
                dirtyFile.delete();
            }
        }
    }
}
