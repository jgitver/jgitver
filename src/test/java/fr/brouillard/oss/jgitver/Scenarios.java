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
package fr.brouillard.oss.jgitver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.google.common.io.Files;

public class Scenarios {
    /**
     * Builds the following repository
     * <pre>
$ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
* 80eee6d - (18 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
* 98358d0 - (18 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
* 00a993e - (18 seconds ago) content C - Matthieu Brouillard
* 183ccc6 - (18 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
* b048402 - (18 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s1_linear_with_only_annotated_tags() {
        return new ScenarioBuilder()
            .commit("content", "A")
            .commit("content", "B")
            .tag("1.0.0")
            .commit("content", "C")
            .commit("content", "D")
            .tag("2.0.0")
            .commit("content", "E")
            .master()
            .getScenario();
    }
    
    /**
     * Builds the following repository, tag 1.1.0 is a lightweight one, others are annotated ones
     * <pre>
$ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
* 47eb212 - (60 seconds ago) content E - Matthieu Brouillard  (HEAD -> master)
* 01ee9e7 - (60 seconds ago) content D - Matthieu Brouillard  (tag: 2.0.0)
* 84afe52 - (60 seconds ago) content C - Matthieu Brouillard
* 6b4a7d2 - (60 seconds ago) content B - Matthieu Brouillard  (tag: 1.1.0, tag: 1.0.0)
* 368516a - (60 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s2_linear_with_both_tags() {
        return new ScenarioBuilder()
            .commit("content", "A")
            .commit("content", "B")
            .tag("1.0.0")
            .tagLight("1.1.0")
            .commit("content", "C")
            .commit("content", "D")
            .tag("2.0.0")
            .commit("content", "E")
            .master()
            .getScenario();
    }
    
    /**
     * Builds the following repository, tags 1.1.0-SNAPSHOT &amp; 3.0.0-SNAPSHOT are lightweight ones, others are annotated ones
     * <pre>
$ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
* 64a5bf6 - (60 seconds ago) content F - Matthieu Brouillard  (HEAD -> master)
* 47eb212 - (60 seconds ago) content E - Matthieu Brouillard  
* 01ee9e7 - (60 seconds ago) content D - Matthieu Brouillard  (tag: 3.0.0-SNAPSHOT, 2.0.0)
* 84afe52 - (60 seconds ago) content C - Matthieu Brouillard
* 6b4a7d2 - (60 seconds ago) content B - Matthieu Brouillard  (tag: 1.1.0-SNAPSHOT, tag: 1.0.0)
* 368516a - (60 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s3_linear_with_snapshots_light_tags() {
        return new ScenarioBuilder()
                .commit("content", "A")
                .commit("content", "B")
                .tag("1.0.0")
                .tagLight("1.1.0-SNAPSHOT")
                .commit("content", "C")
                .commit("content", "D")
                .tag("2.0.0")
                .tagLight("3.0.0-SNAPSHOT")
                .commit("content", "E")
                .commit("content", "F")
                .master()
                .getScenario();
    }
    
    /**
     * Builds the following repository
     * <pre> 
$ git lg
* 7454c23 - (23 seconds ago) content G - Matthieu Brouillard (issue-10)
* 17716f2 - (23 seconds ago) content F - Matthieu Brouillard
| * 6769dbc - (23 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
| * e5c7b86 - (23 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
|/
* 4640726 - (23 seconds ago) content C - Matthieu Brouillard
* e6231b0 - (23 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
* b4e3196 - (23 seconds ago) content A - Matthieu Brouillard
    </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s4_linear_with_only_annotated_tags_and_branch() {
        return new ScenarioBuilder()
            .commit("content", "A")
            .commit("content", "B")
            .tag("1.0.0")
            .commit("content", "C")
            .commit("content", "D")
            .tag("2.0.0")
            .commit("content", "E")
            .branchOnAppId("issue-10", "C")
            .commit("content", "F")
            .commit("content", "G")
            .master()
            .getScenario();
    }
    
    /**
     * Builds the following repository
     * <pre>
$ git lg
* 1b48dc9 - (19 seconds ago) content D - Matthieu Brouillard (dev)
| * 1e563e6 - (19 seconds ago) content C - Matthieu Brouillard (int)
|/
| * 5a7d916 - (19 seconds ago) content B - Matthieu Brouillard (HEAD -> master)
|/
* 338e4e2 - (19 seconds ago) content A - Matthieu Brouillard (tag: 1.0.0)
     * </pre> 
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s5_several_branches() {
        return new ScenarioBuilder()
                .commit("content", "A")
                .tag("1.0.0")
                .commit("content", "B")
                .branchOnAppId("int", "A")
                .commit("content", "C")
                .branchOnAppId("dev", "A")
                .commit("content", "D")
                .master()
                .getScenario();
    }
    
    /**
     * Builds the following repository
     * <pre>
$ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
* 80eee6d - (18 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
* 98358d0 - (18 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
* 00a993e - (18 seconds ago) content C - Matthieu Brouillard
* 183ccc6 - (18 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
* b048402 - (18 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s6_matching_and_non_matching_versions_tags() {
        return new ScenarioBuilder()
            .commit("content", "A")
            .tag("1.0")
            .commit("content", "B")
            .tag("v2.0")
            .commit("content", "C")
            .tag("a3.0")
            .commit("content", "D")
            .tag("dummy")
            .commit("content", "E")
            .master()
            .getScenario();
    }

    public static class Scenario {
        private File repositoryLocation;
        private Map<String, ObjectId> commits;
        
        public Scenario(File repository) {
            this.repositoryLocation = repository;
            commits = new HashMap<>();
        }

        public File getRepositoryLocation() {
            return repositoryLocation;
        }

        public Map<String, ObjectId> getCommits() {
            return commits;
        }
    }
    
    public static class ScenarioBuilder {
        private Scenario scenario;
        private Repository repository;
        private Git git;

        /**
         * Creates a ScenarioBuilder object pointing to a temporary fresh new & empty git repository. 
         */
        public ScenarioBuilder() {
            try {
                this.scenario = new Scenario(new File(Files.createTempDir(), ".git"));
                this.repository = FileRepositoryBuilder.create(scenario.getRepositoryLocation());
                repository.create();
                this.git = new Git(repository);
            } catch (Exception ex) {
                throw new IllegalStateException("failure building scenario", ex);
            }
        }

        /**
         * Reset the current repository to the master HEAD.
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder master() {
            try {
                git.checkout().setName("master").call();
            } catch (Exception ex) {
                throw new IllegalStateException("cannot checkout master", ex);
            }
            return this;
        }
        
        /**
         * Creates a branch on the git commit corresponding to the given application commit id.
         * @param branchName the branch to be created
         * @param id the application ID to retrieve the git commit from
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder branchOnAppId(String branchName, String id) {
            String commitId = scenario.getCommits().get(id).name();
            try {
                git.checkout().setName(branchName).setCreateBranch(true).setStartPoint(commitId).call();
            } catch (Exception ex) {
                throw new IllegalStateException("cannot create branch: " + branchName + " on ID " + commitId, ex);
            }
            return this;
        }

        /**
         * Creates a normal/annotated tag at the current HEAD.
         * @param tagName the name of the normal/annotated tag
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder tag(String tagName) {
            return tag(tagName, false);
        }
        
        private ScenarioBuilder tag(String tagName, boolean light) {
            try {
                git.tag().setName(tagName).setAnnotated(!light).call();
            } catch (Exception ex) {
                throw new IllegalStateException(String.format("cannot add tag: %s, lightweight[%s]", tagName, light), ex);
            }
            return this;
        }
        
        /**
         * Creates a light tag at the current HEAD.
         * @param tagName the name of the light tag
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder tagLight(String tagName) {
            return tag(tagName, true);
        }

        /**
         * Creates a commit in the repo, by modifying the given file.
         * The git commitID will be stored in the scenario in front of the given app identifier. 
         * @param fileName the filename to be touched, added and commited into the repo.
         * @param id the application identifier to use to store the git commitID in front of
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder commit(String fileName, String id) {
            File content = new File(scenario.getRepositoryLocation(), fileName);
            try {
                AddCommand add = git.add().addFilepattern(fileName);
                Files.touch(content);
                add.call();
                RevCommit rc = git.commit().setMessage("content " + id).call();
                scenario.getCommits().put(id, rc.getId());
            } catch (Exception ex) {
                throw new IllegalStateException(String.format("error creating a commit with new file %s", content), ex);
            }
            return this;
        }

        public Scenario getScenario() {
            return scenario;
        }

        public Repository getRepository() {
            return repository;
        }

        public Git git() {
            return git;
        }
    }
}
