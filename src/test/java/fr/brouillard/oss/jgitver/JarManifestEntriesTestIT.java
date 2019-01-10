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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;

/**
 * This integration test class aim is to check generated MANIFEST.MF entries
 */
public class JarManifestEntriesTestIT {
    public static final String X_GIT_COMMIT_ID_MANIFEST_ENTRY = "X-Git-CommitId";
    public static final String AUTOMATIC_MODULE_NAME_MANIFEST_ENTRY = "Automatic-Module-Name";
    public static final String X_MAVEN_COORDINATES = "X-Maven-Coordinates";

    @Test
    public void check_git_commit_id_exists() {
        Manifest jgitverManifest = jgitverManifestOrEmpty();
        Attributes.Name attributeName = new Attributes.Name(X_GIT_COMMIT_ID_MANIFEST_ENTRY);

        String gitCommitId = jgitverManifest.getMainAttributes().getValue(attributeName);
        assertThat(gitCommitId, notNullValue());
        assertThat(gitCommitId.length(), is(40));
    }

    @Test
    public void check_java_module_name_is_provided() {
        Manifest jgitverManifest = jgitverManifestOrEmpty();
        Attributes.Name attributeName = new Attributes.Name(AUTOMATIC_MODULE_NAME_MANIFEST_ENTRY);
        String moduleName = jgitverManifest.getMainAttributes().getValue(attributeName);
        assertThat(moduleName, notNullValue());
        assertThat(moduleName, is("fr.brouillard.oss.jgitver.library"));
    }

    private Manifest jgitverManifestOrEmpty() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Manifest manifest = new Manifest(url.openStream());
                Attributes mfAttrs = manifest.getMainAttributes();
                Attributes.Name attributeName = new Attributes.Name(X_MAVEN_COORDINATES);
                if (mfAttrs.containsKey(attributeName) && mfAttrs.getValue(attributeName).startsWith("fr.brouillard.oss:jgitver")) {
                    return manifest;
                }
            }
        } catch (IOException ignore) {
            // expected
        }
        return new Manifest();
    }
}
