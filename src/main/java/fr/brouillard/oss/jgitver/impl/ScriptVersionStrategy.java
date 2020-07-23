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

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.petitparser.context.Result;

import org.apache.maven.shared.scriptinterpreter.BeanShellScriptInterpreter;
import org.apache.maven.shared.scriptinterpreter.GroovyScriptInterpreter;
import org.apache.maven.shared.scriptinterpreter.ScriptInterpreter;

import fr.brouillard.oss.jgitver.Features;
import fr.brouillard.oss.jgitver.ScriptType;
import fr.brouillard.oss.jgitver.Version;

import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;
import fr.brouillard.oss.jgitver.metadata.TagType;

/**
 * Executes the given script (according its {@link ScriptType}, 
 * default being {@link ScriptType#GROOVY}) 
 * and use the script output as version.
 *
 * This output must be in CSV format using ';' separator, 
 * with at least the 3 first columns, corresponding to integer values for
 * major, minor and patch (e.g. <tt>1;2;3</tt>).
 *
 * The script has access to <a href="https://jgitver.github.io/#_meta_fields">metadata</a> (e.g. In BeanShell for the SHA1 <tt>metadata.GIT_SHA1_8</tt>).
 *
 * The script also has access to the environment variables 
 * and system properties, respectively thanks to <tt>env</tt> 
 * (e.g. <tt>env.HOME</tt>) and <tt>sys</tt> (e.g. <tt>sys.user.home</tt>).
 *
 * If no script is passed as parameter then the default one is used,
 * with the same behavior as the <tt>PATTERN</tt> strategy.
 */
public class ScriptVersionStrategy extends VersionStrategy<ScriptVersionStrategy> {
    private ScriptType scriptType = ScriptType.GROOVY;
    private String script = "";

    /**
     * Default constructor.
     *
     * @param vnc        the configuration to use
     * @param repository the git repository
     * @param git        a git helper object built from the repository
     * @param registrar  a storage for found/calculated metadata
     */
    public ScriptVersionStrategy(VersionNamingConfiguration vnc,
                                 Repository repository,
                                 Git git,
                                 MetadataRegistrar registrar) {

        super(vnc, repository, git, registrar);
    }

    @Override
    public Version build(Commit head, List<Commit> parents) throws VersionCalculationException {
        try {
            final Commit base = findVersionCommit(head, parents);
            final MetadataRegistrar registrar = getRegistrar();
            final MetadataProvider metaProvider =
                MetadataProvider.class.cast(registrar);

            final HashMap<String, Object> metaProps =
                new HashMap<String, Object>();

            EnumSet.allOf(Metadatas.class).
                forEach(key -> metaProvider.meta(key).
                        ifPresent(value -> metaProps.put(key.name(), value)));

            // Distance
            registrar.registerMetadata(Metadatas.COMMIT_DISTANCE,
                                       "" + base.getHeadDistance());

            metaProps.put(Metadatas.COMMIT_DISTANCE.name(),
                          base.getHeadDistance());

            if (Features.DISTANCE_TO_ROOT.isActive()) {
                final int dtr = GitUtils.
                    distanceToRoot(getRepository(), head.getGitObject());

                registrar.registerMetadata(Metadatas.COMMIT_DISTANCE_TO_ROOT, "" + dtr);

                metaProps.put(Metadatas.COMMIT_DISTANCE_TO_ROOT.name(), dtr);
            }

            // Branch related
            final String branch = getRepository().getBranch();

            metaProps.put(Metadatas.BRANCH_NAME.name(), branch);

            registrar.registerMetadata(Metadatas.BRANCH_NAME, branch);

            getVersionNamingConfiguration().branchQualifier(branch).
                ifPresent(qualifier -> {
                        metaProps.put(Metadatas.QUALIFIED_BRANCH_NAME.name(),
                                      qualifier);

                        registrar.registerMetadata(Metadatas.QUALIFIED_BRANCH_NAME, qualifier);
                    });

            GitUtils.providedBranchName().ifPresent(externalName -> {
                    metaProps.put(Metadatas.QUALIFIED_BRANCH_NAME.name(),
                                  externalName);

                    registrar.registerMetadata(Metadatas.QUALIFIED_BRANCH_NAME,
                                               externalName);
                
                    metaProps.put("PROVIDED_BRANCH_NAME", externalName);
                });

            try (final RevWalk walk = new RevWalk(getRepository())) {
                final RevCommit rc = walk.parseCommit(head.getGitObject());

                metaProps.put(Metadatas.COMMIT_TIMESTAMP.name(), GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant()));
            }

            // Extra convenient metadata
            metaProps.put("DETACHED_HEAD",
                          GitUtils.isDetachedHead(getRepository()));

            metaProps.put("BASE_COMMIT_ON_HEAD",
                          isBaseCommitOnHead(head, base));

            // Tag related metadata
            final Ref tagToUse = findTagToUse(head, base);

            final Version baseVersion;

            if (tagToUse != null) {
                final String tagName = GitUtils.tagNameFromRef(tagToUse);
                final TagType tagType = computeTagType(tagToUse, maxVersionTag(base.getAnnotatedTags()).orElse(null));

                baseVersion = versionFromTag(tagToUse);

                metaProps.put(Metadatas.BASE_TAG_TYPE.name(), tagType.name());

                // Required by provideNextVersionsMetadatas
                registrar.registerMetadata(Metadatas.BASE_TAG_TYPE,
                                           tagType.name());
                
                metaProps.put(Metadatas.BASE_TAG.name(), tagName);
            } else {
                baseVersion = Version.DEFAULT_VERSION;
            }

            metaProps.put(Metadatas.BASE_VERSION.name(), baseVersion);

            // Required by provideNextVersionsMetadatas
            registrar.registerMetadata(Metadatas.BASE_VERSION,
                                       baseVersion.toString());

            metaProps.put(Metadatas.CURRENT_VERSION_MAJOR.name(),
                          baseVersion.getMajor());

            metaProps.put(Metadatas.CURRENT_VERSION_MINOR.name(),
                          baseVersion.getMinor());

            metaProps.put(Metadatas.CURRENT_VERSION_PATCH.name(),
                          baseVersion.getPatch());

            metaProps.put("ANNOTATED", GitUtils.isAnnotated(tagToUse));

            // ---

            final HashMap<String, Object> globalVariables =
                new HashMap<String, Object>();

            globalVariables.put("env", System.getenv());
            globalVariables.put("sys", System.getProperties());

            globalVariables.put("metadata",
                                Collections.unmodifiableMap(metaProps));

            final ScriptInterpreter interpreter;


            if (script == null || script.length() == 0) {
                interpreter = new GroovyScriptInterpreter();

                final String sep = System.lineSeparator();

                try (final InputStream in = getClass().getResourceAsStream("/META-INF/jgitver-version-script.groovy");
                     final BufferedReader br =
                         new BufferedReader(new InputStreamReader(in))) {

                    final StringBuilder buf = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        buf.append(line + sep);
                    }

                    script = buf.toString();
                }
            } else {
                interpreter = (scriptType == ScriptType.BEAN_SHELL)
                    ? new BeanShellScriptInterpreter()
                    : new GroovyScriptInterpreter();
            }

            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            interpreter.evaluateScript(script,
                                       Collections.emptyList(),
                                       globalVariables,
                                       new java.io.PrintStream(output));

            final String csvResult = output.toString();

            final String[] verComponents = csvResult.split(";");

            if (verComponents.length < 3) {
                throw new VersionCalculationException("invalid script result; expect at least 3 CSV columns (<major>;<minor>;<patch>): " + csvResult);
            }

            // ---

            final int major;
            final int minor;
            final int patch;

            try {
                major = Integer.parseInt(verComponents[0]);
            } catch (Exception cause) {
                throw new VersionCalculationException("invalid major", cause);
            }

            try {
                minor = Integer.parseInt(verComponents[1]);
            } catch (Exception cause) {
                throw new VersionCalculationException("invalid minor", cause);
            }

            try {
                patch = Integer.parseInt(verComponents[2]);
            } catch (Exception cause) {
                throw new VersionCalculationException("invalid patch", cause);
            }

            // ---

            final int qualifierCount = verComponents.length - 3;
            final String[] qualifiers;

            if (qualifierCount == 0) {
                qualifiers = new String[0];
            } else {
                qualifiers = new String[qualifierCount];

                System.arraycopy(verComponents, 3,
                                 qualifiers, 0, qualifierCount);

            }

            return new Version(major, minor, patch, qualifiers);
        } catch (Exception ex) {
            throw new VersionCalculationException("cannot compute version", ex);
        }
    }

    /**
     * Set the type of the script (default: {@link ScriptType#GROOVY}).
     * @param scriptType the script type
     * @return itself for chaining
     */
    public ScriptVersionStrategy setScriptType(ScriptType scriptType) {
        return runAndGetSelf(() -> this.scriptType = scriptType);
    }

    /**
     * Set the script to be interpreted.
     * @param script the script content
     * @return itself for chaining
     */
    public ScriptVersionStrategy setScript(String script) {
        return runAndGetSelf(() -> this.script = script);
    }
}
