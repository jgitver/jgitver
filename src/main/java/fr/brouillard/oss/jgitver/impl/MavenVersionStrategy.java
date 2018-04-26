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

import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.VersionCalculationException;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.metadata.TagType;

public class MavenVersionStrategy extends VersionStrategy {
    private boolean useDirty = false;

    public MavenVersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git, MetadataRegistrar metadatas) {
        super(vnc, repository, git, metadatas);
    }

    @Override
    public Version build(Commit head, List<Commit> parentsWithTags) throws VersionCalculationException {
        try {
            Commit base = parentsWithTags.get(0);

            Ref tagToUse;
            if (isBaseCommitOnHead(head, base) && !GitUtils.isDirty(getGit())) {
                // consider first the annotated tags
                tagToUse = base.getAnnotatedTags().stream().findFirst()
                        .orElseGet(() -> base.getLightTags().stream().findFirst().orElse(null));
            } else {
                // consider first the light tags
                tagToUse = base.getLightTags().stream().findFirst()
                        .orElseGet(() -> base.getAnnotatedTags().stream().findFirst().orElse(null));
            }

            Version baseVersion = null;
            boolean needSnapshot = false;

            if (tagToUse == null) {
                // we have reached the first commit of the repository and this commit is still no annotated
                // Let's use a default version.
                baseVersion = Version.DEFAULT_VERSION;
                needSnapshot = true;
            } else {
                String tagName = GitUtils.tagNameFromRef(tagToUse);
                TagType tagType = computeTagType(tagToUse, base.getAnnotatedTags().stream().findFirst().orElse(null));
                getRegistrar().registerMetadata(Metadatas.BASE_TAG, tagName);
                getRegistrar().registerMetadata(Metadatas.BASE_TAG_TYPE, tagType.name());
                baseVersion = Version
                        .parse(getVersionNamingConfiguration().extractVersionFrom(tagName));
                needSnapshot = baseVersion.isSnapshot() || !isBaseCommitOnHead(head, base)
                        || !GitUtils.isAnnotated(tagToUse);
            }
            getRegistrar().registerMetadata(Metadatas.BASE_VERSION, baseVersion.toString());

            if (!isBaseCommitOnHead(head, base)) {
                // we are not on head
                if (GitUtils.isAnnotated(tagToUse) && !baseVersion.removeQualifier("SNAPSHOT").isQualified()) {
                    // found tag to use was a non qualified annotated one, lets' increment the version automatically
                    baseVersion = baseVersion.incrementPatch();
                }
                baseVersion = baseVersion.noQualifier();
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
            
            if (useDirty && GitUtils.isDirty(getGit())) {
                baseVersion = baseVersion.addQualifier("dirty");
            }

            try (RevWalk walk = new RevWalk(getRepository())) {
                RevCommit rc = walk.parseCommit(head.getGitObject());
                String commitTimestamp = GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_TIMESTAMP, commitTimestamp);
            }

            return needSnapshot ? baseVersion.removeQualifier("SNAPSHOT").addQualifier("SNAPSHOT") : baseVersion;
        } catch (Exception ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }

    public void setUseDirty(boolean useDirty) {
        this.useDirty = useDirty;
    }
}
