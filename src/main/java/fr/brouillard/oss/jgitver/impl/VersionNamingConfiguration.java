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
package fr.brouillard.oss.jgitver.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import fr.brouillard.oss.jgitver.BranchingPolicy;

public class VersionNamingConfiguration {
    public static final Pattern DEFAULT_FIND_TAG_VERSION_PATTERN = Pattern.compile("v?([0-9]+(?:\\.[0-9]+){0,2}(?:-[a-zA-Z0-9\\-_]+)?)");
    private static final String EXTRACT_TAG_VERSION_PATTERN = "$1";

    private Pattern searchPattern;
    private List<BranchingPolicy> branchPolicies;

    /**
     * Builds a Configuration object holding information to use while building version.
     * Uses a default pattern matching <code>[v]X[.Y[.Z[-qualifiers]]]</code>
     * @param policies list of policies to apply for branch qualifier 
     */
    public VersionNamingConfiguration(BranchingPolicy ... policies) {
        this.branchPolicies = new LinkedList<>(Arrays.asList(policies));
        this.searchPattern = DEFAULT_FIND_TAG_VERSION_PATTERN;
    }
    
    /**
     * Builds a Configuration object holding information to use while building version.
     * @param searchVersionPattern a regex pattern that will be applied to the repository tag list 
     *      to filter only the tags that represent a version
     * @param policies list of policies to apply for branch qualifier 
     */
    public VersionNamingConfiguration(Pattern searchVersionPattern, BranchingPolicy ... policies) {
        this.branchPolicies = new LinkedList<>(Arrays.asList(policies));
        this.searchPattern = searchVersionPattern;
    }
    
    protected Pattern getSearchPattern() {
        return searchPattern;
    }
    
    protected String getReplaceVersionRegex() {
        return EXTRACT_TAG_VERSION_PATTERN;
    }
    
    public String extractVersionFrom(String tagName) {
        return searchPattern.matcher(tagName).replaceAll(getReplaceVersionRegex());
    }
    
    /**
     * Builds an optional qualifier from the given branch name.
     * Depending on the settings given {@link #VersionNamingConfiguration(String, String, BranchingPolicy...)} during construction,
     * a qualifier will or not be built.
     * @param branch the branch name for which a qualifier should be built
     * @return a non null optional object containing or not a qualifier 
     */
    public Optional<String> branchQualifier(String branch) {
        for (BranchingPolicy bPolicy : branchPolicies) {
            if (bPolicy.appliesOn(branch)) {
                return bPolicy.qualifier(branch);
            }
        }
        return Optional.empty();
    }

    public Optional<String> branchPattern(String branch) {
        for (BranchingPolicy bPolicy : branchPolicies) {
            if (bPolicy.appliesOn(branch)) {
                return bPolicy.getVersionPattern();
            }
        }
        return Optional.empty();
    }
}
