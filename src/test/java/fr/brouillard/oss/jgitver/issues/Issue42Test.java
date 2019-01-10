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
package fr.brouillard.oss.jgitver.issues;

import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.Scenarios.Scenario;
import fr.brouillard.oss.jgitver.Scenarios.ScenarioBuilder;
import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public class Issue42Test {
    private static final String BRANCH_NAME = "my-branch";
    private static final String TAG_1_2_3 = "1.2.3";
    private static final String TAG_3_2_1 = "3.2.1";
    private static Scenario s;

    private Git git;
    private Repository repository;

    @BeforeAll
    public static void init_scenario() {
        s = new ScenarioBuilder()
                .commit("content", "A")
                .tag(TAG_1_2_3)
                .commit("content", "B")
                .tag(TAG_3_2_1)
                .getScenario();

        if (Misc.isDebugMode()) {
            System.out.println("git repository created under: " + s.getRepositoryLocation());
        }
    }

    @BeforeEach
    public void init() throws IOException {
        repository = new FileRepositoryBuilder().setGitDir(s.getRepositoryLocation()).build();
        git = new Git(repository);
    }

    @Test
    public void check_metadatas_on_tag_1_2_3() {
        unchecked(() -> git.checkout().setName(TAG_1_2_3).call());

        GitVersionCalculator gvc = GitVersionCalculator.location(s.getRepositoryLocation());
        gvc.setMavenLike(true);
        Version versionObject = gvc.getVersionObject();

        assertThat(versionObject.getMajor(), CoreMatchers.is(1));
        assertThat(gvc.meta(Metadatas.CURRENT_VERSION_MAJOR).get(), CoreMatchers.is("1"));

        assertThat(versionObject.getMinor(), CoreMatchers.is(2));
        assertThat(gvc.meta(Metadatas.CURRENT_VERSION_MINOR).get(), CoreMatchers.is("2"));

        assertThat(versionObject.getPatch(), CoreMatchers.is(3));
        assertThat(gvc.meta(Metadatas.CURRENT_VERSION_PATCH).get(), CoreMatchers.is("3"));
    }

    @Test
    public void check_metadatas_on_tag_3_2_1() {
        unchecked(() -> git.checkout().setName(TAG_3_2_1).call());

        GitVersionCalculator gvc = GitVersionCalculator.location(s.getRepositoryLocation());
        gvc.setMavenLike(true);
        Version versionObject = gvc.getVersionObject();

        assertThat(versionObject.getMajor(), CoreMatchers.is(3));
        assertThat(gvc.meta(Metadatas.CURRENT_VERSION_MAJOR).get(), CoreMatchers.is("3"));

        assertThat(versionObject.getMinor(), CoreMatchers.is(2));
        assertThat(gvc.meta(Metadatas.CURRENT_VERSION_MINOR).get(), CoreMatchers.is("2"));

        assertThat(versionObject.getPatch(), CoreMatchers.is(1));
        assertThat(gvc.meta(Metadatas.CURRENT_VERSION_PATCH).get(), CoreMatchers.is("1"));
    }

    @AfterAll
    public static void cleanup() {
        Misc.deleteDirectorySimple(s.getRepositoryLocation());
    }
}
