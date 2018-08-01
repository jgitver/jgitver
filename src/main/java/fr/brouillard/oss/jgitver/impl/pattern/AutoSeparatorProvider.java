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
package fr.brouillard.oss.jgitver.impl.pattern;

/**
 * Computes the separator to use for a semver compatible version pattern.
 */
public class AutoSeparatorProvider {
    private String current;
    private boolean versionEnded = false;
    private boolean firstAfterVersion = true;

    public AutoSeparatorProvider() {
        this.current = ".";
    }

    /**
     * to be called when major version is processed.
     */
    public void major() {
    }

    /**
     * to be called when minor version is processed.
     */
    public void minor() {
    }

    /**
     * to be called when patch version is processed.
     */
    public void patch() {
        endVersion();
    }

    /**
     * to be called when version is finished to be processed.
     */
    public void endVersion() {
        if (!this.versionEnded) {
            this.versionEnded = true;
        }
    }

    /**
     * Call this each time a separator has been used.
     */
    public void next() {
        if (!versionEnded) {
            this.current = ".";
        } else {
            if (firstAfterVersion) {
                this.current = "-";
                firstAfterVersion = false;
            } else {
                this.current = ".";
            }
        }
    }

    /**
     * Retrieve the current separator that should be used.
     */
    public String currentSeparator() {
        return this.current;
    }
}
