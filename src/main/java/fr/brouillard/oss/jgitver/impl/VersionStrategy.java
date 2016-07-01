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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.VersionCalculationException;

public abstract class VersionStrategy {
    private VersionNamingConfiguration vnc;
    private Repository repository;
    private Git git;

    /**
     * Default constructor.
     * @param vnc the configuration to use
     * @param repository the git repository
     * @param git a git helper object built from the repository
     */
    public VersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git) {
        this.vnc = vnc;
        this.repository = repository;
        this.git = git;
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
        return Integer.MAX_VALUE;
    }
    
    public boolean considerTagAsAVersionOne(Ref tag) {
        String tagName = tagNameFromRef(tag);
        return getVersionNamingConfiguration().getSearchPattern().matcher(tagName).matches();
    }

    public StrategySearchMode searchMode() {
        return StrategySearchMode.STOP_AT_FIRST;
    }

    private String tagNameFromRef(Ref tag) {
        return tag.getName().replace("refs/tags/", "");
    }
    
    public static enum StrategySearchMode {
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
}
