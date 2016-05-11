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
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.VersionCalculationException;

public class ConfigurableVersionStrategy extends VersionStrategy {
    private boolean autoIncrementPatch = false;
    private boolean useDistance = true;
    private boolean useGitCommitId = false;
    private int gitCommitIdLength = 8;
    
    public ConfigurableVersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git) {
        super(vnc, repository, git);
    }
    
    public ConfigurableVersionStrategy setAutoIncrementPatch(boolean autoIncrementPatch) {
        this.autoIncrementPatch = autoIncrementPatch;
        return this;
    }

    public ConfigurableVersionStrategy setUseDistance(boolean useDistance) {
        this.useDistance = useDistance;
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

    @Override
    public Version build(Commit head, List<Commit> parents) throws VersionCalculationException {
        try {
            Commit base = parents.get(0);
            Ref tagToUse;
            
            if (isBaseCommitOnHead(head, base) && GitUtils.isDetachedHead(getRepository())) {
                // consider first the annotated tags
                tagToUse = base.getAnnotatedTags().stream().findFirst()
                        .orElseGet(() -> base.getLightTags().stream().findFirst().orElse(null));
            } else {
                // consider first the light tags
                tagToUse = base.getLightTags().stream().findFirst()
                        .orElseGet(() -> base.getAnnotatedTags().stream().findFirst().orElse(null));
            }
            
            Version baseVersion;
            
            if (tagToUse == null) {
                // we have reach the initial commit of the repository
                baseVersion = Version.DEFAULT_VERSION;
            } else {
                baseVersion = Version
                        .parse(getVersionNamingConfiguration().extractVersionFrom(GitUtils.tagNameFromRef(tagToUse)));
            }
            
            final boolean useSnapshot = baseVersion.isSnapshot();
            
            if (!isBaseCommitOnHead(head, base) && autoIncrementPatch) {
                // we are not on head
                if (GitUtils.isAnnotated(tagToUse)) {
                    // found tag to use was an annotated one, lets' increment the version automatically
                    baseVersion = baseVersion.increasePatch();
                }
            }
            
            if (useDistance && !useSnapshot) {
                if (tagToUse == null) {
                    // no tag was found, let's count from initial commit
                    baseVersion = baseVersion.addQualifier("" + base.getHeadDistance());
                } else {
                    // use distance when not on head
                    // or if on head with a light tag
                    if (!isBaseCommitOnHead(head, base) || !GitUtils.isAnnotated(tagToUse)) {
                        baseVersion = baseVersion.addQualifier("" + base.getHeadDistance());
                    }
                }
            }
            
            if (useGitCommitId && !isBaseCommitOnHead(head, base)) {
                baseVersion = baseVersion.addQualifier(head.getGitObject().getName().substring(0, gitCommitIdLength));
            }
            
            if (!GitUtils.isDetachedHead(getRepository())) {
                // let's add a branch qualifier if one is computed
                Optional<String> branchQualifier = getVersionNamingConfiguration().branchQualifier(getRepository().getBranch());
                if (branchQualifier.isPresent()) {
                    baseVersion = baseVersion.addQualifier(branchQualifier.get());
                }
            }
            
            return useSnapshot ? baseVersion.removeQualifier("SNAPSHOT").addQualifier("SNAPSHOT") : baseVersion;
        } catch (IOException ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }
}
