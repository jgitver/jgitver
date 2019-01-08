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
package fr.brouillard.oss.jgitver.impl.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.lib.Ref;

import fr.brouillard.oss.jgitver.impl.GitUtils;
import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public class MetadataHolder implements MetadataProvider, MetadataRegistrar {
    private final Map<Metadatas, String> metadataValues = new HashMap<>();
    
    @Override
    public void registerMetadata(Metadatas meta, String value) {
        metadataValues.put(meta, value);
    }
    
    public void registerMetadataTags(Metadatas meta, Stream<Ref> tags) {
        String concatenatedTags = tags.map(GitUtils::tagNameFromRef).collect(Collectors.joining(","));
        metadataValues.put(meta, concatenatedTags);
    }

    @Override
    public Optional<String> meta(Metadatas meta) {
        return Optional.ofNullable(metadataValues.get(meta));
    }
}
