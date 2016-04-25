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

import static fr.brouillard.oss.jgitver.Lambdas.as;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitVersionCalculator implements AutoCloseable {
    private Repository repository;
    private boolean autoIncrementPatch = false;
    private boolean useDistance = true;
    private boolean useGitCommitId = false;
    private int gitCommitIdLength = 8;
    private String nonQualifierBranches = "master";

    private GitVersionCalculator(File gitRepositoryLocation) throws IOException {
        this.repository = openRepository(gitRepositoryLocation);
    }

    /**
     * Creates a {@link GitVersionCalculator} for the git repository pointing to the given path.
     * 
     * @param gitRepositoryLocation
     *            the location of the git repository to find version for
     * @return a non null {@link GitVersionCalculator}
     */
    public static GitVersionCalculator location(File gitRepositoryLocation) {
        GitVersionCalculator gvc;
        try {
            gvc = new GitVersionCalculator(gitRepositoryLocation);
            return gvc;
        } catch (IOException ex) {
            throw new IllegalStateException("cannot open git repository under: " + gitRepositoryLocation, ex);
        }
    }

    private Repository openRepository(File location) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.findGitDir(location).build();
    }

    /**
     * Calculates the version to use for the current git repository depending on the HEAD position.
     * 
     * @return a string representation of this version.
     */
    public String getVersion() {
        try (Git git = new Git(repository)) {
            Version v = findRelevantVersionFromTag(git);
            
            v = enhanceVersionWithBranch(v);
            
            if (v.isSnapshot()) {
                // reset the SNAPSHOT qualifier at the end
                v = v.removeQualifier("SNAPSHOT").addQualifier("SNAPSHOT");
            }
            
            return v.toString();
        }
    }

    private Version enhanceVersionWithBranch(Version version) {
        List<String> noQualifierForBranches = Arrays.asList(nonQualifierBranches.split("\\s*,\\s*"));
        
        try {
            String currentBranch = repository.getBranch();
            
            if (!noQualifierForBranches.contains(currentBranch) && !isDetachedHead(repository)) {
                // we need to append a branch qualifier
                return version.addQualifier(sanitizeBranchName(currentBranch));
            }
            
            return version;
        } catch (Exception ex) {
            throw new IllegalStateException("failure adding branch information to version " + version.toString(), ex);
        }
    }
    
    private String sanitizeBranchName(String currentBranch) {
        return currentBranch.replaceAll("[\\s\\-]+", "_");
    }

    private boolean isDetachedHead(Repository repository) throws IOException {
        return repository.getFullBranch().matches("[0-9a-f]{40}");
    }

    private Version findRelevantVersionFromTag(Git git) {
        try {
            // retrieve all tags matching a version, and get all info for each of them
            List<Ref> allTags = git.tagList().call().stream().filter(this::tagIsAVersionOne).map(this::peel)
                    .collect(Collectors.toCollection(ArrayList::new));
            // let's have tags sorted from most recent to oldest
            Collections.reverse(allTags);

            List<Ref> normals = allTags.stream().filter(this::isAnnotated).collect(Collectors.toList());
            List<Ref> lights = allTags.stream().filter(as(this::isAnnotated).negate()).collect(Collectors.toList());

            List<Ref> tags = new ArrayList<>();
            tags.addAll(lights);        // first add lights one for precedence on top of normals/annotated ones
            tags.addAll(normals);

            // let's find a tag with a version on it
            
            try (RevWalk revWalk = new RevWalk(repository)) {
                ObjectId rootId = repository.resolve("HEAD");
                revWalk.markStart(revWalk.parseCommit(rootId));

                int depth = 0;
                boolean tagIsOnHead = false;
                for (RevCommit rc : revWalk) {
                    final ObjectId id = rc.getId();
                    Optional<Ref> tagFound = findFirstTag(tags, id);
                    Optional<Ref> normalTagFound = findFirstTag(normals, id);

                    if (tagFound.isPresent()) {
                        if (normalTagFound.isPresent()) {
                            // we found a light tag on the same ObjectID than a normal tag
                            // If the HEAD is also on this revision then use the normal tag
                            if (rootId.equals(rc.getId())) {
                                tagFound = normalTagFound;
                                tagIsOnHead = true;
                            }
                        }
                        String tag = tagFound.get().getName().replace("refs/tags/", "");
                        Version v = Version.parse(tag);

                        if (!tagIsOnHead && autoIncrementPatch && isAnnotated(tagFound.get())) {
                            v = v.increasePatch();
                        }

                        if (useDistance && depth > 0 && !tagIsOnHead && !v.isSnapshot()) {
                            v = v.addQualifier("" + depth);
                        }
                        
                        if (useGitCommitId && !!tagIsOnHead && !v.isSnapshot()) {
                            v = v.addQualifier(rootId.getName().substring(0, gitCommitIdLength));
                        }

                        return v;
                    }
                    depth++;
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("failure calculating version", ex);
        }

        return Version.DEFAULT_VERSION;
    }

    private boolean isAnnotated(Ref ref) {
        return ref.getPeeledObjectId() != null;
    }

    private Optional<Ref> findFirstTag(List<Ref> tags, final ObjectId id) {
        return tags.stream().filter(ref -> id.equals(ref.getObjectId()) || id.equals(ref.getPeeledObjectId()))
                .findFirst();
    }

    private boolean tagIsAVersionOne(Ref tag) {
        // TODO provide a regexp find replace to handle common cases like:
        // v1.0 -> 1.0
        return true;
    }

    private Ref peel(Ref tag) {
        return repository.peel(tag);
    }

    @Override
    public void close() throws Exception {
        repository.close();
    }

    public GitVersionCalculator setAutoIncrementPatch(boolean value) {
        this.autoIncrementPatch = value;
        return this;
    }

    public GitVersionCalculator setNonQualifierBranches(String nonQualifierBranches) {
        this.nonQualifierBranches = nonQualifierBranches;
        return this;
    }

    public GitVersionCalculator setUseDistance(boolean useDistance) {
        this.useDistance = useDistance;
        return this;
    }

    public GitVersionCalculator setUseGitCommitId(boolean useGitCommitId) {
        this.useGitCommitId = useGitCommitId;
        return this;
    }

    public GitVersionCalculator setGitCommitIdLength(int gitCommitIdLength) {
        if (gitCommitIdLength < 8 || gitCommitIdLength > 40) {
            throw new IllegalStateException("GitCommitIdLength must be between 8 & 40");
        }
        this.gitCommitIdLength = gitCommitIdLength;
        return this;
    }
}
