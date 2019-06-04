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
package fr.brouillard.oss.jgitver;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class Misc {
    public static boolean isDebugMode() {
        return Boolean.getBoolean("jgitver.debug");
    }

    /**
     * Deletes the given directory recursively.
     * 
     * @param path
     *            the path to delete, must not be null
     * @return true if deletion was totally successful, false otherwise
     */
    public static boolean deleteDirectorySimple(File path) {
        if (!path.exists()) {
            return false;
        }

        if (isDebugMode()) {
            return true;
        }

        boolean ret = true;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                ret = ret && deleteDirectorySimple(f);
            }
        }
        return ret && path.delete();
    }
}
