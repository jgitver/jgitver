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
package fr.brouillard.oss.jgitver.impl;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class GitUtils {
    public static String tagNameFromRef(Ref tag) {
        return tag.getName().replace("refs/tags/", "");
    }
    
    public static boolean isAnnotated(Ref ref) {
        return ref != null && ref.getPeeledObjectId() != null;
    }
    
    public static String sanitizeBranchName(String currentBranch) {
        return currentBranch.replaceAll("[\\s\\-#/\\\\]+", "_");
    }
    
    public static boolean isDetachedHead(Repository repository) throws IOException {
        return repository.getFullBranch().matches("[0-9a-f]{40}");
    }
    
    /**
     * Checks that underlying repository is dirty (modified with uncommitted changes).
     * @return true if the underlying repository is dirty, false otherwise
     * @throws GitAPIException if a git eeror occured while computing status
     * @throws NoWorkTreeException  if the underlying repsoitory directory is not git managed 
     */
    public static boolean isDirty(Git git) throws NoWorkTreeException, GitAPIException {
        Status status = git.status().call();
        return !status.isClean();
    }

    /**
     * Builds a string representing the given instant interpolated in the current system timezone
     * @param commitInstant commit time as an Instant
     * @return a string representing the commit time
     */
    public static String getTimestamp(Instant commitInstant) {
        LocalDateTime commitDateTime = LocalDateTime.ofInstant(commitInstant, ZoneId.systemDefault());
        String isoDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(commitDateTime);
        return isoDateTime
                .replace("-", "")
                .replace(":", "")
                .replace("T", "");
    }
}
