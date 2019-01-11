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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.petitparser.parser.Parser;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

/**
 * Extension of the {@link VersionGrammarDefinition} defining the actions to realize on rules detection.
 */
public class VersionPatternGrammarDefinition extends VersionGrammarDefinition {
    private AutoSeparatorProvider separatorProvider;

    /**
     * Constructor is given the necessary elements to transform the recognized elements.
     * @param version the base version to use (normally coming from a tag)
     * @param env provider of environment variables values
     * @param sys provider of system properties values
     * @param meta provider of computed {@link Metadatas}
     */
    public VersionPatternGrammarDefinition(
            Version version,
            Function<String, Optional<String>> env,
            Function<String, Optional<String>> sys,
            Function<Metadatas, Optional<String>> meta
    ) {
        super();
        resetSeparatorProvider();

        action("pattern", (List<?> elements) -> {
            String computedVersion = elements.stream().map(Object::toString).collect(Collectors.joining());
            return Version.parse(computedVersion);
        });
        action("pattern_element", (o) -> o);
        action("delimitedPlaceholder", (o) -> {
            return o;
        });
        action("placeholder", (o) -> o);
        action("withPrefixPlaceholder", (List<?> elements) -> {
            @SuppressWarnings("unchecked")
            Prefix p = (Prefix) elements.get(0);
            @SuppressWarnings("unchecked")
            Optional<String> value = (Optional<String>) elements.get(1);

            Optional<String> result = p.apply(value);
            result.ifPresent((ignore) -> getSeparatorProvider().next());
            return result;
        });
        action("chars", (o) -> o);
        action("prefix_placeholder", (o) -> o);
        action("fixed_prefix_placeholder", (List<?> elements) -> {
            getSeparatorProvider().endVersion();
            String prefix = (String) elements.get(0);
            Mode mode = (Mode) elements.get(1);
            return new Prefix(mode, prefix);
        });
        action("mandatory_prefix_placeholder", (o) -> o);
        action("optional_prefix_placeholder",      (o) -> o);
        action("auto_prefix_placeholder", (ignore) -> {
            return new Prefix(Mode.OPTIONAL, () -> this.getSeparatorProvider().currentSeparator());
        });
        action("meta", (String s) -> {
            try {
                Metadatas m = Metadatas.valueOf(s);
                return meta.apply(m);
            } catch (IllegalArgumentException iae) {
                return Optional.empty();
            }
        });
        action("sys", (String s) -> sys.apply(s));
        action("env", (String s) -> env.apply(s));
        action("full_version", (o) -> {
            getSeparatorProvider().major();
            getSeparatorProvider().minor();
            getSeparatorProvider().patch();
            getSeparatorProvider().next();
            return Optional.of(String.format("%s.%s.%s", version.getMajor(), version.getMinor(), version.getPatch()));
        });
        action("major_version", (o) -> {
            getSeparatorProvider().major();
            return Optional.of("" + version.getMajor());
        });
        action("minor_version", (o) -> {
            getSeparatorProvider().minor();
            return Optional.of("" + version.getMinor());
        });
        action("patch_version", (o) -> {
            getSeparatorProvider().patch();
            return Optional.of("" + version.getPatch());
        });
        action("inner_placeholder", (o) -> o);
        action("placeholder", (Optional<String> o) -> o.orElse(""));
    }

    @Override
    public Parser build() {
        resetSeparatorProvider();
        return super.build();
    }

    /**
     * Allow to reset the automatic computation of the separator to use during detection.
     * Must be called before each pattern matching and version recognition.
     */
    public void resetSeparatorProvider() {
        separatorProvider = new AutoSeparatorProvider();
    }

    private AutoSeparatorProvider getSeparatorProvider() {
        return separatorProvider;
    }
}
