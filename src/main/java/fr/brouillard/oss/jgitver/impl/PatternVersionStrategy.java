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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import fr.brouillard.oss.jgitver.Features;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.petitparser.context.Result;

import fr.brouillard.oss.jgitver.Version;
import fr.brouillard.oss.jgitver.impl.pattern.VersionGrammarParser;
import fr.brouillard.oss.jgitver.impl.pattern.VersionPatternGrammarDefinition;
import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

import static java.util.Optional.empty;

public class PatternVersionStrategy extends VersionStrategy<PatternVersionStrategy> {
    public static final String DEFAULT_VERSION_PATTERN = "${v}${<meta.QUALIFIED_BRANCH_NAME}${<meta.COMMIT_DISTANCE}";
    public static final String DEFAULT_TAG_VERSION_PATTERN = "${v}";

    private String versionPattern = null;
    private String tagVersionPattern = null;
    private boolean autoIncrementPatch = false;

    /**
     * Default constructor.
     *
     * @param vnc        the configuration to use
     * @param repository the git repository
     * @param git        a git helper object built from the repository
     * @param registrar  a storage for found/calculated metadata
     */
    public PatternVersionStrategy(VersionNamingConfiguration vnc, Repository repository, Git git, MetadataRegistrar registrar) {
        super(vnc, repository, git, registrar);
    }

    @Override
    public Version build(Commit head, List<Commit> parents) throws VersionCalculationException {
        try {
            Commit base = findVersionCommit(head, parents);
            Ref tagToUse = findTagToUse(head, base);
            Version baseVersion = getBaseVersionAndRegisterMetadata(base, tagToUse);
            Optional<String> branchPattern = empty();
            if (!isBaseCommitOnHead(head, base) && autoIncrementPatch) {
                // we are not on head
                if (GitUtils.isAnnotated(tagToUse)) {
                    // found tag to use was an annotated one, lets' increment the version automatically
                    baseVersion = baseVersion.incrementPatch();
                }
            }

            getRegistrar().registerMetadata(Metadatas.BASE_COMMIT_ON_HEAD, "" + isBaseCommitOnHead(head, base));
            
            int headDistance = base.getHeadDistance();
            getRegistrar().registerMetadata(Metadatas.COMMIT_DISTANCE, "" + headDistance);

            if (Features.DISTANCE_TO_ROOT.isActive()) {
                int headToRootDistance = GitUtils.distanceToRoot(getRepository(), head.getGitObject());
                getRegistrar().registerMetadata(Metadatas.COMMIT_DISTANCE_TO_ROOT, "" + headToRootDistance);
            }

            try (RevWalk walk = new RevWalk(getRepository())) {
                RevCommit rc = walk.parseCommit(head.getGitObject());
                String commitTimestamp = GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_TIMESTAMP, commitTimestamp);
                String isoCommitTimestamp = GitUtils.getIsoTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_ISO_TIMESTAMP, isoCommitTimestamp);
                baseVersion = baseVersion.addQualifier(commitTimestamp);
            }

            if (!GitUtils.isDetachedHead(getRepository())) {
                String branch = getRepository().getBranch();
                baseVersion = enhanceVersionWithBranch(baseVersion, branch);
                branchPattern = getVersionNamingConfiguration().branchPattern(branch);
            } else {
                // ugly syntax to bypass the final/effectively final pb to access vraiable in lambda
                Optional<String> externalyProvidedBranchName = GitUtils.providedBranchName();
                if (externalyProvidedBranchName.isPresent()) {
                    baseVersion = enhanceVersionWithBranch(baseVersion, externalyProvidedBranchName.get());
                    getRegistrar().registerMetadata(Metadatas.PROVIDED_BRANCH_NAME, externalyProvidedBranchName.get());
                }
            }


            String versionPattern = resolveVersionPattern(branchPattern);
            if (isBaseCommitOnHead(head, base) && GitUtils.isAnnotated(tagToUse)) {
                versionPattern = (this.tagVersionPattern != null) ? this.tagVersionPattern : DEFAULT_TAG_VERSION_PATTERN;
            }

            final Function<String, Optional<String>> env = (String t) -> Optional.ofNullable(System.getenv(t));
            final Function<String, Optional<String>> sys = (String t) -> Optional.ofNullable(System.getProperty(t));
            MetadataProvider metaProvider = MetadataProvider.class.cast(getRegistrar());

            VersionPatternGrammarDefinition def = new VersionPatternGrammarDefinition(baseVersion, env, sys, metaProvider::meta);
            VersionGrammarParser parser = new VersionGrammarParser(def);
            Result parseResult = parser.parse(versionPattern);
            if (parseResult.isSuccess()) {
                return parseResult.get();
            } else {
                String msg = String.format("cannot parse version using pattern: %s\nparsing failure: %s",
                        versionPattern,
                        parseResult.getMessage()
                );
                throw new VersionCalculationException(msg);
            }
        } catch (Exception ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }

    private String resolveVersionPattern(Optional<String> branchPattern) {
        return branchPattern.orElse(this.versionPattern != null ? this.versionPattern : DEFAULT_VERSION_PATTERN);
    }

    /**
     * Set the version pattern to use to compute the final version.
     * @param versionPattern a non null string describing the pattern
     */
    public PatternVersionStrategy setVersionPattern(String versionPattern) {
        return runAndGetSelf(() -> this.versionPattern = versionPattern);
    }

    /**
     * Set the version pattern to use to compute the final version when HEAD is on a commit with an annotated tag.
     * @param tagVersionPattern a non null string describing the pattern
     */
    public PatternVersionStrategy setTagVersionPattern(String tagVersionPattern) {
        return runAndGetSelf(() -> this.tagVersionPattern = tagVersionPattern);
    }

    /**
     * Set the parameter to increment automatically patch number for standard versions coming from annotated tag.
     * @param autoIncrementPatch true to increment the patch number, false otherwise
     * @return itself for chaining
     */
    public PatternVersionStrategy setAutoIncrementPatch(boolean autoIncrementPatch) {
        return runAndGetSelf(() -> this.autoIncrementPatch = autoIncrementPatch);
    }

}
