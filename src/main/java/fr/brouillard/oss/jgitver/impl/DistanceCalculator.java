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
import java.util.Iterator;
import java.util.Optional;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.DepthWalk;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Allow to compute git depth, in term of commit distance between several commits.
 */
public interface DistanceCalculator {
    /**
     * Creates a reusable {@link DistanceCalculator} on the given repository for the given start commit.
     * @param start the commit where the computation will start
     * @param repository the repository on which to operate
     * @param maxDepth the maximum depth to which we accept to look at. If <= 0 then Integer.MAX_VALUE will be used.
     * @return a reusable {@link DistanceCalculator} object
     */
    static DistanceCalculator create(ObjectId start, Repository repository, int maxDepth) {
        return new DepthWalkDistanceCalculator(start, repository, maxDepth > 0 ? maxDepth : Integer.MAX_VALUE);
    }

    /**
     * Creates a reusable {@link DistanceCalculator} on the given repository for the given start commit,
     * uses Integer.MAX_VALUE as the maximum depth distance.
     * @see #create(ObjectId, Repository, int)
     */
    static DistanceCalculator create(ObjectId start, Repository repository) {
        return create(start, repository, Integer.MAX_VALUE);
    }

    /**
     * Computes an eventual distance between the start commit given at build time and the provided target commit.
     * Returns the computed distance inside an Optional which can be empty if the given target is not reachable
     * or is too far regarding the given distance.
     * @param target the commit to compute distance for
     * @return a distance as an Optional
     */
    Optional<Integer> distanceTo(ObjectId target);

    class DepthWalkDistanceCalculator implements DistanceCalculator {
        private final ObjectId startId;
        private final Repository repository;
        private final int maxDepth;

        DepthWalkDistanceCalculator(ObjectId start, Repository repository, int maxDepth) {
            this.startId = start;
            this.repository = repository;
            this.maxDepth = maxDepth;
        }

        public Optional<Integer> distanceTo(ObjectId target) {
            DepthWalk.RevWalk walk = null;
            try {
                walk = new DepthWalk.RevWalk(repository, maxDepth);
                RevCommit startCommit = walk.parseCommit(startId);
                walk.markRoot(startCommit);
                walk.setRetainBody(false);

                Iterator<? extends RevCommit> commitIterator = walk.iterator();

                while (commitIterator.hasNext()) {
                    RevCommit commit = commitIterator.next();
                    if (commit.getId().getName().equals(target.getName())) {
                        // we found it
                        if (commit instanceof DepthWalk.Commit) {
                            DepthWalk.Commit dwc = (DepthWalk.Commit) commit;
                            return Optional.of(Integer.valueOf(dwc.getDepth()));
                        } else {
                            throw new IllegalStateException(String.format(
                                    "implementation of %s or jgit internal has been incorrectly changed",
                                    DepthWalkDistanceCalculator.class.getSimpleName()
                            ));
                        }
                    }
                }
            } catch (IOException ignore) {
                ignore.printStackTrace();
            } finally {
                if (walk != null) {
                    walk.dispose();
                    walk.close();
                }
            }
            return Optional.empty();
        }
    }
}
