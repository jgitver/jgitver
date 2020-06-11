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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.brouillard.oss.jgitver.BranchingPolicy;
import fr.brouillard.oss.jgitver.GitVersionCalculator;
import fr.brouillard.oss.jgitver.JGitverProperties;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import picocli.CommandLine;

/**
 * Allows to run jgitver using a CLI.
 * @since 0.10.0
 */
public class RunnableCLI {
    private static final String ALL_METADATAS = "ALL";

    /**
     * Execute jgitver from CLI, parses args to configure.
     * @param args the arguments to parse to configure GitVersionCalculator
     */
    public static void main(String[] args) {
        System.exit(execute(args,System.out, System.err));
    }

    /**
     * Execute jgitver from CLI, parses args to configure and uses the given print streams to output messages.
     * @param args the arguments to parse to configure GitVersionCalculator
     * @param normal the standard PrintStream to output messages to
     * @param error the error PrintStream to output errors to
     */
    static int execute(String[] args, PrintStream normal, PrintStream error) {
        Options opts = new Options();
        CommandLine cli = new CommandLine(opts);

        try {
            cli.parseArgs(args);
        } catch (CommandLine.UnmatchedArgumentException ex) {
            CommandLine.usage(new Options(), error);
            return 1;
        }

        if (cli.isVersionHelpRequested()) {
            new CommandLine(new Options()).printVersionHelp(
                    normal,
                    CommandLine.Help.Ansi.AUTO,
                    JGitverProperties.getVersion(),
                    JGitverProperties.getSHA1()
            );
            return 2;
        }

        if (cli.isUsageHelpRequested()) {
            CommandLine.usage(new Options(), normal);
            return 3;
        }

        GitVersionCalculator gvc = GitVersionCalculator.location(opts.directory);

        gvc.setUseDistance(opts.useDistance);
        gvc.setAutoIncrementPatch(opts.autoIncrementPatch);
        gvc.setUseDirty(opts.useDirty);
        gvc.setForceComputation(opts.forceComputation);
        gvc.setUseLongFormat(opts.useLongFormat);
        gvc.setUseGitCommitId(opts.useGitCommitId);
        if (opts.useGitCommitId) {
            gvc.setGitCommitIdLength(opts.gitCommitIdLength);
        }
        gvc.setUseGitCommitTimestamp(opts.useGitCommitTimestamp);
        gvc.setStrategy(opts.strategy);
        if (opts.pattern != null) {
            gvc.setFindTagVersionPattern(opts.pattern);
        }
        gvc.setLookupPolicy(opts.policy);

        if (opts.qualifierBranchPolicies != null) {
            gvc.setQualifierBranchingPolicies(opts.qualifierBranchPolicies
                    .stream()
                    .map(
                            (Options.QualifierBranchPolicy policy) -> {
                                if(policy.branchPolicyPattern == null) {
                                    throw new IllegalArgumentException(
                                            "Each usage of branchPolicyTransformation must have a branchPolicyPattern");
                                }

                                if (policy.branchPolicyTransformations != null
                                        && policy.branchPolicyTransformations.size() > 0) {
                                    return new BranchingPolicy(
                                            policy.branchPolicyPattern,
                                            policy.branchPolicyTransformations
                                                    .stream()
                                                    .map(BranchingPolicy.BranchNameTransformations::name)
                                                    .collect(Collectors.toList()));
                                } else {
                                    return new BranchingPolicy(policy.branchPolicyPattern);
                                }
                            }
                    )
                    .collect(Collectors.toList()));
        }

        if (opts.nonQualifierBranches != null) {
            gvc.setNonQualifierBranches(opts.nonQualifierBranches);
        }

        if (opts.versionPattern != null) {
            gvc.setVersionPattern(opts.versionPattern);
        }

        if (opts.tagVersionPattern != null) {
            gvc.setTagVersionPattern(opts.tagVersionPattern);
        }

        if (opts.metadatas == null) {
            // no metadatas provided, just output the version
            normal.print(gvc.getVersion());
        } else {
            // some metadatas provided
            // only show the ones that are asked
            Stream<Metadatas> metadatas;
            if (ALL_METADATAS.equals(opts.metadatas)) {
                metadatas = Arrays.stream(Metadatas.values());
            } else {
                metadatas = Arrays.stream(opts.metadatas.split(",")).map(Metadatas::valueOf);
            }

            metadatas.forEach(m -> normal.println(String.format("%s=%s",m, gvc.meta(m).orElse("No value found"))));
        }

        return 0;
    }
}
