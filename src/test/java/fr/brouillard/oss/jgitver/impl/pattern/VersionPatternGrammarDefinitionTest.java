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

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionPatternGrammarDefinitionTest {
    @Test
    public void recognizeSimpleMajorVersion() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(0));
        assertThat(computedVersion.getPatch(), is(0));
        assertFalse(computedVersion.isQualified());
    }

    @Test
    public void recognizeSimpleMinorVersion() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(0));
        assertFalse(computedVersion.isQualified());
    }

    @Test
    public void recognizeFullVersionUsingDetailledVersionsPatterns() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertFalse(computedVersion.isQualified());
    }

    @Test
    public void recognizeFullVersion() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${v}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertFalse(computedVersion.isQualified());
    }

    @Test
    public void recognizeFullWithOneEnvQualifier() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${<env.E1}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());

        assertThat(computedVersion.toString(), is("1.2.3-E_E1"));
    }

    @Test
    public void recognizeFullWithTwoEnvQualifiers() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${<env.E1}${<env.E2}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());

        assertThat(computedVersion.toString(), is("1.2.3-E_E1.E_E2"));
    }

    @Test
    public void recognizeFullWithAllKindQualifiers() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${<sys.S1}${<env.E2}${<meta.BRANCH_NAME}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());

        assertThat(computedVersion.toString(), is("1.2.3-S_S1.E_E2.M_BRANCH_NAME"));
    }

    @Test
    public void useFixedPrefixWithValue() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${+:sys.S1}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());

        assertThat(computedVersion.toString(), is("1.2.3+S_S1"));
    }

    @Test
    public void useFixedPrefixAndIdentifiers() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> Optional.of("E_" + s);

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${+:sys.S1}${<sys.S2}${<sys.S3}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());

        assertThat(computedVersion.toString(), is("1.2.3+S_S1.S_S2.S_S3"));
    }

    @Test
    public void useAutoPrefixWithEmptyValue() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> {
            if ("EMPTY".equals(s)) {
                return Optional.empty();
            }
            return Optional.of("E_" + s);
        };

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${<env.EMPTY}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertFalse(computedVersion.isQualified());
        assertThat(computedVersion.toString(), is("1.2.3"));
    }

    @Test
    public void useAutoPrefixWithEmptyValueAndNonEmptyValue() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> {
            if ("EMPTY".equals(s)) {
                return Optional.empty();
            }
            return Optional.of("E_" + s);
        };

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${<env.EMPTY}${<env.E1}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());
        assertThat(computedVersion.toString(), is("1.2.3-E_E1"));
    }

    @Test
    public void useAutoPrefixWithEmptyValueAndTwoNonEmptyValues() {
        Function<Metadatas, Optional<String>> metaProvider = (m) -> Optional.of("M_" + m.name());
        Function<String, Optional<String>> systemProvider = (s) -> Optional.of("S_" + s);
        Function<String, Optional<String>> envProvider = (s) -> {
            if ("EMPTY".equals(s)) {
                return Optional.empty();
            }
            return Optional.of("E_" + s);
        };

        VersionGrammarDefinition g = new VersionPatternGrammarDefinition(Version.parse("1.2.3"), envProvider, systemProvider, metaProvider);

        Parser p = g.build();

        assertThat(p, notNullValue());
        Result result = p.parse("${M}${<m}${<p}${<env.EMPTY}${<env.E1}${<env.E2}");
        assertThat(result, notNullValue());
        assertTrue(result.isSuccess());

        Version computedVersion = result.get();
        assertThat(computedVersion, notNullValue());
        assertThat(computedVersion.getMajor(), is(1));
        assertThat(computedVersion.getMinor(), is(2));
        assertThat(computedVersion.getPatch(), is(3));
        assertTrue(computedVersion.isQualified());
        assertThat(computedVersion.toString(), is("1.2.3-E_E1.E_E2"));
    }
}