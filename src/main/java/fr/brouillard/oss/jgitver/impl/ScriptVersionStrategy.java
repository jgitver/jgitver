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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.apache.maven.shared.scriptinterpreter.ProjectBeanShellScriptInterpreter;
import org.apache.maven.shared.scriptinterpreter.ProjectGroovyScriptInterpreter;
import org.apache.maven.shared.scriptinterpreter.ScriptRunner;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import org.apache.maven.shared.scriptinterpreter.ScriptInterpreter;

import fr.brouillard.oss.jgitver.Features;
import fr.brouillard.oss.jgitver.ScriptType;
import fr.brouillard.oss.jgitver.Version;

import fr.brouillard.oss.jgitver.metadata.MetadataProvider;
import fr.brouillard.oss.jgitver.metadata.MetadataRegistrar;
import fr.brouillard.oss.jgitver.metadata.Metadatas;

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
            Commit base = findVersionCommit(head, parents);
            Ref tagToUse = findTagToUse(head, base);
            final Version baseVersion = getBaseVersionAndRegisterMetadata(base,tagToUse);
            final MetadataRegistrar registrar = getRegistrar();
            final MetadataProvider metaProvider = MetadataProvider.class.cast(registrar);

            final HashMap<String, Object> metaProps = new HashMap<String, Object>();

            registrar.registerMetadata(Metadatas.COMMIT_DISTANCE,
                                       "" + base.getHeadDistance());

            if (Features.DISTANCE_TO_ROOT.isActive()) {
                final int dtr = GitUtils.
                    distanceToRoot(getRepository(), head.getGitObject());

                registrar.registerMetadata(Metadatas.COMMIT_DISTANCE_TO_ROOT, "" + dtr);
            }

            // Branch related
            final String branch = getRepository().getBranch();

            registrar.registerMetadata(Metadatas.BRANCH_NAME, branch);

            getVersionNamingConfiguration().branchQualifier(branch).
                ifPresent(qualifier -> {
                        registrar.registerMetadata(Metadatas.QUALIFIED_BRANCH_NAME, qualifier);
                    });

            GitUtils.providedBranchName().ifPresent(externalName -> {
                registrar.registerMetadata(Metadatas.QUALIFIED_BRANCH_NAME, externalName);
                registrar.registerMetadata(Metadatas.PROVIDED_BRANCH_NAME, externalName);
            });

            try (final RevWalk walk = new RevWalk(getRepository())) {
                final RevCommit rc = walk.parseCommit(head.getGitObject());
                getRegistrar().registerMetadata(Metadatas.COMMIT_TIMESTAMP, GitUtils.getTimestamp(rc.getAuthorIdent().getWhen().toInstant()));
                String isoCommitTimestamp = GitUtils.getIsoTimestamp(rc.getAuthorIdent().getWhen().toInstant());
                getRegistrar().registerMetadata(Metadatas.COMMIT_ISO_TIMESTAMP, isoCommitTimestamp);
            }

            registrar.registerMetadata(Metadatas.BASE_COMMIT_ON_HEAD, "" + isBaseCommitOnHead(head, base));
            
            EnumSet.allOf(Metadatas.class).
                forEach(key -> metaProvider.meta(key).
                    ifPresent(value -> metaProps.put(key.name(), metaFunctor(key).apply(value))));

            // ---

            final HashMap<String, Object> globalVariables =
                new HashMap<String, Object>();

            globalVariables.put("env", System.getenv());
            globalVariables.put("sys", System.getProperties());

            globalVariables.put("metadata",
                                Collections.unmodifiableMap(metaProps));

            final ScriptInterpreter interpreter;
            final ScriptRunner runner;

            if (script == null || script.length() == 0) {
                interpreter = new ProjectGroovyScriptInterpreter();
                script = defaultGroovyScript();
            } else {
                interpreter = (ScriptType.BEAN_SHELL.equals(scriptType))
                    ? new ProjectBeanShellScriptInterpreter()
                    : new ProjectGroovyScriptInterpreter();
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

            final int major = extractPartialVersion(verComponents[0], "major");
            final int minor = extractPartialVersion(verComponents[1], "minor");
            final int patch = extractPartialVersion(verComponents[2], "patch");

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
    
    private String defaultGroovyScript() {
        final String sep = System.lineSeparator();

        try (final InputStream in = getClass().getResourceAsStream("/META-INF/jgitver-version-script.groovy");
             final BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            final StringBuilder buf = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                buf.append(line + sep);
            }

            return buf.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    private int extractPartialVersion(String partialValue, String part) throws VersionCalculationException {
        try {
            return Integer.parseInt(partialValue);
        } catch (Exception cause) {
            throw new VersionCalculationException("invalid " + part + " value: " + partialValue, cause);
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

    private static Function<String, String> IDENTITY = s -> s;
    private static Function<String, Boolean> TO_BOOLEAN = s -> {
        if (s == null || s.length() == 0) {
            return null;
        }
        return Boolean.valueOf(s);
    };
    private static Function<String, Integer> TO_INTEGER = s -> {
        if (s == null || s.length() == 0) {
            return null;
        }
        return Integer.valueOf(s);
    };
    private static Function<String, Version> TO_VERSION = Version::parse;
    
    private static Function<String, ? extends Object> metaFunctor(Metadatas meta) {
        switch (meta) {
            case CALCULATED_VERSION:
            case BASE_VERSION:
            case NEXT_MAJOR_VERSION:
            case NEXT_MINOR_VERSION:
            case NEXT_PATCH_VERSION:
                return TO_VERSION;
            case CURRENT_VERSION_MAJOR:
            case CURRENT_VERSION_MINOR:
            case CURRENT_VERSION_PATCH:
            case COMMIT_DISTANCE:
            case COMMIT_DISTANCE_TO_ROOT:
                return TO_INTEGER;
            case DIRTY:
            case ANNOTATED:
            case DETACHED_HEAD:
            case BASE_COMMIT_ON_HEAD:
                return TO_BOOLEAN;
            default: return IDENTITY;
        }
    }
}
