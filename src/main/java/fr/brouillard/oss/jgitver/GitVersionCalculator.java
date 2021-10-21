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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

import java.lang.reflect.InvocationTargetException;

import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public interface GitVersionCalculator extends AutoCloseable, MetadataProvider {
    /**
     * Creates a {@link GitVersionCalculator} for the git repository pointing to the given path.
     *
     * @param gitRepositoryLocation the location of the git repository to find version for
     * @return a non null {@link GitVersionCalculator}
     */
    static GitVersionCalculator location(File gitRepositoryLocation) {
        // ensure given directory exists and is readable
        if (!Objects.requireNonNull(gitRepositoryLocation).isDirectory() || !gitRepositoryLocation.canRead()) {
            throw new IllegalStateException("cannot work on non readable directory:" + gitRepositoryLocation);
        }

        ClassLoader[] classLoaders = {
            Thread.currentThread().getContextClassLoader(),
            GitVersionCalculator.class.getClassLoader()
        };

        for (ClassLoader classLoader : classLoaders) {
            Iterator<GitVersionCalculatorBuilder> builders = ServiceLoader.load(GitVersionCalculatorBuilder.class, classLoader).iterator();

            if (builders.hasNext()) {
                GitVersionCalculator gvc;
                try {
                    gvc = builders.next().build(gitRepositoryLocation);
                    return gvc;
                } catch (IOException ex) {
                    throw new IllegalStateException("cannot open git repository under: " + gitRepositoryLocation, ex);
                }
            }
        }

        try {
            Class builderClass = Class.forName("fr.brouillard.oss.jgitver.impl.GitVersionCalculatorImplBuilder");
            GitVersionCalculatorBuilder builder = (GitVersionCalculatorBuilder) builderClass.getConstructor().newInstance();
            return builder.build(gitRepositoryLocation);
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
            throw new IllegalStateException(
                    "cannot instantiate default GitVersionCalculatorImplBuilder class",
                    ex
            );
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "using GitVersionCalculatorImplBuilder cannot open git repository under: " + gitRepositoryLocation,
                    ex
            );
        }
    }

    /**
     * Calculates the version to use for the current git repository depending on the HEAD position.
     *
     * @param forceComputation true to discard any previous cached result, false allow to reuse already cached values
     * @return the version object computed
     */
    Version getVersionObject(boolean forceComputation);

    /**
     * Return the version to use for the current git repository depending on the HEAD position.
     * Can return cached values if no parameters have changed since last computation.
     *
     * @return the calculated version object
     */
    Version getVersionObject();

    /**
     * Return the version to use for the current git repository depending on the HEAD position.
     * If the computation was already performed, ust return the last computed value.
     *
     * @return a string representation of this version.
     */
    String getVersion();

    /**
     * Calculates if necessary the version to use for the current git repository depending on the HEAD position.
     *
     * @param forceComputation true to force computation even if no computation parameters have changed,
     *                         false to allow to retrieve the value from the last computed version.
     *
     * @return a string representation of this version.
     */
    String getVersion(boolean forceComputation);

    @Override
    void close() throws Exception;

    /**
     * When true, when the found tag to calculate a version for HEAD is a normal/annotated one, the semver patch version
     * of the tag is increased by one ; except when the tag is on the HEAD itself. This action is not in use if the
     * SNAPSHOT qualifier is present on the found version or if the found tag is a lightweight one.
     *
     * @param value if true and when found tag is not on HEAD, then version returned will be the found version with
     *        patch number increased by one. default false.
     * @return itself to chain settings
     */
    GitVersionCalculator setAutoIncrementPatch(boolean value);

    /**
     * When true, when the found tag to calculate a version for HEAD is a normal/annotated one, the semver minor version
     * of the tag is increased by one and the patch version is set to 0 ;
     * except when the tag is on the HEAD itself. This action is not in use if the
     * SNAPSHOT qualifier is present on the found version or if the found tag is a lightweight one.
     *
     * @param value if true and when found tag is not on HEAD, then version returned will be the found version with
     *        minor number increased by one and patch number set to 0. default false.
     * @return itself to chain settings
     */
    GitVersionCalculator setAutoIncrementMinor(boolean value);

    /**
     * Defines a comma separated list of branches for which no branch name qualifier will be used. default "master".
     * Example: "master, integration"
     * This method overrides the usage of {@link #setQualifierBranchingPolicies(List)} &amp;
     * {@link #setQualifierBranchingPolicies(BranchingPolicy...)}.
     *
     * @param nonQualifierBranches a comma separated list of branch name for which no branch name qualifier should be
     *        used, can be null and/or empty
     * @return itself to chain settings
     */
    GitVersionCalculator setNonQualifierBranches(String nonQualifierBranches);

    /**
     * Sets as an array the policies that will be applied to try to build a qualifier from the branch of the HEAD.
     * This method overrides the usage of {@link #setNonQualifierBranches(String)} & {@link #setQualifierBranchingPolicies(List)}.
     *
     * @param policies an array of policies to apply can be empty
     * @return itself to chain settings
     */
    GitVersionCalculator setQualifierBranchingPolicies(BranchingPolicy... policies);

    /**
     * Sets as a list the policies that will be applied to try to build a qualifier from the branch of the HEAD.
     * This method overrides the usage of {@link #setNonQualifierBranches(String)} &amp;
     * {@link #setQualifierBranchingPolicies(BranchingPolicy...)}.
     *
     * @param policies an array of policies to apply can be empty
     * @return itself to chain settings
     */
    GitVersionCalculator setQualifierBranchingPolicies(List<BranchingPolicy> policies);

    /**
     * When true, append a qualifier with the distance between the HEAD commit and the found commit with a version tag.
     * This qualifier is not used if the SNAPSHOT qualifier is used.
     *
     * @param useDistance if true, a qualifier with found distance will be used.
     * @return itself to chain settings
     */
    GitVersionCalculator setUseDistance(boolean useDistance);

    /**
     * When true, append a qualifier with the "dirty" qualifier if the repository is in a dirty state (ie with
     * uncommited changes or new files)
     *
     * @param useDirty if true, a qualifier with "dirty" qualifier will be used if the repository is stall.
     * @return itself to chain settings
     */
    GitVersionCalculator setUseDirty(boolean useDirty);

    /**
     * When true describes commits hash with long format pattern, ie preceded with the letter 'g'.
     * @param useLongFormat if true and useGitCommitId,
     *                      then commitId will be prepended with a 'g' to be compliant with `git describe --long` format
     * @return itself to chain settings
     */
    GitVersionCalculator setUseLongFormat(boolean useLongFormat);

    /**
     * When true, append the git commit id (SHA1) to the version. This qualifier is not used if the SNAPSHOT qualifier
     * is used.
     *
     * @param useGitCommitId if true, a qualifier with SHA1 git commit will be used, default true
     * @return itself to chain settings
     */
    GitVersionCalculator setUseGitCommitId(boolean useGitCommitId);

    /**
     * When true, append the git commit timestamp to the version. This qualifier is not used if the SNAPSHOT qualifier
     * is used.
     *
     * @param useGitCommitTimestamp if true, a qualifier with git commit timestamp will be used, default false
     * @return itself to chain settings
     */
    GitVersionCalculator setUseGitCommitTimestamp(boolean useGitCommitTimestamp);

    /**
     * When true, uses {@link BranchingPolicy#DEFAULT_FALLBACK} as last {@link BranchingPolicy}.
     *
     * @param useDefaultBranchingPolicy if true, appends {@link BranchingPolicy#DEFAULT_FALLBACK} as last branching policy
     * @return itself to chain settings
     */
    GitVersionCalculator setUseDefaultBranchingPolicy(boolean useDefaultBranchingPolicy);

    /**
     * Defines how long the qualifier from SHA1 git commit has to be.
     *
     * @param gitCommitIdLength the length of the SHA1 substring to use as qualifier, valid values [8, 40], default 8
     * @return itself to chain settings
     * @throws IllegalArgumentException in case the length is not in the range [8,40]
     */
    GitVersionCalculator setGitCommitIdLength(int gitCommitIdLength);

    /**
     * Activates the maven like mode.
     *
     * @param mavenLike true to activate maven like mode
     * @return itself to chain settings
     * @deprecated since 0.7.0, use {@link #setStrategy(Strategies)} instead
     */
    GitVersionCalculator setMavenLike(boolean mavenLike);

    /**
     * Defines a regexp search pattern that will match tags identifying a version.
     * The provided regexp MUST contains at least one selector group that will represent the version extracted from the tag.
     * @param pattern a non null string representing a java regexp pattern able to match tag containing versions
     * @return itself to chain settings
     * @throws java.util.regex.PatternSyntaxException if the given string cannot be parsed
     *      as a correct {@link java.util.regex.Pattern} object
     */
    GitVersionCalculator setFindTagVersionPattern(String pattern);

    @Override
    Optional<String> meta(Metadatas meta);

    /**
     * Defines the strategy to use.
     * @param s the non null strategy to use as a {@link Strategies} enum value
     * @return itself to chain settings
     * @since 0.7.0
     */
    GitVersionCalculator setStrategy(Strategies s);

    /**
     * Defines the version pattern to use in {@link Strategies#PATTERN} mode when HEAD is on an annotated tag
     * @param pattern the pattern to use for annotated tags
     * @return itself to chain settings
     * @since 0.7.0
     */
    GitVersionCalculator setTagVersionPattern(String pattern);

    /**
     * Defines the version pattern to use in {@link Strategies#PATTERN} mode for normal situation (i.e. not on a tag)
     * @param pattern the pattern to use
     * @return itself to chain settings
     * @since 0.7.0
     */
    GitVersionCalculator setVersionPattern(String pattern);

    /**
     * Defines max depth to look for version tags, defaults to Integer.MAX_VALUE.
     * @param maxDepth the maximum depth to reach to lookup for version tags
     * @return itself to chain settings
     * @since 0.8.6
     */
    GitVersionCalculator setMaxDepth(int maxDepth);

    /**
     * Defines the {@link LookupPolicy} to be used for the next version resolution.
     * @param policy the policy kind, cannot be null
     * @return itself to chain settings
     * @since 0.10.0
     */
    GitVersionCalculator setLookupPolicy(LookupPolicy policy);

    /**
     * When true, append a qualifier with the "SNAPSHOT" qualifier if no version tags found.
     *
     * @param useSnapshot if true, a qualifier with "SNAPSHOT" qualifier if no version tags found.
     * @return itself to chain settings
     * @since 0.12.0
     */
    GitVersionCalculator setUseSnapshot(boolean useSnapshot);
    
    /**
     * When true, force the computation of the version even if HEAD is on a version tag.
     *
     * @param forceComputation if true, forces the computation of version using other defined parameters
     * @return itself to chain settings
     * @since 0.13.0
     */
    GitVersionCalculator setForceComputation(boolean forceComputation);

    /**
     * Set the type of the script (default: {@link ScriptType#GROOVY}).
     * @param scriptType the script type
     * @return itself for chaining
     */
    GitVersionCalculator setScriptType(ScriptType scriptType);

    /**
     * Set the script to be interpreted.
     * @param script the script content
     * @return itself for chaining
     */
    GitVersionCalculator setScript(String script);
}
