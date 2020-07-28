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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.metadata.TagType;

public abstract class VersionStrategy<T extends VersionStrategy> {
    private VersionNamingConfiguration vnc;
    private Repository repository;
    private Git git;
    private MetadataRegistrar registrar;
    private int searchDepthLimit = Integer.MAX_VALUE;

    protected MetadataRegistrar getRegistrar() {
        return registrar;
    }

    /**
     * Default constructor.
     * @param vnc the configuration to use
     * @param repository the git repository
     * @param git a git helper object built from the repository
     * @param registrar a storage for found/calculated metadata
     */
    public VersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git, MetadataRegistrar registrar) {
        this.vnc = vnc;
        this.repository = repository;
        this.git = git;
        this.registrar = registrar;
    }

    /**
     * Build a version using the given information extracted from the git repository.
     * 
     * @param head cannot be null the current head commit
     * @param parents a non null list of commits that will be involved in version naming. 
     *      The list cannot be null and contains the first commit of the repository if no commit with version tag can be found.
     * @return a non null Version object
     * @throws VersionCalculationException in case an error occurred while computing the version 
     */
    public abstract Version build(Commit head, List<Commit> parents) throws VersionCalculationException;

    /**
     * Defines the history commit depth, starting from HEAD, until which parent commits will be parsed to find tags
     * information. This parameter is informative and will be respected only if at least one commit with version
     * information is found between HEAD and the defined depth. If none is found then the search will go deeper until it
     * find one commit with version information or until it reaches the first commit. Valid only when the {@link #searchMode()}
     * is {@link VersionStrategy.StrategySearchMode#DEPTH}.
     * 
     * @return a strict positive integer representing the depth until which the search will stop.
     */
    public int searchDepthLimit() {
        return searchDepthLimit;
    }

    /**
     * Sets a limit to the depth of lookup for base version tags.
     * @param newLimit a positive value or 0 if the given value is negative
     * @return self instance of the strategy object to allow method chaining
     */
    public T setSearchDepthLimit(int newLimit) {
        return runAndGetSelf(() -> searchDepthLimit = (newLimit > 0) ? newLimit : 0);
    }

    /**
     * Checks regarding the current strategy if the given {@link Ref} corresponds to a tag of a version.
     * @param tag a non null tag as a {@link Ref} object
     * @return true if the given tag matches the version rules of this strategy, false otherwise
     */
    public boolean isVersionTag(Ref tag) {
        String tagName = tagNameFromRef(tag);
        return getVersionNamingConfiguration().getSearchPattern().matcher(tagName).matches();
    }

    public StrategySearchMode searchMode() {
        return StrategySearchMode.STOP_AT_FIRST;
    }

    private String tagNameFromRef(Ref tag) {
        return tag.getName().replace("refs/tags/", "");
    }

    protected TagType computeTagType(Ref tagToUse, Ref annotatedTag) {
        if (annotatedTag != null) {
            if (tagToUse.getObjectId().toString().equals(annotatedTag.getObjectId().toString())) {
                return TagType.ANNOTATED;
            }
        }
        return TagType.LIGHTWEIGHT;
    }

    protected T runAndGetSelf(Runnable runnable) {
        runnable.run();
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    protected Version getBaseVersionAndRegisterMetadata(Commit base, Ref tagToUse) {
        Version baseVersion = Version.DEFAULT_VERSION;

        if (tagToUse != null) {
            String tagName = GitUtils.tagNameFromRef(tagToUse);
            baseVersion = versionFromTag(tagToUse);
            TagType tagType = computeTagType(tagToUse, maxVersionTag(base.getAnnotatedTags()).orElse(null));

            getRegistrar().registerMetadata(Metadatas.BASE_TAG_TYPE, tagType.name());
            getRegistrar().registerMetadata(Metadatas.BASE_TAG, tagName);
        }

        try {
            getRegistrar().registerMetadata(Metadatas.DETACHED_HEAD, "" + GitUtils.isDetachedHead(getRepository()));
        } catch (IOException ioe) {
            // ignore
        }
        getRegistrar().registerMetadata(Metadatas.ANNOTATED,"" + GitUtils.isAnnotated(tagToUse));
        getRegistrar().registerMetadata(Metadatas.BASE_VERSION, baseVersion.toString());
        getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_MAJOR, Integer.toString(baseVersion.getMajor()));
        getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_MINOR, Integer.toString(baseVersion.getMinor()));
        getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_PATCH, Integer.toString(baseVersion.getPatch()));

        return baseVersion;
    }

    protected Version tagToVersion(String tagName) {
        return Version.parse(getVersionNamingConfiguration().extractVersionFrom(tagName));
    }

    protected boolean isGitDirty() {
        return Lambdas.unchecked(GitUtils::isDirty).apply(getGit());
    }

    protected Ref findTagToUse(Commit head, Commit base) {
        return isBaseCommitOnHead(head, base) && !isGitDirty()
                ? maxVersionTag(base.getAnnotatedTags(), base.getLightTags())
                : maxVersionTag(base.getLightTags(), base.getAnnotatedTags());
    }

    protected Commit findVersionCommit(Commit head, List<Commit> parents) {
        if (!head.getAnnotatedTags().isEmpty() || !head.getLightTags().isEmpty()) {
            return head;
        }

        return parents.size() > 1 ? findMaxVersionCommit(head, parents) : parents.get(0);
    }

    protected Commit findMaxVersionCommit(Commit head, List<Commit> parents) {
        return parents.stream()
                .map(commit -> toVersionTarget(head, commit))
                .max(Comparator.naturalOrder())
                .map(VersionTarget::getTarget)
                .orElse(parents.get(0));
    }

    protected Ref maxVersionTag(List<Ref> primaryTags, List<Ref> secondaryTags) {
        return maxVersionTag(primaryTags).orElseGet(() -> maxVersionTag(secondaryTags).orElse(null));
    }

    protected Optional<Ref> maxVersionTag(List<Ref> tags) {
        return tags.stream()
                .map(this::toVersionTarget)
                .max(Comparator.naturalOrder())
                .map(VersionTarget::getTarget);
    }

    /**
     * Computes a {@link Version} object from the given tag reference using the strategy configuration.
     * @param ref a git {@link Ref} object corresponding to the tag
     * @return the corresponding Version object
     */
    public Version versionFromTag(Ref ref) {
        return tagToVersion(GitUtils.tagNameFromRef(ref));
    }

    protected VersionTarget<Ref> toVersionTarget(Ref tagRef) {
        return new VersionTarget<>(versionFromTag(tagRef), tagRef);
    }

    protected VersionTarget<Commit> toVersionTarget(Commit head, Commit commit) {
        Ref tagToUse = findTagToUse(head, commit);
        return new VersionTarget<>(versionFromTag(tagToUse), commit);
    }

    public enum StrategySearchMode {
        /**
         * Search will stop on first commit having at least one tag with version information.
         */
        STOP_AT_FIRST,
        /**
         * Search go deep in the git commit history tree to find all relevant commits having at least one tag with
         * version information. The search will respect {@link VersionStrategy#searchDepthLimit()} defined value.
         */
        DEPTH;
    }

    protected VersionNamingConfiguration getVersionNamingConfiguration() {
        return vnc;
    }

    protected Repository getRepository() {
        return repository;
    }

    protected Git getGit() {
        return git;
    }

    protected boolean isBaseCommitOnHead(Commit head, Commit base) {
        return head.getGitObject().name().equals(base.getGitObject().name());
    }

    protected Version enhanceVersionWithBranch(Version baseVersion, String branch) {
        getRegistrar().registerMetadata(Metadatas.BRANCH_NAME, branch);

        // let's add a branch qualifier if one is computed
        Optional<String> branchQualifier = getVersionNamingConfiguration().branchQualifier(branch);
        if (branchQualifier.isPresent()) {
            getRegistrar().registerMetadata(Metadatas.QUALIFIED_BRANCH_NAME, branchQualifier.get());
            baseVersion = baseVersion.addQualifier(branchQualifier.get());
        }
        return baseVersion;
    }

    private static class VersionTarget<T> implements Comparable<VersionStrategy.VersionTarget<T>> {
        private final Version version;
        private final T target;

        VersionTarget(Version version, T target) {
            this.version = version;
            this.target = target;
        }

        Version getVersion() {
            return version;
        }

        T getTarget() {
            return target;
        }

        @Override
        public int compareTo(VersionTarget versionTarget) {
            return this.version.compareTo(versionTarget.version);
        }
    }
}
