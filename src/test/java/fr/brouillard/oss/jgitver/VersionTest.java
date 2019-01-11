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

import static java.util.function.Predicate.isEqual;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

public class VersionTest {

    @Test
    public void parse_simple_integer_as_major_number() {
        Version v = Version.parse("1");

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(0));
        assertThat(v.getPatch(), is(0));

        assertFalse(v.isQualified(), "no qualifiers expected");
    }

    @Test
    public void parse_major_minor() {
        Version v = Version.parse("1.2");

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(2));
        assertThat(v.getPatch(), is(0));

        assertFalse(v.isQualified(), "no qualifiers expected");
    }

    @Test
    public void parse_full_version() {
        Version v = Version.parse("4.5.6");

        assertThat(v.getMajor(), is(4));
        assertThat(v.getMinor(), is(5));
        assertThat(v.getPatch(), is(6));

        assertFalse(v.isQualified(), "no qualifiers expected");
    }

    @Test
    public void parse_version_with_simple_qualifier() {
        String versionToParse = "1.0.0-Q1";
        Version v = Version.parse(versionToParse);

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(0));
        assertThat(v.getPatch(), is(0));

        assertTrue(v.isQualified(), "version should be qualified");
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
    public void can_increment_correctly() {
        Version v = Version.parse("1");

        assertThat(v.getMajor(), is(1));
        assertThat(v.getMinor(), is(0));
        assertThat(v.getPatch(), is(0));

        assertThat("incremented patch should be 1", v.incrementPatch().getPatch(), is(1));
        assertThat("increased patch should be 1", v.increasePatch().getPatch(), is(1));

        assertThat("incremented minor should be 1", v.incrementMinor().getMinor(), is(1));

        assertThat("incremented major should be 2", v.incrementMajor().getMajor(), is(2));
    }

    @Test
    public void exception_thrown_when_parsing_non_version_string() {
        assertThrows(NullPointerException.class, () -> Version.parse(null));
        assertThrows(IllegalStateException.class, () -> Version.parse("this is not a version"));
    }

    @Test
    public void verify_snapshot_version() {
        Version v = Version.parse("1");

        assertFalse(v.isSnapshot(), "release version should not be reported as SNAPSHOT");

        Version rc = v.addQualifier("RC");
        assertFalse(rc.isSnapshot(), "rc version should not be reported as SNAPSHOT");

        Version vSnapshot = v.addQualifier("SNAPSHOT");
        Version rcSnapshot = rc.addQualifier("SNAPSHOT");

        assertTrue(vSnapshot.isSnapshot(), "release version with SNAPSHOT qualifier should be reported as SNAPSHOT");
        assertTrue(rcSnapshot.isSnapshot(), "rc version with SNAPSHOT qualifier should be reported as SNAPSHOT");
    }

    @Test
    public void check_two_equals_version_have_same_hashcode() {
        Arrays.asList("1", "1.0.0-rc").forEach(versionAsString -> {
            Version v1 = Version.parse(versionAsString);
            Version v2 = Version.parse(versionAsString);

            assertEquals(v1, v2);
            assertEquals(v1.hashCode(), v2.hashCode());
        });
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
    public void same_version_object_are_equals() {
        Version v = Version.parse("1.0.0");

        assertTrue(v.equals(v), "same object should equals itself");
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

    private static List<String> ascendingVersionsAsString = Arrays.asList(
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
            "1.0.0",
            "1.0.1",
            "1.1.0",
            "2.0.0",
            "2.1.0",
            "3.0.0"
    );

    @Test
    public void can_compare_equals_versions() {
        Comparator<Version> versionComparator = Comparator.naturalOrder();

        ascendingVersionsAsString.stream().forEach(versionAsString -> {
            Version first = Version.parse(versionAsString);
            Version second = Version.parse(versionAsString);

            assertEquals(first,second, "versions should be equals");
            assertEquals(versionComparator.compare(first, first), 0, "versions comparison should be 0");
            assertEquals(versionComparator.compare(first, second), 0, "versions comparison should be 0");
        });
    }

    @Test
    public void ensure_copy_constructor_lead_to_equality() {
        Version base = Version.parse("1");
        Version copy = new Version(base);

        assertEquals(base, copy, "version from copy constructor should equal initial version");
    }

    @Test
    public void ensure_non_equality() {
        List<Version> versions = Arrays.asList(Version.parse("1.0.0"), Version.parse("1.0.0-notinlist"));

        versions.forEach(version -> {
            ascendingVersionsAsString.stream()
                    .filter(isEqual("1.0.0").negate())
                    .map(Version::parse)
                    .forEach(v -> assertNotEquals(version, v));
        });
    }

    @Test
    public void can_compare_versions_ascending() {
        Comparator<Version> versionComparator = Comparator.naturalOrder();
        List<String> versions = new ArrayList<>(ascendingVersionsAsString);

        for (int i = 0; i < versions.size(); i++) {
            Version baseVersion = Version.parse(versions.get(i));
            for (int j = i + 1; j < versions.size(); j++) {
                Version cmpVersion = Version.parse(versions.get(j));
                assertLower(versionComparator, baseVersion, cmpVersion);
            }
        }
    }

    @Test
    public void can_compare_versions_descending() {
        Comparator<Version> versionComparator = Comparator.naturalOrder();
        List<String> versions = new ArrayList<>(ascendingVersionsAsString);
        Collections.reverse(versions);

        for (int i = 0; i < versions.size(); i++) {
            Version baseVersion = Version.parse(versions.get(i));
            for (int j = i + 1; j < versions.size(); j++) {
                Version cmpVersion = Version.parse(versions.get(j));
                assertGreater(versionComparator, baseVersion, cmpVersion);
            }
        }
    }

    @Test
    public void version_without_plus_and_minus_in_qualifiers() {
        Version parsedVersion = Version.parse("1.0-rc-2019");
        Version constructed = new Version(1, 0, 0, "rc", "2019");

        System.out.println(parsedVersion);
        System.out.println(constructed);

        assertEquals(parsedVersion, constructed);
    }

    private void assertLower(Comparator<Version> versionComparator, Version baseVersion, Version cmpVersion) {
        boolean isLower = versionComparator.compare(baseVersion, cmpVersion) < 0;
        assertTrue(isLower, String.format("%s should be lower than %s", baseVersion, cmpVersion));
    }

    private void assertGreater(Comparator<Version> versionComparator, Version baseVersion, Version cmpVersion) {
        boolean isGreater = versionComparator.compare(baseVersion, cmpVersion) > 0;
        assertTrue(isGreater, String.format("%s should be greater than %s", baseVersion, cmpVersion));
    }
}