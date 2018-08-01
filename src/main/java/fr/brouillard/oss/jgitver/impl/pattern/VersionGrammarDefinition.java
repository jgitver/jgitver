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

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.CharacterParser.digit;
import static org.petitparser.parser.primitive.CharacterParser.letter;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.StringParser.of;

import org.petitparser.tools.GrammarDefinition;

/**
 * Grammar definition to recognize a version pattern.
 */
public class VersionGrammarDefinition extends GrammarDefinition {
    /**
     * Default constructor that define the grammar rules.
     */
    public VersionGrammarDefinition() {
        def("start", ref("pattern").end());
        def("pattern", ref("pattern_element").plus());
        def("pattern_element", ref("chars").or(ref("delimitedPlaceholder")));
        def("delimitedPlaceholder", of("${").seq(ref("placeholder"), of('}')).pick(1));
        def("placeholder", ref("inner_placeholder").or(ref("withPrefixPlaceholder")));
        def("withPrefixPlaceholder", ref("prefix_placeholder").seq(ref("inner_placeholder")));
        def("chars", letter().or(digit(), anyOf("_-.+")).plus().flatten());

        def("prefix_placeholder", ref("fixed_prefix_placeholder").or(ref("auto_prefix_placeholder")));
        def("fixed_prefix_placeholder", ref("chars").seq(
                ref("mandatory_prefix_placeholder").or(ref("optional_prefix_placeholder"))
        ));
        def("mandatory_prefix_placeholder",     of(':').map((s) -> Mode.MANDATORY));
        def("optional_prefix_placeholder",      of('~').map((s) -> Mode.OPTIONAL));
        def("auto_prefix_placeholder",          of('<'));

        def("meta", of("meta.").seq(ref("chars")).pick(1));
        def("sys", of("sys.").seq(ref("chars")).pick(1));
        def("env", of("env.").seq(ref("chars")).pick(1));

        def("full_version", of('v'));
        def("major_version", of('M'));
        def("minor_version", of('m'));
        def("patch_version", of('p'));

        def("inner_placeholder", ref("meta").or(
                ref("sys"),
                ref("env"),
                ref("full_version"),
                ref("major_version"),
                ref("minor_version"),
                ref("patch_version")
        ));
    }
}
