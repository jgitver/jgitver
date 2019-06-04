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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
    public static final Version DEFAULT_VERSION = new Version(0, 0, 0);
    public static final Version EMPTY_REPOSITORY_VERSION = DEFAULT_VERSION.addQualifier("EMPTY_GIT_REPOSITORY");
    public static final Version NOT_GIT_VERSION = DEFAULT_VERSION.addQualifier("NOT_A_GIT_REPOSITORY");
    public static final Version NO_WORKTREE_AND_INDEX = DEFAULT_VERSION.addQualifier("NO_WORKTREE_AND_INDEX");

    private final int major;

    private final int minor;

    private final int patch;
    private final String stringRepresentation;
    private final List<String> qualifiers;

    public Version(Version other) {
        this(other.major, other.minor, other.patch, other.qualifiers);
    }

    public Version(int major, int minor, int patch, String...qualifiers) {
        this(major, minor, patch, Arrays.asList(qualifiers));
    }

    private Version(int major, int minor, int patch, List<String> qualifiers) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.qualifiers = new ArrayList<>(Objects.requireNonNull(qualifiers));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d.%d.%d", major, minor, patch));

        this.qualifiers.forEach(q -> {
            if (!q.startsWith("-") && !q.startsWith("+")) {
                sb.append('-');
            }
            sb.append(q);
        });
        this.stringRepresentation = sb.toString();
    }

    /**
     * Retrieves the MAJOR part of the version object.
     * @return the number for the major version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Retrieves the MINOR part of the version object.
     * @return the number for the minor version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Retrieves the PATCH part of the version object.
     * @return the number for the patch version
     */
    public int getPatch() {
        return patch;
    }


    @Override
    public String toString() {
        return stringRepresentation;
    }

    /**
     * Creates a new Version object from the current one, but append a new qualifier to it.
     * @param qualifier the qualifier to be added
     * @return a new Version object with exact same major/minor/patch numbers, previous qualifiers and the new added one
     */
    public Version addQualifier(String qualifier) {
        List<String> newQualifiers = new ArrayList<>(this.qualifiers);
        newQualifiers.add(qualifier);
        return new Version(major, minor, patch, newQualifiers.toArray(new String[newQualifiers.size()]));
    }
    
    /**
     * Creates a new Version object from the current one, but removes the given qualifier from it if it exists.
     * @param qualifier the qualifier to be removed
     * @return a new Version object with exact same major/minor/patch numbers, previous qualifiers without the given one
     */
    public Version removeQualifier(String qualifier) {
        List<String> newQualifiers = new ArrayList<>(this.qualifiers);
        newQualifiers.remove(qualifier);
        return new Version(major, minor, patch, newQualifiers.toArray(new String[newQualifiers.size()]));
    }
    
    /**
     * Creates a new Version object from the current one, but removes all qualifiers from it.
     * @return a new Version object with exact same major/minor/patch numbers, but without any qualifier
     */
    public Version noQualifier() {
        return new Version(major, minor, patch, Collections.emptyList());
    }
    
    private static final Pattern globalVersionPattern = Pattern.compile("^([0-9]+)(?:\\.([0-9]+))?(?:\\.([0-9]+))?([\\-\\+][a-zA-Z0-9][a-zA-Z0-9\\-_\\.\\+]*)?$");
    
    /**
     * Creates a {@link Version} object by parsing the given string.
     * @param versionAsString the string to parse
     * @return a Version object built from the information of the given representation
     * @throws IllegalStateException if the given string doesn't match the version
     */
    public static Version parse(String versionAsString) {
        Matcher globalVersionMatcher = globalVersionPattern.matcher(versionAsString);
        
        if (globalVersionMatcher.matches()) {
            int major = Integer.parseInt(globalVersionMatcher.group(1));
            int minor = Integer.parseInt(Optional.ofNullable(globalVersionMatcher.group(2)).orElse("0"));
            int patch = Integer.parseInt(Optional.ofNullable(globalVersionMatcher.group(3)).orElse("0"));
            
            String qualifiersAsString = globalVersionMatcher.group(4);
            
            String[] qualifiers = new String[0];
            if (qualifiersAsString != null) {
                qualifiers = qualifiersAsString.replaceFirst("-", "").split("\\-");
            }
            
            return new Version(major, minor, patch, qualifiers);
        }
        
        throw new IllegalStateException("cannot parse " + versionAsString + " as a semver compatible version");
    }


    /**
     * return a new Version object that is a copy of this one where the patch number is incremented by one.
     * @return a new Version object with incremented patch number
     */
    public Version incrementPatch() {
        return new Version(major, minor, patch + 1, qualifiers);
    }

    /**
     * return a new Version object that is a copy of this one where the minor number is incremented by one ; patch version is set to 0.
     * @return a new Version object with incremented patch number and 0 for patch number
     */
    public Version incrementMinor() {
        return new Version(major, minor + 1, 0, qualifiers);
    }

    /**
     * return a new Version object that is a copy of this one where the major number is incremented by one,
     * the minor and patch are set to 0.
     * @return a new Version object with incremented major number and 0 for minor and patch numbers
     */
    public Version incrementMajor() {
        return new Version(major + 1, 0, 0, qualifiers);
    }

    /**
     * return a new Version object that is a copy of this one where the patch number is incremented by one.
     * @return a new Version object with incremented patch number
     * @deprecated use {@link #incrementPatch()}
     */
    public Version increasePatch() {
        return incrementPatch();
    }

    public boolean isSnapshot() {
        return qualifiers.stream().anyMatch("SNAPSHOT"::equals);
    }
    
    public boolean isQualified() {
        return qualifiers.size() > 0;
    }

    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        if (minor != version.minor) return false;
        if (patch != version.patch) return false;
        if (!stringRepresentation.equals(version.stringRepresentation)) return false;
        return qualifiers.equals(version.qualifiers);
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + stringRepresentation.hashCode();
        result = 31 * result + qualifiers.hashCode();
        return result;
    }

    @Override
    public int compareTo(Version o) {
        if (this == o) {
            return 0;
        }

        int majorDiff = this.major - o.major;
        if (majorDiff != 0) {
            return majorDiff;
        }

        int minorDiff = this.minor - o.minor;
        if (minorDiff != 0) {
            return minorDiff;
        }

        int patchDiff = this.patch - o.patch;
        if (patchDiff != 0) {
            return patchDiff;
        }

        if (qualifiers.size() == 0) {
            return o.qualifiers.size() == 0 ? 0 : 1;
        }

        if (o.qualifiers.size() == 0) {
            return -1;
        }

        return this.stringRepresentation.compareTo(o.stringRepresentation);
    }
}
