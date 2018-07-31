package fr.brouillard.oss.jgitver.impl.pattern;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.CharacterParser;
import org.petitparser.parser.primitive.StringParser;
import org.petitparser.tools.GrammarDefinition;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.petitparser.parser.primitive.CharacterParser.*;
import static org.petitparser.parser.primitive.CharacterParser.of;
import static org.petitparser.parser.primitive.StringParser.of;

public class VersionGrammarDefinition extends GrammarDefinition {
    public VersionGrammarDefinition() {
        def("start", ref("pattern").end());
        def("pattern", ref("pattern_element").plus());
        def("pattern_element", ref("chars").or(ref("delimitedPlaceholder")));
        def("delimitedPlaceholder", of("${").seq(ref("placeholder"), of('}')).pick(1));
        def("placeholder", ref("inner_placeholder").or(ref("withPrefixPlaceholder")));
        def("withPrefixPlaceholder", ref("prefix_placeholder").seq(ref("inner_placeholder")));
        def("chars", letter().or(digit(), anyOf("_-.+")).plus().flatten());

        def("prefix_placeholder", ref("fixed_prefix_placeholder").or(ref("auto_prefix_placeholder")));
        def("fixed_prefix_placeholder", ref("chars").seq(ref("mandatory_prefix_placeholder").or(ref("optional_prefix_placeholder"))));
        def("mandatory_prefix_placeholder",     of(':').map((s) -> Mode.MANDATORY));
        def("optional_prefix_placeholder",      of('~').map((s) -> Mode.OPTIONAL));
        def("auto_prefix_placeholder",          of('<'));

        def("meta", of("meta.").seq(ref("chars")).pick(1));
        def("sys", of("sys.").seq(ref("chars")).pick(1));
        def("env", of("env.").seq(ref("chars")).pick(1));

        def("major_version", of('M'));
        def("minor_version", of('m'));
        def("patch_version", of('p'));

        def("inner_placeholder", ref("meta").or(ref("sys"), ref("env"), ref("major_version"), ref("minor_version"), ref("patch_version")));
    }

}
