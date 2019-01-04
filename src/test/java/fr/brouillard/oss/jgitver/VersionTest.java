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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class VersionTest {

    @Test
    public void parse_simple_integer_as_major_number() {
        Version v = Version.parse("1");

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(0));
        assertThat(v.getPatch(), is(0));

        assertFalse("no qualifiers expected", v.isQualified());
    }

    @Test
    public void parse_major_minor() {
        Version v = Version.parse("1.2");

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(2));
        assertThat(v.getPatch(), is(0));

        assertFalse("no qualifiers expected", v.isQualified());
    }

    @Test
    public void parse_full_version() {
        Version v = Version.parse("4.5.6");

        assertThat(v.getMajor(), is(4));
        assertThat(v.getMinor(), is(5));
        assertThat(v.getPatch(), is(6));

        assertFalse("no qualifiers expected", v.isQualified());
    }

    @Test
    public void parse_version_with_simple_qualifier() {
        String versionToParse = "1.0.0-Q1";
        Version v = Version.parse(versionToParse);

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(0));
        assertThat(v.getPatch(), is(0));

        assertTrue("version should be qualified", v.isQualified());
        assertThat(v.toString(), is(versionToParse));
    }

    @Test
    public void version_is_immutable() {
        String versionToParse = "1.0.0";
        String qualifier = "Q1";
        String anotherQualifier = "Q2";

        Version vInitial = Version.parse(versionToParse);
        Version vQ1 = vInitial.addQualifier(qualifier);
        Version vQ1removed = vQ1.removeQualifier(qualifier);
        Version vQ1noQualifier = vQ1.addQualifier(anotherQualifier).noQualifier();

        assertThat(vInitial.toString(), is("1.0.0"));
        assertThat(vQ1.toString(), is("1.0.0-Q1"));
        assertThat(vQ1removed.toString(), is("1.0.0"));
        assertThat(vQ1noQualifier.toString(), is("1.0.0"));

        assertThat(System.identityHashCode(vInitial), not(System.identityHashCode(vQ1)));
        assertThat(System.identityHashCode(vInitial), not(System.identityHashCode(vQ1removed)));
        assertThat(System.identityHashCode(vInitial), not(System.identityHashCode(vQ1noQualifier)));

        assertThat(vInitial, not(equalTo(vQ1)));
        assertThat(vInitial, equalTo(vQ1removed));
        assertThat(vInitial, equalTo(vQ1noQualifier));
    }

    @Test
    public void can_parse_semver() {
        List<String> versions = Arrays.asList(
                "1.2.3",
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-0.3.7",
                "1.0.0-x.7.z.92",
                "1.0.0-alpha+001",
                "1.0.0+20130313144700",
                "1.0.0-beta+exp.sha.5114f85",
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-alpha.beta",
                "1.0.0-beta",
                "1.0.0-beta.2",
                "1.0.0-beta.11",
                "1.0.0-rc.1",
                "1.0.0"
        );

        versions.forEach(version -> {
            Version parsed = Version.parse(version);

            assertThat(parsed, notNullValue());
            assertThat(parsed.toString(), is(version));
        });
    }

    @Test
    public void can_parse_multiple_qualifiers() {
        List<String> versions = Arrays.asList(
                "1.0.0-alpha",
                "1.0.0-alpha-beta",
                "1.0.0-alpha-beta_01",
                "1.0.0-alpha-beta_01-gamma"
        );

        versions.forEach(version -> {
            Version parsed = Version.parse(version);

            assertThat(parsed, notNullValue());
            assertThat(parsed.toString(), is(version));
        });
    }

    @Test
    public void can_compare_versions() {
        List<String> versions = Arrays.asList(
                "0.0.0",
                "0.0.1",
                "0.1.0",
                "0.1.1",
                "1.0.0-alpha",
                "1.0.0-alpha.1",
                "1.0.0-alpha.beta",
                "1.0.0-beta",
                "1.0.0-beta.2",
                "1.0.0-rc.1",
                "1.0.0"
        );

        Comparator<Version> versionComparator = Comparator.naturalOrder();

        for (int i = 0; i < versions.size(); i++) {
            Version baseVersion = Version.parse(versions.get(i));
            for (int j = i + 1; j < versions.size(); j++) {
                Version cmpVersion = Version.parse(versions.get(j));

                boolean isLower = versionComparator.compare(baseVersion, cmpVersion) < 0;
                assertTrue(String.format("%s should be lower than %s", baseVersion, cmpVersion), isLower);
            }
        }
    }
}