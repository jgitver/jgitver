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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import fr.brouillard.oss.jgitver.Lambdas;
import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.metadata.TagType;

public abstract class MaxVersionStrategy<T extends MaxVersionStrategy> extends VersionStrategy {
    protected boolean useMaxVersion;
    protected int maxVersionSearchDepth = 1000;

    protected MaxVersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git, MetadataRegistrar metadatas) {
        super(vnc, repository, git, metadatas);
    }

    @Override
    public StrategySearchMode searchMode() {
        return useMaxVersion ? StrategySearchMode.DEPTH : StrategySearchMode.STOP_AT_FIRST;
    }

    @Override
    public int searchDepthLimit() {
        return maxVersionSearchDepth;
    }

    protected Version getBaseVersionAndRegisterMetadata(Commit base, Ref tagToUse) {
        Version baseVersion = Version.DEFAULT_VERSION;

        if (tagToUse != null) {
            String tagName = GitUtils.tagNameFromRef(tagToUse);
            TagType tagType = computeTagType(tagToUse, maxVersionTag(base.getAnnotatedTags()).orElse(null));
            baseVersion = tagToVersion(tagName);

            getRegistrar().registerMetadata(Metadatas.BASE_TAG_TYPE, tagType.name());
            getRegistrar().registerMetadata(Metadatas.BASE_TAG, tagName);
        }
        
        getRegistrar().registerMetadata(Metadatas.BASE_VERSION, baseVersion.toString());
        getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_MAJOR, Integer.toString(baseVersion.getMajor()));
        getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_MINOR, Integer.toString(baseVersion.getMinor()));
        getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_PATCH, Integer.toString(baseVersion.getPatch()));

        return baseVersion;
    }

    public T setUseMaxVersion(boolean useMaxVersion) {
        return runAndGetSelf(() -> this.useMaxVersion = useMaxVersion);
    }

    public T setMaxVersionSearchDepth(int maxVersionSearchDepth) {
        return runAndGetSelf(() -> this.maxVersionSearchDepth = maxVersionSearchDepth);
    }

    protected T runAndGetSelf(Runnable runnable) {
        runnable.run();
        return self();
    }

    protected T self() {
        return (T) this;
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
        return useMaxVersion ? findMaxVersionCommit(head, parents) : parents.get(0);
    }

    protected Commit findMaxVersionCommit(Commit head, List<Commit> parents) {
        return parents.stream()
                .limit(maxVersionSearchDepth)
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

    protected VersionTarget<Ref> toVersionTarget(Ref tagRef) {
        String tagName = GitUtils.tagNameFromRef(tagRef);
        return new VersionTarget<>(tagToVersion(tagName), tagRef);
    }

    protected VersionTarget<Commit> toVersionTarget(Commit head, Commit commit) {
        String tagName = GitUtils.tagNameFromRef(findTagToUse(head, commit));
        Version version = Version.parse(tagName);
        return new VersionTarget<>(version, commit);
    }

    private static class VersionTarget<T> implements Comparable<VersionTarget<T>> {
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