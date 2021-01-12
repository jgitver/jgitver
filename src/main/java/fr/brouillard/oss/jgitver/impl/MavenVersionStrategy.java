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
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public class MavenVersionStrategy extends VersionStrategy<MavenVersionStrategy> {
    private boolean useDirty = false;
    private boolean forceComputation = false;

    public MavenVersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git, MetadataRegistrar metadatas) {
        super(vnc, repository, git, metadatas);
    }

    @Override
    public Version build(Commit head, List<Commit> parents) throws VersionCalculationException {
        try {
            Commit base = findVersionCommit(head, parents);
            Ref tagToUse = findTagToUse(head, base);
            Version baseVersion = getBaseVersionAndRegisterMetadata(base, tagToUse);
            
            boolean needSnapshot = true;
            boolean isDirty = GitUtils.isDirty(getGit());
            boolean isDetachedHead = GitUtils.isDetachedHead(getRepository());
            boolean isBaseCommitOnHead = isBaseCommitOnHead(head, base);

            boolean shouldComputeVersion = !isDetachedHead || isDirty || !isBaseCommitOnHead || forceComputation;

            getRegistrar().registerMetadata(Metadatas.BASE_COMMIT_ON_HEAD, "" + isBaseCommitOnHead);

            if (tagToUse != null) {
                needSnapshot = baseVersion.isSnapshot() || !isBaseCommitOnHead
                        || !GitUtils.isAnnotated(tagToUse) || (isDirty && !isDetachedHead) || forceComputation;
            }

            if (shouldComputeVersion) {
                if (!isBaseCommitOnHead || (isDirty && !isDetachedHead) || forceComputation) {
                    // we are not on head
                    if (GitUtils.isAnnotated(tagToUse) && !baseVersion.removeQualifier("SNAPSHOT").isQualified()) {
                        // found tag to use was a non qualified annotated one, lets' increment the version automatically
                        baseVersion = baseVersion.incrementPatch();
                    }
                    baseVersion = baseVersion.noQualifier();
                }
            }
            
            if (!isDetachedHead) {
                String branch = getRepository().getBranch();
                baseVersion = enhanceVersionWithBranch(baseVersion, branch);
            } else {
                // ugly syntax to bypass the final/effectively final pb to access vraiable in lambda
                Optional<String> externalyProvidedBranchName = GitUtils.providedBranchName();
                if (externalyProvidedBranchName.isPresent()) {
                    baseVersion = enhanceVersionWithBranch(baseVersion, externalyProvidedBranchName.get());
                    getRegistrar().registerMetadata(Metadatas.PROVIDED_BRANCH_NAME, externalyProvidedBranchName.get());
                }
            }

            if (useDirty && isDirty) {
                baseVersion = baseVersion.addQualifier("dirty");
            }
            
            try (RevWalk walk = new RevWalk(getRepository())) {
                RevCommit rc = walk.parseCommit(head.getGitObject());
                String commitTimestamp = GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_TIMESTAMP, commitTimestamp);
                String isoCommitTimestamp = GitUtils.getIsoTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_ISO_TIMESTAMP, isoCommitTimestamp);
            }
            
            return needSnapshot ? baseVersion.removeQualifier("SNAPSHOT").addQualifier("SNAPSHOT") : baseVersion;
        } catch (Exception ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }

    public MavenVersionStrategy setUseDirty(boolean useDirty) {
        return runAndGetSelf(() -> this.useDirty = useDirty);
    }
    public MavenVersionStrategy setForceComputation(boolean forceComputation) {
        return runAndGetSelf(() -> this.forceComputation = forceComputation);
    }
}
