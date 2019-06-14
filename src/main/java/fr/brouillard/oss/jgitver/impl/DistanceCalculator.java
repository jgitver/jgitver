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
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.DepthWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

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
    static DistanceCalculator create(AnyObjectId start, Repository repository, int maxDepth) {
        CalculatorKind calculatorBuilder = GitUtils.calculatorBuilder();
        return calculatorBuilder.calculator(start, repository, maxDepth > 0 ? maxDepth : Integer.MAX_VALUE);
    }

    /**
     * Creates a reusable {@link DistanceCalculator} on the given repository for the given start commit,
     * uses Integer.MAX_VALUE as the maximum depth distance.
     * @see #create(AnyObjectId, Repository, int)
     */
    static DistanceCalculator create(AnyObjectId start, Repository repository) {
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

    /**
     * DistanceCalculator that mimics 'git log --typo-sort --oneline | wc -l' minus 1
     */
    public class LogWalkDistanceCalculator implements DistanceCalculator {
        private final AnyObjectId startId;
        private final Repository repository;
        private final int maxDepth;

        public LogWalkDistanceCalculator(AnyObjectId start, Repository repository, int maxDepth) {
            this.startId = start;
            this.repository = repository;
            this.maxDepth = maxDepth;
        }

        @Override
        public Optional<Integer> distanceTo(ObjectId target) {
            Objects.requireNonNull(target);
            try (RevWalk walk = new RevWalk(this.repository)) {
                RevCommit startCommit = walk.parseCommit(startId);
                walk.setRetainBody(false);
                walk.markStart(startCommit);
                walk.sort(RevSort.TOPO);

                System.out.printf("from %s :: %s\n", startId.name(), target.name());

                Iterator<? extends RevCommit> commitIterator = walk.iterator();
                int distance = 0;
                while (commitIterator.hasNext()) {
                    RevCommit commit = commitIterator.next();

                    System.out.printf("%d - %s\n", distance, commit.getId().name());
                    if (commit.getId().getName().equals(target.getName())) {
                        // we found it
                        return Optional.of(distance);
                    }

                    distance++;

                    if (distance > maxDepth) {
                        return Optional.empty();
                    }
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }

            return Optional.empty();
        }
    }

    public class DepthWalkDistanceCalculator implements DistanceCalculator {
        private final AnyObjectId startId;
        private final Repository repository;
        private final int maxDepth;

        public DepthWalkDistanceCalculator(AnyObjectId start, Repository repository, int maxDepth) {
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

    /**
     * Calculates the distance by trying to find the target commit first on the main branch and then following any other branches.
     */
    public class FirstParentWalkDistanceCalculator implements DistanceCalculator {
        private final AnyObjectId startId;

        private final Repository repository;

        private final int maxDepth;

        public FirstParentWalkDistanceCalculator(AnyObjectId start, Repository repository, int maxDepth) {
            this.startId = start;
            this.repository = repository;
            this.maxDepth = maxDepth;
        }

        public Optional<Integer> distanceTo(ObjectId target) {
            try (RevWalk walk = new RevWalk(repository)) {
                RevCommit head = walk.parseCommit(startId);

                Deque<Pair<Integer, RevCommit>> parentsStack = new LinkedList<>();
                parentsStack.add(Pair.of(0, head));
                Set<RevCommit> processedRevs = new HashSet<>();

                int commitCount = 0;
                while (!parentsStack.isEmpty()) {
                    // based on https://stackoverflow.com/questions/33038224/how-to-call-git-show-first-parent-in-jgit
                    RevCommit[] parents = head.getParents();

                    if (head.getId().getName().equals(target.getName())) {
                        // we found it
                        return Optional.of(Integer.valueOf(commitCount));
                    }
                    // get next head
                    RevCommit firstParent = null;
                    if (parents != null && parents.length > 0) {
                        firstParent = parents[0];
                    }
                    if (firstParent != null && !processedRevs.contains(firstParent)) {
                        // follow the first parent but only if not yet processed for faster processing and to avoid loops
                        head = walk.parseCommit(firstParent);
                        processedRevs.add(firstParent);
                        // remember other parents as we may need to follow the other parents as well if
                        // the target is not on the current branch.
                        for (int i = 1; i < parents.length; i++) {
                            parentsStack.push(Pair.of(commitCount, parents[i]));
                        }
                    } else {
                        // traverse next parent
                        Pair<Integer, RevCommit> previous = parentsStack.poll();
                        commitCount = previous.getLeft();
                        RevCommit nextParent = previous.getRight();
                        head = walk.parseCommit(nextParent);
                        processedRevs.add(nextParent);
                    }

                    if (commitCount >= maxDepth) {
                        return Optional.empty();
                    }

                    commitCount++;
                }
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
            return Optional.empty();
        }
    }

    public static enum CalculatorKind {
        /**
         * Distance calculator which emulates 'git log --typo-sort --oneline | wc -l' minus 1
         */
        LOG {
            @Override
            DistanceCalculator calculator(AnyObjectId start, Repository repository, int maxDepth) {
                return new LogWalkDistanceCalculator(start, repository, maxDepth);
            }
        },
        DEPTH {
            @Override
            DistanceCalculator calculator(AnyObjectId start, Repository repository, int maxDepth) {
                return new DepthWalkDistanceCalculator(start, repository, maxDepth);
            }
        }, FIRST_PARENT {
            @Override
            DistanceCalculator calculator(AnyObjectId start, Repository repository, int maxDepth) {
                return new FirstParentWalkDistanceCalculator(start, repository, maxDepth);
            }
        };

        abstract DistanceCalculator calculator(AnyObjectId start, Repository repository, int maxDepth);
    }
}
