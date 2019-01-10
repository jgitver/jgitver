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
import java.util.Date;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

/**
 * Utility class able to retrieve date of tags.
 */
public class TagDateExtractor implements AutoCloseable {
    private final RevWalk revWalk;

    TagDateExtractor(Repository repository) {
        this.revWalk = new RevWalk(repository);
    }

    /**
     * Retrieve the date of the given tag.
     * @param tagObjectId a non null {@link ObjectId} of a tag
     * @return the date of the date
     */
    public Date dateOfTagObjectId(ObjectId tagObjectId) throws IOException {
        return revWalk.parseTag(tagObjectId).getTaggerIdent().getWhen();
    }

    /**
     * Retrieve the date of the given tag.
     * @param r a non null {@link Ref} of a tag
     * @return the date of the date
     */
    public Date dateOfRef(Ref r) {
        try {
            return dateOfTagObjectId(r.getObjectId());
        } catch (IOException ex) {
            throw new IllegalStateException("can't parse date of tag " + r.getName(), ex);
        }
    }

    @Override
    public void close() {
        this.revWalk.close();
    }
}
