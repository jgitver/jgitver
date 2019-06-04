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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

/**
 * Container object for a git node involved in version calculation.
 * @author Matthieu Brouillard
 */
public class Commit {
    private final ObjectId gitObject;
    private final List<Ref> annotatedTags;
    private final List<Ref> lightTags;
    private final int headDistance;

    /**
     * Creates commit object using the given informations.
     * @param gitObject the git object corresponding the the git node
     * @param headDistance positive number of commit between head and this node (0 if node is HEAD)
     * @param annotatedTags non null list of annotated tags found on the git object
     * @param lightTags non null list of lightweight tags found on the git object
     */
    public Commit(ObjectId gitObject, int headDistance, List<Ref> annotatedTags, List<Ref> lightTags) {
        super();
        this.gitObject = Objects.requireNonNull(gitObject);
        this.annotatedTags = new ArrayList<>(Objects.requireNonNull(annotatedTags));
        this.lightTags = new ArrayList<>(Objects.requireNonNull(lightTags));
        this.headDistance = headDistance;
    }

    public ObjectId getGitObject() {
        return gitObject;
    }

    public List<Ref> getAnnotatedTags() {
        return annotatedTags;
    }

    public List<Ref> getLightTags() {
        return lightTags;
    }

    public int getHeadDistance() {
        return headDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Commit commit = (Commit) o;
        return Objects.equals(gitObject, commit.gitObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gitObject);
    }
}
