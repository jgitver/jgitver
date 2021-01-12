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

import static fr.brouillard.oss.jgitver.impl.Lambdas.mute;
import static fr.brouillard.oss.jgitver.impl.Lambdas.unchecked;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.Files;

import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.Misc;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public class Issue35ForCommitTimestampOnJGitverHistoryTest {
    private static File cloneDir;
    private static File repoDir;

    private Repository repository;
    private GitVersionCalculator versionCalculator;
    private Git git;
    private static TimeZone defaultTZ;
    
    @BeforeAll
    public static void initialization() throws GitAPIException {
        defaultTZ = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2:00"));

        cloneDir = new File(Files.createTempDir(), "jgitver");
        repoDir = new File(cloneDir, ".git");
        String jgitverSourceDirectoryURI = new File(System.getProperty("user.dir")).toURI().toString();

        Git.cloneRepository()
                .setURI(jgitverSourceDirectoryURI)
                .setDirectory( cloneDir )
                .setBare(false)
                .setCloneAllBranches(true)
                .setBranch("master")
                .call();

        if (Misc.isDebugMode()) {
            System.out.println("jgitver repository cloned under: " + cloneDir);
        }
    }
    
    @BeforeEach
    public void init() throws IOException {
        repository = new FileRepositoryBuilder().setGitDir(repoDir).build();
        git = new Git(repository);
        versionCalculator = GitVersionCalculator.location(repoDir)
                .setMavenLike(false);
    }

    @Test
    public void check_commit_6F9A3BD838BAD514BF994605F6888035EC87289B() throws IOException {
/*
commit 6f9a3bd838bad514bf994605f6888035ec87289b
Author:     Matthieu Brouillard <matthieu@brouillard.fr>
AuthorDate: Mon Jun 3 08:17:02 2019 +0200
Commit:     Matthieu Brouillard <matthieu@brouillard.fr>
CommitDate: Mon Jun 3 08:17:02 2019 +0200
*/
        String commitId = "6F9A3BD838BAD514BF994605F6888035EC87289B";
        unchecked(() -> git.checkout().setName(commitId).call());

        Optional<String> commitTimestampMeta = versionCalculator.meta(Metadatas.COMMIT_TIMESTAMP);
        assertTrue(commitTimestampMeta.isPresent());
        assertThat(commitTimestampMeta.get(), is("20190603081702"));

        Optional<String> commitIsoTimestampMeta = versionCalculator.meta(Metadatas.COMMIT_ISO_TIMESTAMP);
        assertTrue(commitIsoTimestampMeta.isPresent());
        assertThat(commitIsoTimestampMeta.get(), is("2019-06-03T06:17:02Z"));
}

    @AfterEach
    public void cleanup() {
        mute(() -> git.close());
        mute(() -> repository.close());
        mute(() -> versionCalculator.close());
    }
    
    @AfterAll
    public static void reset() {
        TimeZone.setDefault(defaultTZ);

        try {
            Misc.deleteDirectorySimple(cloneDir);
        } catch (Exception ignore) {
            System.err.println("cannot remove " + cloneDir);
        }
    }
}
