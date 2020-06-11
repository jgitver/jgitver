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
package fr.brouillard.oss.jgitver.cli;

import java.io.File;
import java.util.List;

import fr.brouillard.oss.jgitver.BranchingPolicy;
import fr.brouillard.oss.jgitver.LookupPolicy;
import fr.brouillard.oss.jgitver.Strategies;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "java -jar jgitver-executable.jar", version = {"jgitver %s (%s)"})
public class Options {
    @SuppressWarnings("unused")
    @Option(names = {"-V", "--version"}, versionHelp = true, description = "display version info")
    boolean versionInfoRequested;

    @SuppressWarnings("unused")
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display usage")
    boolean helpRequested = false;

    @Option(names = {"--autoIncrementPatch"}, description = "activate auto increment patch functionality")
    boolean autoIncrementPatch = true;

    @Option(names = {"--useDistance"}, description = "activate distance qualifier")
    boolean useDistance = true;

    @Option(names = {"--useDirty"}, description = "activate dirty flag")
    boolean useDirty = true;

    @Option(names = {"--forceComputation", "-fc"}, description = "activate forceComputation flag")
    boolean forceComputation = false;

    @Option(names = {"--useLongFormat"}, description = "activate long format usage")
    boolean useLongFormat = false;

    @Option(names = {"--useGitCommitId"}, description = "add git commit id as qualifier")
    boolean useGitCommitId = false;

    @Option(names = {"--gitCommitIdLength"}, description = "length of the git commit id if used")
    Integer gitCommitIdLength;

    @Option(names = {"--useGitCommitTimestamp"}, description = "add git commit timestamp as qualifier")
    boolean useGitCommitTimestamp = false;

    @Option(names = {"--strategy"}, description = "defines the strategy to use")
    Strategies strategy = Strategies.CONFIGURABLE;

    @Option(names = {"--pattern"}, description = "pattern to identify base tags as versionable ones")
    String pattern;

    @Option(names = {"--policy"}, description = "lookup policy to use to find the base tag to use")
    LookupPolicy policy = LookupPolicy.MAX;

    @Option(names = {"--nonQualifierBranches"}, description = "list of fixed name for non qualifier branches")
    String nonQualifierBranches;

    @Option(names = {"--versionPattern"}, description = "set version pattern (PATTERN strategy)")
    String versionPattern;

    @Option(names = {"--tagVersionPattern"}, description = "set version pattern for when on annotated tag (PATTERN strategy)")
    String tagVersionPattern;

    @Option(names = {"--dir", "--directory"}, description = "root directory for git project")
    File directory = new File(System.getProperty("user.dir"));

    @Option(names = {"--metas", "--metadatas"}, description = "metadatas to show, separated by ','")
    String metadatas;

    @ArgGroup(exclusive = false, multiplicity = "*")
    List<QualifierBranchPolicy> qualifierBranchPolicies;

    static class QualifierBranchPolicy {
        @Option(names = "--branchPolicyPattern", required = true, description = "regex to match a branch name")
        String branchPolicyPattern;
        @Option(names = "--branchPolicyTransformations",
                split = ",",
                description = "transformations to apply to the branchPolicyPattern match")
        List<BranchingPolicy.BranchNameTransformations> branchPolicyTransformations;
    }
}
