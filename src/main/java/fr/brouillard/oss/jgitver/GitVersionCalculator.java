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

import static fr.brouillard.oss.jgitver.Lambdas.as;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import fr.brouillard.oss.jgitver.BranchingPolicy.BranchNameTransformations;
import fr.brouillard.oss.jgitver.impl.Commit;
import fr.brouillard.oss.jgitver.impl.ConfigurableVersionStrategy;
import fr.brouillard.oss.jgitver.impl.GitUtils;
import fr.brouillard.oss.jgitver.impl.MavenVersionStrategy;
import fr.brouillard.oss.jgitver.impl.PatternVersionStrategy;
import fr.brouillard.oss.jgitver.impl.VersionNamingConfiguration;
import fr.brouillard.oss.jgitver.impl.VersionStrategy;
import fr.brouillard.oss.jgitver.impl.VersionStrategy.StrategySearchMode;
import fr.brouillard.oss.jgitver.metadata.MetadataHolder;
import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.metadata.TagType;

public class GitVersionCalculator implements AutoCloseable, MetadataProvider {
    private MetadataHolder metadatas;
    private Repository repository;
    private boolean mavenLike = false;
    private boolean autoIncrementPatch = false;
    private boolean useDistance = true;
    private boolean useGitCommitId = false;
    private boolean useGitCommitTimestamp = false;
    private boolean useDirty = false;
    private boolean useLongFormat = false;
    private int gitCommitIdLength = 8;
    private List<BranchingPolicy> qualifierBranchingPolicies;
    private boolean useDefaultBranchingPolicy = true;
    private Strategies versionStrategy = null;
    private String tagVersionPattern = null;
    private String versionPattern = null;

    private File gitRepositoryLocation;

    private final SimpleDateFormat dtfmt;
    private Pattern findTagVersionPattern = VersionNamingConfiguration.DEFAULT_FIND_TAG_VERSION_PATTERN;

    private GitVersionCalculator(File gitRepositoryLocation) throws IOException {
        this.gitRepositoryLocation = gitRepositoryLocation;

        dtfmt = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        setNonQualifierBranches("master");
    }

    /**
     * Creates a {@link GitVersionCalculator} for the git repository pointing to the given path.
     * 
     * @param gitRepositoryLocation the location of the git repository to find version for
     * @return a non null {@link GitVersionCalculator}
     */
    public static GitVersionCalculator location(File gitRepositoryLocation) {
        GitVersionCalculator gvc;
        try {
            gvc = new GitVersionCalculator(gitRepositoryLocation);
            return gvc;
        } catch (IOException ex) {
            throw new IllegalStateException("cannot open git repository under: " + gitRepositoryLocation, ex);
        }
    }

    private Repository openRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.findGitDir(gitRepositoryLocation).build();
    }

    /**
     * Calculates the version to use for the current git repository depending on the HEAD position.
     * 
     * @return the calculated version object
     */
    public Version getVersionObject() {
        metadatas = new MetadataHolder();

        try {
            this.repository = openRepository();
        } catch (Exception ex) {
            return Version.NOT_GIT_VERSION;
        }
        try (Git git = new Git(repository)) {
            VersionStrategy strategy;
            
            List<BranchingPolicy> policiesToUse = new LinkedList<>(qualifierBranchingPolicies);
            if (useDefaultBranchingPolicy) {
                policiesToUse.add(BranchingPolicy.DEFAULT_FALLBACK);
            }

            VersionNamingConfiguration vnc = new VersionNamingConfiguration(
                    findTagVersionPattern,
                    policiesToUse.toArray(new BranchingPolicy[policiesToUse.size()])
            );

            if (versionStrategy == null) {
                // no versionStrategy defined yet
                // we use historical mavenLike for compatibility purposes
                versionStrategy = mavenLike ? Strategies.MAVEN : Strategies.CONFIGURABLE;
            }

            switch (versionStrategy) {
                case MAVEN:
                    strategy = new MavenVersionStrategy(vnc, repository, git, metadatas)
                            .setUseDirty(useDirty);
                    break;
                case CONFIGURABLE:
                    strategy = new ConfigurableVersionStrategy(vnc, repository, git, metadatas)
                            .setAutoIncrementPatch(autoIncrementPatch)
                            .setUseDistance(useDistance)
                            .setUseDirty(useDirty)
                            .setUseGitCommitId(useGitCommitId)
                            .setGitCommitIdLength(gitCommitIdLength)
                            .setUseCommitTimestamp(useGitCommitTimestamp)
                            .setUseLongFormat(useLongFormat);
                    break;
                case PATTERN:
                    strategy = new PatternVersionStrategy(vnc, repository, git, metadatas)
                            .setAutoIncrementPatch(autoIncrementPatch)
                            .setVersionPattern(versionPattern)
                            .setTagVersionPattern(tagVersionPattern);
                    break;
                default:
                    throw new IllegalStateException("unknown strategy: " + versionStrategy);
            }

            Version calculatedVersion = buildVersion(git, strategy);

            return calculatedVersion;
        }
    }

    private boolean hasPatchVersionBeenIncremented(VersionStrategy strategy, Version calculatedVersion) {
        if (Version.EMPTY_REPOSITORY_VERSION.equals(calculatedVersion)) {
            return false;
        }
        return (autoIncrementPatch || strategy instanceof MavenVersionStrategy)
                && metadatas.meta(Metadatas.HEAD_VERSION_TAGS).get().isEmpty();
    }

    private void provideNextVersionsMetadatas(Version calculatedVersion, boolean patchIsIncremented) {
        Version unqualifiedCalculatedVersion = calculatedVersion.noQualifier();
        Version baseVersion = Version.parse(metadatas.meta(Metadatas.BASE_VERSION).get());
        Version unqualifiedBaseVersion = baseVersion.noQualifier();

        if (baseVersion.isSnapshot() || TagType.LIGHTWEIGHT.name().equals(metadatas.meta(Metadatas.BASE_TAG_TYPE).orElse(null))) {
            // base version was a snapshot or a lightweight tag, meaning the version it represent has never been published yet
            metadatas.registerMetadata(Metadatas.NEXT_PATCH_VERSION, unqualifiedBaseVersion.toString());
            if (unqualifiedBaseVersion.getPatch() == 0) {
                metadatas.registerMetadata(Metadatas.NEXT_MINOR_VERSION, unqualifiedBaseVersion.toString());
            } else {
                metadatas.registerMetadata(Metadatas.NEXT_MINOR_VERSION, unqualifiedBaseVersion.incrementMinor().toString());
            }
            if (unqualifiedBaseVersion.getMinor() == 0) {
                metadatas.registerMetadata(Metadatas.NEXT_MAJOR_VERSION, unqualifiedBaseVersion.toString());
            } else {
                metadatas.registerMetadata(Metadatas.NEXT_MAJOR_VERSION, unqualifiedBaseVersion.incrementMajor().toString());
            }
        } else {
            if (patchIsIncremented) {
                if (unqualifiedCalculatedVersion.equals(unqualifiedBaseVersion)) {
                    // we're probably on the tag itself don't do anything to the base version
                } else {
                    // we need to decrement the patch number
                    unqualifiedCalculatedVersion = new Version(
                            unqualifiedCalculatedVersion.getMajor(),
                            unqualifiedCalculatedVersion.getMinor(),
                            unqualifiedCalculatedVersion.getPatch() - 1
                    );
                }
            }

            metadatas.registerMetadata(Metadatas.NEXT_MAJOR_VERSION, unqualifiedCalculatedVersion.incrementMajor().toString());
            metadatas.registerMetadata(Metadatas.NEXT_MINOR_VERSION, unqualifiedCalculatedVersion.incrementMinor().toString());
            metadatas.registerMetadata(Metadatas.NEXT_PATCH_VERSION, unqualifiedCalculatedVersion.incrementPatch().toString());
        }
    }

    /**
     * Calculates the version to use for the current git repository depending on the HEAD position.
     * 
     * @return a string representation of this version.
     */
    public String getVersion() {
        return getVersionObject().toString();
    }

    private Version buildVersion(Git git, VersionStrategy strategy) {
        try {
            //
            metadatas.registerMetadata(Metadatas.DIRTY, "" + GitUtils.isDirty(git));
            
            // retrieve all tags matching a version, and get all info for each of them
            List<Ref> allTags = git.tagList().call().stream().map(this::peel)
                    .collect(Collectors.toCollection(ArrayList::new));
            // let's have tags sorted from most recent to oldest
            Collections.reverse(allTags);

            metadatas.registerMetadataTags(Metadatas.ALL_TAGS, allTags.stream());
            metadatas.registerMetadataTags(Metadatas.ALL_ANNOTATED_TAGS, allTags.stream().filter(GitUtils::isAnnotated));
            metadatas.registerMetadataTags(Metadatas.ALL_LIGHTWEIGHT_TAGS, allTags.stream().filter(as(GitUtils::isAnnotated).negate()));

            List<Ref> allVersionTags = allTags.stream()
                    .filter(strategy::considerTagAsAVersionOne)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<Ref> normals = allVersionTags.stream().filter(GitUtils::isAnnotated).collect(Collectors.toList());
            List<Ref> lights = allVersionTags.stream().filter(as(GitUtils::isAnnotated).negate()).collect(Collectors.toList());

            metadatas.registerMetadataTags(Metadatas.ALL_VERSION_TAGS, allVersionTags.stream());
            metadatas.registerMetadataTags(Metadatas.ALL_VERSION_ANNOTATED_TAGS, normals.stream());
            metadatas.registerMetadataTags(Metadatas.ALL_VERSION_LIGHTWEIGHT_TAGS, lights.stream());

            ObjectId rootId = repository.resolve("HEAD");

            // handle a call on an empty git repository
            if (rootId == null) {
                // no HEAD exist
                // the GIT repo might just be initialized without any commit
                return Version.EMPTY_REPOSITORY_VERSION;
            }

            git.log().add(rootId).setMaxCount(1).call().spliterator().tryAdvance(rc -> {
                PersonIdent commitInfo = rc.getAuthorIdent();
                metadatas.registerMetadata(Metadatas.HEAD_COMMITTER_NAME, commitInfo.getName());
                metadatas.registerMetadata(Metadatas.HEAD_COMMITER_EMAIL, commitInfo.getEmailAddress());
                dtfmt.setTimeZone(commitInfo.getTimeZone());
                metadatas.registerMetadata(Metadatas.HEAD_COMMIT_DATETIME, dtfmt.format(commitInfo.getWhen()));
            });

            metadatas.registerMetadataTags(Metadatas.HEAD_TAGS, tagsOf(allTags, rootId).stream());
            metadatas.registerMetadataTags(Metadatas.HEAD_ANNOTATED_TAGS,
                    tagsOf(allTags.stream().filter(GitUtils::isAnnotated).collect(Collectors.toList()), rootId)
                            .stream());
            metadatas.registerMetadataTags(Metadatas.HEAD_LIGHTWEIGHT_TAGS,
                    tagsOf(allTags.stream().filter(as(GitUtils::isAnnotated).negate()).collect(Collectors.toList()),
                            rootId).stream());
            metadatas.registerMetadataTags(Metadatas.HEAD_VERSION_TAGS, tagsOf(allVersionTags, rootId).stream());
            metadatas.registerMetadataTags(Metadatas.HEAD_VERSION_ANNOTATED_TAGS,
                    tagsOf(allVersionTags.stream().filter(GitUtils::isAnnotated).collect(Collectors.toList()), rootId)
                            .stream());
            metadatas.registerMetadataTags(Metadatas.HEAD_VERSION_LIGHTWEIGHT_TAGS,
                    tagsOf(allVersionTags.stream().filter(as(GitUtils::isAnnotated).negate()).collect(Collectors.toList()),
                            rootId).stream());

            metadatas.registerMetadata(Metadatas.GIT_SHA1_FULL, rootId.getName());
            metadatas.registerMetadata(Metadatas.GIT_SHA1_8, rootId.getName().substring(0, 8));
            
            Commit head = new Commit(rootId, 0, tagsOf(normals, rootId), tagsOf(lights, rootId));

            // a set is used so that only one commit is kept for commits
            // reachable by 2 different paths
            Set<Commit> commits = new LinkedHashSet<>();

            try (RevWalk revWalk = new RevWalk(repository)) {
                Commit stoppedCommit = lookupCommits(rootId, 0, commits, normals, lights, revWalk);
                if (commits.isEmpty()) {
                    // need at least the deepest commit (ie the first of the repo)
                    // if no commit with version tag could be found
                    commits.add(stoppedCommit);
                }
            }

            Version calculatedVersion = strategy.build(head, new ArrayList<>(commits));
            metadatas.registerMetadata(Metadatas.CALCULATED_VERSION, calculatedVersion.toString());

            // Calculated version could have the patch already incremented under conditions
            boolean patchVersionIsIncremented = hasPatchVersionBeenIncremented(strategy, calculatedVersion);
            provideNextVersionsMetadatas(calculatedVersion, patchVersionIsIncremented);

            return calculatedVersion;
        } catch (Exception ex) {
            throw new IllegalStateException("failure calculating version", ex);
        }
    }

    /**
     * Navigate to reachable commits, including merged branches, from the given commit
     * and stop each time a commit with some _version_ tags is found on the currentCommit.
     * @param currentCommitId the commit identifier to search tags on and to navigate to parents from
     * @param depth the current depth since the HEAD
     * @param commits the accumulator set of commits
     * @param normals list of all annotated tags of this git repo
     * @param lights list of all lightweight tags of this git repo
     * @param revWalk the jgit walker able to parse commits
     * @return the commit with minimal depth the lookup process stopped onto
     */
    private Commit lookupCommits(
            ObjectId currentCommitId,
            int depth,
            Set<Commit> commits,
            List<Ref> normals,
            List<Ref> lights,
            RevWalk revWalk
    ) throws IOException {
        RevCommit currentCommit = revWalk.parseCommit(currentCommitId);

        List<Ref> annotatedCommitTags = tagsOf(normals, currentCommitId);
        List<Ref> lightCommitTags = tagsOf(lights, currentCommitId);

        if (annotatedCommitTags.size() > 0 || lightCommitTags.size() > 0) {
            // we found a commit with version tags
            Commit c = new Commit(currentCommitId, depth, annotatedCommitTags, lightCommitTags);
            commits.add(c);
            return c;
        }

        RevCommit[] parents = currentCommit.getParents();
        if (parents.length == 0) {
            return new Commit(currentCommit, depth, Collections.emptyList(), Collections.emptyList());
        } else {
            Commit minDepthStoppedCommit = null;

            for (RevCommit parent: parents) {
                Commit parentStoppedCommit = lookupCommits(parent.getId(), depth + 1, commits, normals, lights, revWalk);
                minDepthStoppedCommit = keepNearest(minDepthStoppedCommit, parentStoppedCommit);
            }
            if (minDepthStoppedCommit == null) {
                return new Commit(currentCommit, depth, Collections.emptyList(), Collections.emptyList());
            }
            return minDepthStoppedCommit;
        }
    }

    private Commit keepNearest(Commit minDepthStoppedCommit, Commit parentStoppedCommit) {
        if (minDepthStoppedCommit == null) {
            return parentStoppedCommit;
        }

        return (minDepthStoppedCommit.getHeadDistance() <= parentStoppedCommit.getHeadDistance())
            ? minDepthStoppedCommit : parentStoppedCommit;
    }

    private List<Ref> tagsOf(List<Ref> tags, final ObjectId id) {
        return tags.stream().filter(ref -> id.equals(ref.getObjectId()) || id.equals(ref.getPeeledObjectId()))
                .collect(Collectors.toList());
    }

    private Ref peel(Ref tag) {
        return repository.peel(tag);
    }

    @Override
    public void close() throws Exception {
        if (repository != null) {
            repository.close();
        }
    }

    /**
     * When true, when the found tag to calculate a version for HEAD is a normal/annotated one, the semver patch version
     * of the tag is increased by one ; except when the tag is on the HEAD itself. This action is not in use if the
     * SNAPSHOT qualifier is present on the found version or if the found tag is a lightweight one.
     * 
     * @param value if true and when found tag is not on HEAD, then version returned will be the found version with
     *        patch number increased by one. default false.
     * @return itself to chain settings
     */
    public GitVersionCalculator setAutoIncrementPatch(boolean value) {
        this.autoIncrementPatch = value;
        return this;
    }

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
    public GitVersionCalculator setNonQualifierBranches(String nonQualifierBranches) {
        List<BranchingPolicy> branchPolicies = new LinkedList<>();
        
        if (nonQualifierBranches != null && !"".equals(nonQualifierBranches.trim())) {
            for (String branch : nonQualifierBranches.split(",")) {
                branchPolicies.add(
                        BranchingPolicy.fixedBranchName(
                                branch, 
                                Collections.singletonList(BranchNameTransformations.IGNORE.name())
                        )
                );
            }
        }
        
        return setQualifierBranchingPolicies(branchPolicies);
    }
    
    /**
     * Sets as an array the policies that will be applied to try to build a qualifier from the branch of the HEAD.
     * This method overrides the usage of {@link #setNonQualifierBranches(String)} & {@link #setQualifierBranchingPolicies(List)}.
     * 
     * @param policies an array of policies to apply can be empty
     * @return itself to chain settings
     */
    public GitVersionCalculator setQualifierBranchingPolicies(BranchingPolicy...policies) {
        return setQualifierBranchingPolicies(Arrays.asList(policies));
    }
    
    /**
     * Sets as a list the policies that will be applied to try to build a qualifier from the branch of the HEAD.  
     * This method overrides the usage of {@link #setNonQualifierBranches(String)} &amp;
     * {@link #setQualifierBranchingPolicies(BranchingPolicy...)}.
     * 
     * @param policies an array of policies to apply can be empty
     * @return itself to chain settings
     */
    public GitVersionCalculator setQualifierBranchingPolicies(List<BranchingPolicy> policies) {
        if (policies != null) {
            this.qualifierBranchingPolicies = new LinkedList<>(policies);
        }
        return this;
    }

    /**
     * When true, append a qualifier with the distance between the HEAD commit and the found commit with a version tag.
     * This qualifier is not used if the SNAPSHOT qualifier is used.
     * 
     * @param useDistance if true, a qualifier with found distance will be used.
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseDistance(boolean useDistance) {
        this.useDistance = useDistance;
        return this;
    }

    /**
     * When true, append a qualifier with the "dirty" qualifier if the repository is in a dirty state (ie with
     * uncommited changes or new files)
     * 
     * @param useDirty if true, a qualifier with "dirty" qualifier will be used if the repository is stall.
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseDirty(boolean useDirty) {
        this.useDirty = useDirty;
        return this;
    }

    /**
     * When true describes commits hash with long format pattern, ie preceded with the letter 'g'.
     * @param useLongFormat if true and useGitCommitId,
     *                      then commitId will be prepended with a 'g' to be compliant with `git describe --long` format
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseLongFormat(boolean useLongFormat) {
        this.useLongFormat = useLongFormat;
        return this;
    }

    /**
     * When true, append the git commit id (SHA1) to the version. This qualifier is not used if the SNAPSHOT qualifier
     * is used.
     * 
     * @param useGitCommitId if true, a qualifier with SHA1 git commit will be used, default true
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseGitCommitId(boolean useGitCommitId) {
        this.useGitCommitId = useGitCommitId;
        return this;
    }

    /**
     * When true, append the git commit timestamp to the version. This qualifier is not used if the SNAPSHOT qualifier
     * is used.
     * 
     * @param useGitCommitTimestamp if true, a qualifier with git commit timestamp will be used, default false
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseGitCommitTimestamp(boolean useGitCommitTimestamp) {
        this.useGitCommitTimestamp = useGitCommitTimestamp;
        return this;
    }
    
    /**
     * When true, uses {@link BranchingPolicy#DEFAULT_FALLBACK} as last {@link BranchingPolicy}.
     * 
     * @param useDefaultBranchingPolicy if true, appends {@link BranchingPolicy#DEFAULT_FALLBACK} as last branching policy
     * @return itself to chain settings
     */
    public GitVersionCalculator setUseDefaultBranchingPolicy(boolean useDefaultBranchingPolicy) {
        this.useDefaultBranchingPolicy = useDefaultBranchingPolicy;
        return this;
    }
    

    /**
     * Defines how long the qualifier from SHA1 git commit has to be.
     * 
     * @param gitCommitIdLength the length of the SHA1 substring to use as qualifier, valid values [8, 40], default 8
     * @return itself to chain settings
     * @throws IllegalArgumentException in case the length is not in the range [8,40]
     */
    public GitVersionCalculator setGitCommitIdLength(int gitCommitIdLength) {
        if (gitCommitIdLength < 8 || gitCommitIdLength > 40) {
            throw new IllegalStateException("GitCommitIdLength must be between 8 & 40");
        }
        this.gitCommitIdLength = gitCommitIdLength;
        return this;
    }

    /**
     * Activates the maven like mode.
     * 
     * @param mavenLike true to activate maven like mode
     * @return itself to chain settings
     * @deprecated since 0.7.0, use {@link #setStrategy(Strategies)} instead
     */
    public GitVersionCalculator setMavenLike(boolean mavenLike) {
        this.mavenLike = mavenLike;
        return this;
    }
    
    /**
     * Defines a regexp search pattern that will match tags identifying a version.
     * The provided regexp MUST contains at least one selector group that will represent the version extracted from the tag. 
     * @param pattern a non null string representing a java regexp pattern able to match tag containing versions
     * @return itself to chain settings
     * @throws java.util.regex.PatternSyntaxException if the given string cannot be parsed 
     *      as a correct {@link java.util.regex.Pattern} object
     */
    public GitVersionCalculator setFindTagVersionPattern(String pattern) {
        this.findTagVersionPattern = Pattern.compile(pattern);
        return this;
    }

    @Override
    public Optional<String> meta(Metadatas meta) {
        if (metadatas == null) {
            getVersion();
        }
        return metadatas.meta(meta);
    }

    /**
     * Defines the strategy to use.
     * @param s the non null strategy to use as a {@link Strategies} enum value
     * @return itself to chain settings
     * @since 0.7.0
     */
    public GitVersionCalculator setStrategy(Strategies s) {
        this.versionStrategy = Objects.requireNonNull(s, "provided strategy cannot be null");
        return this;
    }

    /**
     * Defines the version pattern to use in {@link Strategies#PATTERN} mode when HEAD is on an annotated tag
     * @param pattern the pattern to use for annotated tags
     * @return itself to chain settings
     * @since 0.7.0
     */
    public GitVersionCalculator setTagVersionPattern(String pattern) {
        this.tagVersionPattern = pattern;
        return this;
    }

    /**
     * Defines the version pattern to use in {@link Strategies#PATTERN} mode for normal situation (i.e. not on a tag)
     * @param pattern the pattern to use
     * @return itself to chain settings
     * @since 0.7.0
     */
    public GitVersionCalculator setVersionPattern(String pattern) {
        this.versionPattern = pattern;
        return this;
    }
}
