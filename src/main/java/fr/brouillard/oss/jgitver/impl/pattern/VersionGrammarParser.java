package fr.brouillard.oss.jgitver.impl.pattern;

import org.petitparser.context.Context;
import org.petitparser.context.Result;
import org.petitparser.tools.GrammarParser;

public class VersionGrammarParser extends GrammarParser {
    private VersionPatternGrammarDefinition definition;

    public VersionGrammarParser(VersionPatternGrammarDefinition definition) {
        super(definition);
        this.definition = definition;

    }

    @Override
    public Result parseOn(Context context) {
        this.definition.resetSeparatorProvider();
        return super.parseOn(context);
    }
}
