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

/**
 * Simple data holder of 2 data types.
 * @param <U> first datatype
 * @param <V> second datatype
 */
public class Pair<U,V> {
    private final U left;
    private final V right;

    /**
     * Creates an immutable pair of two objects of type U & V.
     * @param left first part
     * @param right second part
     */
    public Pair(U left, V right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Retrieve the left part of the pair object.
     * @return the value registered at construction time as the left part
     */
    public U getLeft() {
        return left;
    }

    /**
     * Retrieve the rightpart of the pair object.
     * @return the value registered at construction time as the right part
     */
    public V getRight() {
        return right;
    }

    /**
     * Method factory of a pair.
     * @param left the left value
     * @param right the right value
     * @param <U> the type of the left value
     * @param <V> the type of the right value
     * @return a non null Pair object
     */
    public static <U,V> Pair<U, V> of(U left, V right) {
        return new Pair<>(left, right);
    }
}
