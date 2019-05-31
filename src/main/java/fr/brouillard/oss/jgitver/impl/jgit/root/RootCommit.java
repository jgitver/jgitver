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
package fr.brouillard.oss.jgitver.impl.jgit.root;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.IOException;

public class RootCommit extends RevCommit {
    /**
     * Create a new commit reference.
     *
     * @param id object name for the commit.
     */
    protected RootCommit(AnyObjectId id) {
        super(id);
    }

    public static class RootWalk extends RevWalk {
        public RootWalk(Repository repo) {
            super(repo);
            setRevFilter(rootFilter());
        }

        @Override
        protected RevCommit createCommit(AnyObjectId id) {
            return new RootCommit(id);
        }

        static RevFilter rootFilter() {
            return new RevFilter() {
                @Override
                public boolean include(RevWalk walker, RevCommit commit) throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
                    return commit.getParents().length == 0;
                }

                @Override
                public RevFilter clone() {
                    return rootFilter();
                }
            };
        }
    }
}
