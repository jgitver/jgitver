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
package fr.brouillard.oss.jgitver.metadata;


/**
 * Enumeration of all the possible metadata that {@link fr.brouillard.oss.jgitver.GitVersionCalculator} can provide for a repository.
 * 
 * @author Matthieu Brouillard
 */
public enum Metadatas {
    /**
     * The calculated version.
     */
    CALCULATED_VERSION, 
    /**
     * Is the repository dirty. 
     */
    DIRTY,

    /**
     * Literally text dirty if the repository is dirty.
     */
    DIRTY_TEXT,
    /**
     * Name of the commiter of HEAD commit. 
     */
    HEAD_COMMITTER_NAME, 
    /**
     * Email of the commiter of HEAD commit. 
     */
    HEAD_COMMITER_EMAIL, 
    /**
     * Datetime of the commit.
     */
    HEAD_COMMIT_DATETIME, 
    /**
     * Corresponds to then the full git identifier of the HEAD. 
     */
    GIT_SHA1_FULL, 
    /**
     * Corresponds to a substring of the git identifier of the HEAD. 
     */
    GIT_SHA1_8, 
    /**
     * Corresponds to the current branch name if any. 
     */
    BRANCH_NAME,
    /**
     * Branch name used as a qualifier if any. 
     */
    QUALIFIED_BRANCH_NAME,
    /**
     * Branch name externally provided if any. 
     */
    PROVIDED_BRANCH_NAME,
    /**
     * Corresponds to the list of tags, associated with the current HEAD.
     */
    HEAD_TAGS, 
    /**
     * Corresponds to the list of annotated tags, associated with the current HEAD.
     */
    HEAD_ANNOTATED_TAGS, 
    /**
     * Corresponds to the list of light tags, associated with the current HEAD.
     */
    HEAD_LIGHTWEIGHT_TAGS, 
    /**
     * Corresponds to the list of tags, eligible as version, associated with the current HEAD.
     */
    HEAD_VERSION_TAGS,
    /**
     * Corresponds to the list of annotated tags, eligible as version, associated with the current HEAD.
     */
    HEAD_VERSION_ANNOTATED_TAGS,
    /**
     * Corresponds to the list of light tags, eligible as version, associated with the current HEAD.
     */
    HEAD_VERSION_LIGHTWEIGHT_TAGS,
    /**
     * Corresponds to the base tag that was used for the version calculation.
     */
    BASE_TAG,
    /**
     * Corresponds to the type of tag that was used for the version calculation.
     * Value taken from {@link TagType}.
     */
    BASE_TAG_TYPE,
    /**
     * Corresponds to the whole list of tags of the current repository.
     */
    ALL_TAGS, 
    /**
     * Corresponds to the whole list of annotated tags of the current repository. 
     */
    ALL_ANNOTATED_TAGS, 
    /**
     * Corresponds to the whole list of light tags of the current repository.
     */
    ALL_LIGHTWEIGHT_TAGS, 
    /**
     * Corresponds to the whole list of tags that can serve for version calculation.
     */
    ALL_VERSION_TAGS, 
    /**
     * Corresponds to the whole list of annotated tags of the current repository that can serve for version calculation. 
     */
    ALL_VERSION_ANNOTATED_TAGS, 
    /**
     * Corresponds to the whole list of light tags of the current repository that can serve for version calculation.
     */
    ALL_VERSION_LIGHTWEIGHT_TAGS,
    /**
     * Exposes the next calculated version by adding one to the major digit of the current retained version.
     */
    NEXT_MAJOR_VERSION,
    /**
     * Exposes the next calculated version by adding one to the minor digit of the current retained version.
     */
    NEXT_MINOR_VERSION,
    /**
     * Exposes the next calculated version by adding one to the patch digit of the current retained version.
     */
    NEXT_PATCH_VERSION,
    /**
     * Exposes the version used to base the calculation on for the retained version.
     */
    BASE_VERSION,
    /**
     * Exposes the major version of the computed version, ie the X in X.Y.Z.
     */
    CURRENT_VERSION_MAJOR,
    /**
     * Exposes the minor version of the computed version, ie the Y in X.Y.Z.
     */
    CURRENT_VERSION_MINOR,
    /**
     * Exposes the patch version of the computed version, ie the Z in X.Y.Z.
     */
    CURRENT_VERSION_PATCH,
    /**
     * Exposes the commit distance from the base tag used for the version computation.
     */
    COMMIT_DISTANCE,
    /**
     * Exposes the distance from HEAD to the root ancestor.
     *
     * not active by default, use {@link fr.brouillard.oss.jgitver.Features#DISTANCE_TO_ROOT} for activation
     */
    COMMIT_DISTANCE_TO_ROOT,
    /**
     * Exposes the commit timestamp instant in the current system timezone using
     * a simplified DateTimeFormatter.ISO_LOCAL_DATE_TIME
     */
    COMMIT_TIMESTAMP,
    /**
     * Exposes the commit timestamp instant in the UTC timezone using
     * DateTimeFormatter.ISO_OFFSET_DATE_TIME
     */
    COMMIT_ISO_TIMESTAMP,
    /**
     * True if the current HEAD is on an annotated tag, false otherwise
     */
    ANNOTATED,
    /**
     * True if the current HEAD is detached, false otherwise
     */
    DETACHED_HEAD,
    /**
     * True if the current HEAD is on the same commit as the one serving 
     * as reference for the version computation 
     */
    BASE_COMMIT_ON_HEAD
    ;
}
