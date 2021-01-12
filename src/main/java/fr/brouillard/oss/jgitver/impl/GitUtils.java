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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.brouillard.oss.jgitver.impl.jgit.root.RootCommit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.DepthWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;

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

    public static List<Ref> tagsOf(List<Ref> tags, final ObjectId id) {
        return tags.stream().filter(ref -> id.equals(ref.getObjectId()) || id.equals(ref.getPeeledObjectId()))
                .collect(Collectors.toList());
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
     * Builds a string representing the given instant interpolated in the current system timezone.
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

    /**
     * Builds a string representing the given instant interpolated in the UTC timezone.
     * @param commitInstant commit time as an Instant
     * @return a string representing the commit time in ISO format in UTC timezone
     */
    public static String getIsoTimestamp(Instant commitInstant) {
    	OffsetDateTime commitDateTime = OffsetDateTime.ofInstant(commitInstant, ZoneId.of("Z"));
        String isoDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(commitDateTime);
        return isoDateTime;
    }

    /**
     * Provide the branch name externally provided in case we are building on a detached branch.
     * It looks at:
     * <ul>
     *     <li>System property: `jgitver.branch`</li>
     *     <li>Environment variable: `JGITVER_BRANCH`</li>
     * </ul>
     * @return the found branch name in the form of an optional.
     */
    public static Optional<String> providedBranchName() {
        return Optional.ofNullable(System.getProperty(BRANCH_SYSTEM_PROPERTY, System.getenv(BRANCH_ENV_VARIABLE)));
    }

    /**
     * Computes inside the given repository the distance between the provided object and the root of the repository.
     * @param repository the git repository
     * @param objectId the id of the start of the distance computation
     * @return the computed distance
     */
    public static int distanceToRoot(Repository repository, AnyObjectId objectId) {
        try (RootCommit.RootWalk rootWalk = new RootCommit.RootWalk(repository)) {
            RevCommit commit = repository.parseCommit(objectId);
            rootWalk.markStart(commit);
            Iterator<RevCommit> rootsIT = rootWalk.iterator();

            if (!rootsIT.hasNext()) {
                throw new IllegalStateException("could not find at least one root commit in the repository");
            }

            RevCommit foundRoot = rootsIT.next();
            return DistanceCalculator.create(objectId, repository).distanceTo(foundRoot.getId()).get();
        } catch (IOException e) {
            throw new IllegalStateException("failure retrieving root commit in the repository");
        }
    }

    public static DistanceCalculator.CalculatorKind calculatorBuilder() {
        return DistanceCalculator.CalculatorKind.valueOf(System.getProperty(CALCULATOR_KIND_SYSTEM_PROPERTY, DistanceCalculator.CalculatorKind.FIRST_PARENT.name()));
    }


    public static final String PREFIX_SYSTEM_PROPERTY = "jgitver.";
    private static final String BRANCH_SYSTEM_PROPERTY = "jgitver.branch";
    private static final String CALCULATOR_KIND_SYSTEM_PROPERTY = "jgitver.calculator.kind";
    private static final String BRANCH_ENV_VARIABLE = "JGITVER_BRANCH";
}
