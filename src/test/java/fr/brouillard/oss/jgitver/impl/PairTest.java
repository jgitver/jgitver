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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    @Test
    public void method_factory_of() {
        Integer left = 10;
        String right = "some value";

        Pair<Integer, String> p = Pair.of(left, right);


        assertNotNull(p, "Pair#of() should not return a null value");

        assertEquals(left, p.getLeft());
        assertEquals(right, p.getRight());
    }

    @Test
    public void pair_of_null_values_return_null_values() {
        Pair<String, String> pairOfString = Pair.of(null, null);

        assertNull(pairOfString.getLeft(), "given left value should remain null");
        assertNull(pairOfString.getRight(), "given right value should remain null");
    }
}