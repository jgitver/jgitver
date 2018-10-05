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
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import fr.brouillard.oss.jgitver.Lambdas;
import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.VersionCalculationException;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.metadata.TagType;

public class ConfigurableVersionStrategy extends VersionStrategy {
    private boolean autoIncrementPatch = false;
    private boolean useDistance = true;
    private boolean useCommitTimestamp = false;
    private boolean useGitCommitId = false;
    private int gitCommitIdLength = 8;
    private boolean useDirty = false;
    private boolean useLongFormat;
    private boolean useMaxVersion;
    private int maxVersionSearchDepth = 1000;

    public ConfigurableVersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git, MetadataRegistrar metadatas) {
        super(vnc, repository, git, metadatas);
    }

    public ConfigurableVersionStrategy setAutoIncrementPatch(boolean autoIncrementPatch) {
        this.autoIncrementPatch = autoIncrementPatch;
        return this;
    }

    public ConfigurableVersionStrategy setUseDistance(boolean useDistance) {
        this.useDistance = useDistance;
        return this;
    }

    public ConfigurableVersionStrategy setUseCommitTimestamp(boolean useCommitTimestamp) {
        this.useCommitTimestamp = useCommitTimestamp;
        return this;
    }

    public ConfigurableVersionStrategy setUseGitCommitId(boolean useGitCommitId) {
        this.useGitCommitId = useGitCommitId;
        return this;
    }

    public ConfigurableVersionStrategy setGitCommitIdLength(int gitCommitIdLength) {
        this.gitCommitIdLength = gitCommitIdLength;
        return this;
    }

    public ConfigurableVersionStrategy setUseDirty(boolean useDirty) {
        this.useDirty = useDirty;
        return this;
    }

    public ConfigurableVersionStrategy setUseLongFormat(boolean useLongFormat) {
        this.useLongFormat = useLongFormat;
        return this;
    }

    public ConfigurableVersionStrategy setUseMaxVersion(boolean useMaxVersion) {
        this.useMaxVersion = useMaxVersion;
        return this;
    }

    public ConfigurableVersionStrategy setMaxVersionSearchDepth(int maxVersionSearchDepth) {
        this.maxVersionSearchDepth = maxVersionSearchDepth;
        return this;
    }

    @Override
    public StrategySearchMode searchMode() {
        return useMaxVersion ? StrategySearchMode.DEPTH : StrategySearchMode.STOP_AT_FIRST;
    }

    @Override
    public int searchDepthLimit() {
        return maxVersionSearchDepth;
    }

    @Override
    public Version build(Commit head, List<Commit> parents) throws VersionCalculationException {
        try {
            Commit base = findVersionCommit(head, parents);
            Ref tagToUse = findTagToUse(head, base);
            Version baseVersion = Version.DEFAULT_VERSION;

            if (tagToUse != null) {
                String tagName = GitUtils.tagNameFromRef(tagToUse);
                TagType tagType = computeTagType(tagToUse, base.getAnnotatedTags().stream().findFirst().orElse(null));
                getRegistrar().registerMetadata(Metadatas.BASE_TAG_TYPE, tagType.name());
                getRegistrar().registerMetadata(Metadatas.BASE_TAG, tagName);
                baseVersion = tagToVersion(tagName);
            }
            getRegistrar().registerMetadata(Metadatas.BASE_VERSION, baseVersion.toString());
            getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_MAJOR, "" + baseVersion.getMajor());
            getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_MINOR, "" + baseVersion.getMinor());
            getRegistrar().registerMetadata(Metadatas.CURRENT_VERSION_PATCH, "" + baseVersion.getPatch());

            final boolean useSnapshot = baseVersion.isSnapshot();

            if (!isBaseCommitOnHead(head, base) && autoIncrementPatch && !useLongFormat) {
                // we are not on head
                if (GitUtils.isAnnotated(tagToUse)) {
                    // found tag to use was an annotated one, lets' increment the version automatically
                    baseVersion = baseVersion.incrementPatch();
                }
            }

            if ((useDistance || useLongFormat) && !useSnapshot) {
                if (tagToUse == null) {
                    // no tag was found, let's count from initial commit
                    baseVersion = baseVersion.addQualifier("" + base.getHeadDistance());
                } else {
                    // use distance when long format is asked
                    // or not on head
                    // or if on head with a light tag
                    if (useLongFormat || !isBaseCommitOnHead(head, base) || !GitUtils.isAnnotated(tagToUse)) {
                        baseVersion = baseVersion.addQualifier("" + base.getHeadDistance());
                    }
                }
            }

            getRegistrar().registerMetadata(Metadatas.COMMIT_DISTANCE, "" + base.getHeadDistance());

            boolean needsCommitTimestamp = useCommitTimestamp && !useSnapshot;

            try (RevWalk walk = new RevWalk(getRepository())) {
                RevCommit rc = walk.parseCommit(head.getGitObject());
                String commitTimestamp = GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_TIMESTAMP, commitTimestamp);
                if (needsCommitTimestamp) {
                    baseVersion = baseVersion.addQualifier(commitTimestamp);
                }
            }

            boolean needsCommitId = useGitCommitId
                    && !(isBaseCommitOnHead(head, base)
                    && !baseVersion.noQualifier().equals(Version.DEFAULT_VERSION));

            if (useLongFormat || needsCommitId) {
                String commitIdQualifier =
                        (useLongFormat ? "g" : "") + head.getGitObject().getName().substring(0, useLongFormat ? 8 : gitCommitIdLength);
                baseVersion = baseVersion.addQualifier(commitIdQualifier);
            }

            if (!GitUtils.isDetachedHead(getRepository())) {
                String branch = getRepository().getBranch();
                baseVersion = enhanceVersionWithBranch(baseVersion, branch);
            } else {
                // ugly syntax to bypass the final/effectively final pb to access vraiable in lambda
                Optional<String> externalyProvidedBranchName = GitUtils.providedBranchName();
                if (externalyProvidedBranchName.isPresent()) {
                    baseVersion = enhanceVersionWithBranch(baseVersion, externalyProvidedBranchName.get());
                }
            }

            if (useDirty && isGitDirty()) {
                baseVersion = baseVersion.addQualifier("dirty");
            }

            return useSnapshot ? baseVersion.removeQualifier("SNAPSHOT").addQualifier("SNAPSHOT") : baseVersion;
        } catch (Exception ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }

    private Version tagToVersion(String tagName) {
        return Version.parse(getVersionNamingConfiguration().extractVersionFrom(tagName));
    }

    private boolean isGitDirty() {
        return Lambdas.unchecked(GitUtils::isDirty).apply(getGit());
    }

    private Ref findTagToUse(Commit head, Commit base) {
        return isBaseCommitOnHead(head, base) && !isGitDirty()
                ? maxVersionTag(base.getAnnotatedTags(), base.getLightTags())
                : maxVersionTag(base.getLightTags(), base.getAnnotatedTags());
    }

    private Commit findVersionCommit(Commit head, List<Commit> parents) {
        return useMaxVersion ? findMaxVersionCommit(head, parents) : parents.get(0);
    }

    private Commit findMaxVersionCommit(Commit head, List<Commit> parents) {
        return parents.stream()
                .limit(maxVersionSearchDepth)
                .map(commit -> toVersionTarget(head, commit))
                .max(Comparator.naturalOrder())
                .map(VersionTarget::getTarget)
                .orElse(parents.get(0));
    }

    private Ref maxVersionTag(List<Ref> primaryTags, List<Ref> secondaryTags) {
        return maxVersionTag(primaryTags).orElseGet(() -> maxVersionTag(secondaryTags).orElse(null));
    }

    private Optional<Ref> maxVersionTag(List<Ref> tags) {
        return tags.stream()
                .map(this::toVersionTarget)
                .max(Comparator.naturalOrder())
                .map(VersionTarget::getTarget);
    }

    private VersionTarget<Ref> toVersionTarget(Ref tagRef) {
        String tagName = GitUtils.tagNameFromRef(tagRef);
        return new VersionTarget<>(tagToVersion(tagName), tagRef);
    }

    private VersionTarget<Commit> toVersionTarget(Commit head, Commit commit) {
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
