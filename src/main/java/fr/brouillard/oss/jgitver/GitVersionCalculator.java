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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
import fr.brouillard.oss.jgitver.impl.VersionNamingConfiguration;
import fr.brouillard.oss.jgitver.impl.VersionStrategy;
import fr.brouillard.oss.jgitver.impl.VersionStrategy.StrategySearchMode;
import fr.brouillard.oss.jgitver.metadata.MetadataHolder;
import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

public class GitVersionCalculator implements AutoCloseable, MetadataProvider {
    private MetadataHolder metadatas;
    private Repository repository;
    private boolean mavenLike = false;
    private boolean autoIncrementPatch = false;
    private boolean useDistance = true;
    private boolean useGitCommitId = false;
    private boolean useDirty = false;
    private int gitCommitIdLength = 8;
    private List<BranchingPolicy> qualifierBranchingPolicies;
    private boolean useDefaultBranchingPolicy = true;

    private String findTagVersionPattern = "v?([0-9]+(?:\\.[0-9]+){0,2}(?:-[a-zA-Z0-9\\-_]+)?)";
    private String extractTagVersionPattern = "$1";
    private File gitRepositoryLocation;

    private final SimpleDateFormat dtfmt;

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

            VersionNamingConfiguration vnc = new VersionNamingConfiguration(findTagVersionPattern, extractTagVersionPattern, policiesToUse.toArray(new BranchingPolicy[policiesToUse.size()]));

            if (mavenLike) {
                strategy = new MavenVersionStrategy(vnc, repository, git, metadatas);
            } else {
                ConfigurableVersionStrategy cvs = new ConfigurableVersionStrategy(vnc, repository, git, metadatas);
                cvs.setAutoIncrementPatch(autoIncrementPatch);
                cvs.setUseDistance(useDistance);
                cvs.setUseDirty(useDirty);
                cvs.setUseGitCommitId(useGitCommitId);
                cvs.setGitCommitIdLength(gitCommitIdLength);
                strategy = cvs;
            }

            return buildVersion(git, strategy);
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
            metadatas.registerMetadataTags(Metadatas.ALL_ANNOTATED_TAGS,
                    allTags.stream().filter(GitUtils::isAnnotated));
            metadatas.registerMetadataTags(Metadatas.ALL_LIGHTWEIGHT_TAGS,
                    allTags.stream().filter(as(GitUtils::isAnnotated).negate()));

            List<Ref> allVersionTags = allTags.stream().filter(strategy::considerTagAsAVersionOne)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<Ref> normals = allVersionTags.stream().filter(GitUtils::isAnnotated).collect(Collectors.toList());
            List<Ref> lights = allVersionTags.stream().filter(as(GitUtils::isAnnotated).negate())
                    .collect(Collectors.toList());

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

            metadatas.registerMetadata(Metadatas.GIT_SHA1_FULL, rootId.getName());
            metadatas.registerMetadata(Metadatas.GIT_SHA1_8, rootId.getName().substring(0, 8));
            
            Commit head = new Commit(rootId, 0, tagsOf(normals, rootId), tagsOf(lights, rootId));
            List<Commit> commits = new LinkedList<>();

            try (RevWalk revWalk = new RevWalk(repository)) {
                revWalk.markStart(revWalk.parseCommit(rootId));

                int depth = 0;
                ObjectId id = null;
                for (RevCommit rc : revWalk) {
                    id = rc.getId();

                    List<Ref> annotatedCommitTags = tagsOf(normals, id);
                    List<Ref> lightCommitTags = tagsOf(lights, id);

                    if (annotatedCommitTags.size() > 0 || lightCommitTags.size() > 0) {
                        // we found a commit with version tags
                        Commit c = new Commit(id, depth, annotatedCommitTags, lightCommitTags);
                        commits.add(c);

                        // shall we stop searching for commits
                        if (StrategySearchMode.STOP_AT_FIRST.equals(strategy.searchMode())) {
                            break; // let's stop
                        } else if (depth >= strategy.searchDepthLimit()) {
                            break; // let's stop
                        }
                    }

                    depth++;
                }

                // handle the case where we reached the first commit without finding anything
                if (commits.size() == 0) {
                    commits.add(new Commit(id, depth - 1, Collections.emptyList(), Collections.emptyList()));
                }
            }

            Version version = strategy.build(head, commits);
            metadatas.registerMetadata(Metadatas.CALCULATED_VERSION, version.toString());
            return version;
        } catch (Exception ex) {
            throw new IllegalStateException("failure calculating version", ex);
        }
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
     * This method overrides the usage of {@link #setQualifierBranchingPolicies(List)} & {@link #setQualifierBranchingPolicies(BranchingPolicy...)}.
     * 
     * @param nonQualifierBranches a comma separated list of branch name for which no branch name qualifier should be
     *        used, can be null and/or empty
     * @return itself to chain settings
     */
    public GitVersionCalculator setNonQualifierBranches(String nonQualifierBranches) {
        List<BranchingPolicy> branchPolicies = new LinkedList<>();
        
        if (nonQualifierBranches != null && !"".equals(nonQualifierBranches.trim())) {
            for (String branch : nonQualifierBranches.split(",")) {
                branchPolicies.add(BranchingPolicy.fixedBranchName(branch, Collections.singletonList(BranchNameTransformations.IGNORE.name())));
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
     * This method overrides the usage of {@link #setNonQualifierBranches(String)} & {@link #setQualifierBranchingPolicies(BranchingPolicy...)}.
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
     * When true, uses as last a {@link BranchingPolicy} that capture every branch and return a qualifier 
     * 
     * @param useGitCommitId if true, a qualifier with SHA1 git commit will be used, default true
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
     */
    public GitVersionCalculator setMavenLike(boolean mavenLike) {
        this.mavenLike = mavenLike;
        return this;
    }

    @Override
    public Optional<String> meta(Metadatas meta) {
        if (metadatas == null) {
            getVersion();
        }
        return metadatas.meta(meta);
    }
}
