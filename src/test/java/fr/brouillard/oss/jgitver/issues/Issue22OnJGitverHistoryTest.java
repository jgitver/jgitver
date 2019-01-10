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

import java.io.File;
import java.io.IOException;
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

public class Issue22OnJGitverHistoryTest {
    private static File cloneDir;
    private static File repoDir;

    private Repository repository;
    private GitVersionCalculator versionCalculator;
    private Git git;
    private static TimeZone defaultTZ;
    
    @BeforeAll
    public static void initialization() throws GitAPIException {
        defaultTZ = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));

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
                .setMavenLike(false)
                .setUseDistance(false)        
                .setUseGitCommitId(false)
                .setUseGitCommitTimestamp(true);
    }

    @Test
    public void check_commit_F0E315BC69EC53C3F1F72D54D63F9145012776A1() throws IOException {
/*
Commit: f0e315bc69ec53c3f1f72d54d63f9145012776a1 [f0e315b]
Parents: 9660113279
Author: Matthieu Brouillard <matthieu@brouillard.fr>
Date: jeudi 23 février 2017 19:13:09
Committer: Matthieu Brouillard
Commit Date: vendredi 24 février 2017 09:39:10
*/
        String commitId = "F0E315BC69EC53C3F1F72D54D63F9145012776A1";
        unchecked(() -> git.checkout().setName(commitId).call());
        assertThat(versionCalculator.getVersion(), is("0.3.0-20170223191309"));
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
