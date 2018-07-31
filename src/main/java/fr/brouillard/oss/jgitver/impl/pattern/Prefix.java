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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Prefix implements Function<Optional<String>, Optional<String>> {
    private final Mode mode;
    private final Supplier<String> prefixProducer;

    Prefix(Mode mode, String prefix) {
        this(mode, () -> prefix);
    }

    Prefix(Mode mode, Supplier<String> prefixProducer) {
        this.mode = mode;
        this.prefixProducer = prefixProducer;
    }

    @Override
    public Optional<String> apply(Optional<String> s) {
        if (Mode.OPTIONAL.equals(this.mode) && !s.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(prefixProducer.get() + s.get());
    }
}
