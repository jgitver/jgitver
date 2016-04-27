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
import java.util.regex.Pattern;
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
    
    private String findTagVersionPattern = "v?([0-9]+(?:\\.[0-9]+){0,2}(?:-[a-zA-Z0-9\\-_]+)?)";
    private String extractTagVersionPattern = "$1";
    private Pattern findVersionPattern;

    private GitVersionCalculator(File gitRepositoryLocation) throws IOException {
        this.repository = openRepository(gitRepositoryLocation);
        findVersionPattern = Pattern.compile(findTagVersionPattern);
    }

    /**
     * Creates a {@link GitVersionCalculator} for the git repository pointing to the given path.
     * 
     * @param gitRepositoryLocation the location of the git repository to find version for
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
    
    private String extractVersionFromTag(String tagName) {
        return findVersionPattern.matcher(tagName).replaceAll(extractTagVersionPattern);
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
            List<Ref> allTags = git.tagList().call().stream().filter(this::isATagVersion).map(this::peel)
                    .collect(Collectors.toCollection(ArrayList::new));
            // let's have tags sorted from most recent to oldest
            Collections.reverse(allTags);

            List<Ref> normals = allTags.stream().filter(this::isAnnotated).collect(Collectors.toList());
            List<Ref> lights = allTags.stream().filter(as(this::isAnnotated).negate()).collect(Collectors.toList());

            List<Ref> tags = new ArrayList<>();
            tags.addAll(lights); // first add lights one for precedence on top of normals/annotated ones
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
                        String tag = extractVersionFromTag(tagNameFromRef(tagFound.get()));
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

    private boolean isATagVersion(Ref tag) {
        String tagName = tagNameFromRef(tag);
        return findVersionPattern.matcher(tagName).matches();
    }

    private String tagNameFromRef(Ref tag) {
        return tag.getName().replace("refs/tags/", "");
    }

    private Ref peel(Ref tag) {
        return repository.peel(tag);
    }

    @Override
    public void close() throws Exception {
        repository.close();
    }

    /**
     * When true, when the found tag to calculate a version for HEAD is a normal/annotated one, 
     * the semver patch version of the tag is increased by one ; except when the tag is on the HEAD itself.
     * This action is not in use if the SNAPSHOT qualifier is present on the found version or if the found tag is a lightweight one.
     * @param value if true and when found tag is not on HEAD, 
     *      then version returned will be the found version with patch number increased by one. default false.
     * @return itself to chain settings
     */
    public GitVersionCalculator setAutoIncrementPatch(boolean value) {
        this.autoIncrementPatch = value;
        return this;
    }

    /**
     * Defines a comma separated list of branches for which no branch name qualifier will be used. default "master".
     * Example: "master, integration"
     * @param nonQualifierBranches a comma separated list of branch name for which no branch name qualifier should be used,
     *      can be null and/or empty 
     * @return itself to chain settings
     */
    public GitVersionCalculator setNonQualifierBranches(String nonQualifierBranches) {
        this.nonQualifierBranches = Optional.ofNullable(nonQualifierBranches).orElse("");
        return this;
    }

    /**
     * When true, append a qualifier with the distance between the HEAD commit and the found commit with a version tag.
     * This qualifier is not used if the SNAPSHOT qualifier is used.
     *  
     * @param useDistance if true, a qualifier with found distance will be used.
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseDistance(boolean useDistance) {
        this.useDistance = useDistance;
        return this;
    }

    /**
     * When true, append the git commit id (SHA1) to the version.
     * This qualifier is not used if the SNAPSHOT qualifier is used.
     * 
     * @param useGitCommitId if true, a qualifier with SHA1 git commit will be used, default true
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseGitCommitId(boolean useGitCommitId) {
        this.useGitCommitId = useGitCommitId;
        return this;
    }

    /**
     * Defines how long the qualifier from SHA1 git commit has to be.
     * 
     * @param gitCommitIdLength the length of the SHA1 substring to use as qualifier, valid values [8, 40], default 8
     * @return itself to chain settings
     * @throws IllegalArgumentException in case the length is not in the range [8,40]
     */
    public GitVersionCalculator setGitCommitIdLength(int gitCommitIdLength) {
        if (gitCommitIdLength < 8 || gitCommitIdLength > 40) {
            throw new IllegalStateException("GitCommitIdLength must be between 8 & 40");
        }
        this.gitCommitIdLength = gitCommitIdLength;
        return this;
    }
}
