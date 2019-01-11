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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

class CommitTest {
    @Test
    public void cannot_instanciate_without_objectId() {
        assertThrows(
                NullPointerException.class,
                () -> new Commit(null, 0, Collections.emptyList(), Collections.emptyList())
        );
    }

    @Test
    public void cannot_instanciate_with_null_tags_lists() {
        ObjectId anObjectId = new ObjectId(0, 0, 0, 0, 0);
        assertThrows(
                NullPointerException.class,
                () -> new Commit(anObjectId, 0, null, Collections.emptyList())
        );
        assertThrows(
                NullPointerException.class,
                () -> new Commit(anObjectId, 0, Collections.emptyList(), null)
        );
    }

    @Test
    public void check_equality_for_same_objectId() {
        ObjectId o1 = new ObjectId(1, 0, 0, 0, 0);
        ObjectId o2 = new ObjectId(2, 0, 0, 0, 0);

        Commit co1AtDistance1 = new Commit(o1, 1, Collections.emptyList(), Collections.emptyList());
        Commit co1AtDistance2 = new Commit(o1, 2, Collections.emptyList(), Collections.emptyList());
        Commit co2AtDistance1 = new Commit(o2, 2, Collections.emptyList(), Collections.emptyList());

        assertEquals(co1AtDistance1, co1AtDistance1, "same instance should be equal to itself");
        assertEquals(co1AtDistance1, co1AtDistance2, "two instances for same objectId at different depth should be equal");
        assertNotEquals(co1AtDistance1, co2AtDistance1, "two instances for different objectId must not be equal");
    }
}