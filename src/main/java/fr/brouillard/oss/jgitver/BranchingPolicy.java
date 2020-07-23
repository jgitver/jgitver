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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Optional.*;

public class BranchingPolicy {
    private final Pattern recognitionPattern;
    private final List<Function<String, String>> transformations;
    private final Optional<String> versionPattern;

    /**
     * Default branching policy that sanitizes branch names as per jgtiver <=
     * 0.2.0-alpha1;
     */
    public static BranchingPolicy DEFAULT_FALLBACK = new BranchingPolicy("(.*)",
            Arrays.asList(
                    BranchNameTransformations.REPLACE_UNEXPECTED_CHARS_UNDERSCORE.name(), 
                    BranchNameTransformations.LOWERCASE_EN.name())
            );

    /**
     * Builds a BranchingPolicy object using the given pattern as a recognition {@link Pattern}.
     * Some default branches transformations will be applied:
     * <ul>
     *     <li>{@link BranchNameTransformations#REPLACE_UNEXPECTED_CHARS_UNDERSCORE}</li>
     *     <li>{@link BranchNameTransformations#LOWERCASE_EN}</li>
     * </ul>
     * @param recognitionPattern the string representing a {@link Pattern} used to isolate the name to use from the branch.
     *                           The pattern MUST contains at least one capture group.
     */
    public BranchingPolicy(String recognitionPattern) {
        this(recognitionPattern, (String) null);
    }

    /**
     * Builds a BranchingPolicy object using the given pattern as a recognition {@link Pattern}.
     * Some default branches transformations will be applied:
     * <ul>
     *     <li>{@link BranchNameTransformations#REPLACE_UNEXPECTED_CHARS_UNDERSCORE}</li>
     *     <li>{@link BranchNameTransformations#LOWERCASE_EN}</li>
     * </ul>
     * @param recognitionPattern the string representing a {@link Pattern} used to isolate the name to use from the branch.
     *                           The pattern MUST contains at least one capture group.
     * @param versionPattern optional version pattern
     */
    public BranchingPolicy(String recognitionPattern, String versionPattern) {
        this(recognitionPattern, Arrays.asList(
                BranchNameTransformations.REPLACE_UNEXPECTED_CHARS_UNDERSCORE.name(),
                BranchNameTransformations.LOWERCASE_EN.name()), versionPattern
        );
    }

    /**
     * Builds a BranchingPolicy object using the given pattern as a recognition {@link Pattern}and the given branches transformations
     * @param recognitionPattern the string representing a {@link Pattern} used to isolate the name to use from the branch.
     *                           The pattern MUST contains at least one capture group.
     * @param transformationsNames the non null list of transformations to apply to the extracted name
     */
    public BranchingPolicy(String recognitionPattern, List<String> transformationsNames) {
        this(recognitionPattern, transformationsNames, null);
    }

    /**
     * Builds a BranchingPolicy object using the given pattern as a recognition {@link Pattern}and the given branches transformations
     * @param recognitionPattern the string representing a {@link Pattern} used to isolate the name to use from the branch.
     *                           The pattern MUST contains at least one capture group.
     * @param transformationsNames the non null list of transformations to apply to the extracted name
     * @param versionPattern optional version pattern
     */
    public BranchingPolicy(String recognitionPattern, List<String> transformationsNames, String versionPattern) {
        this.recognitionPattern = Pattern.compile(recognitionPattern);
        this.transformations = transformationsNames.stream().map(BranchNameTransformations::valueOf).collect(Collectors.toList());
        this.versionPattern = ofNullable(versionPattern);
    }

    /**
     * Builds a BranchingPolicy object that matches the given name and apply the given transformations.
     * @param name the exact name to match against the git branch
     * @param transformations the non null list of transformations to apply to the extracted name
     */
    public static BranchingPolicy fixedBranchName(String name, List<String> transformations) {
        BranchingPolicy bp = new BranchingPolicy(rememberWhole(Pattern.quote(name)), transformations);
        return bp;
    }

    /**
     * Builds a BranchingPolicy object that matches the given name and apply the default transformations.
     * <ul>
     *     <li>{@link BranchNameTransformations#REPLACE_UNEXPECTED_CHARS_UNDERSCORE}</li>
     *     <li>{@link BranchNameTransformations#LOWERCASE_EN}</li>
     * </ul>
     * @param name the exact name to match against the git branch
     */
    public static BranchingPolicy fixedBranchName(String name) {
        BranchingPolicy bp = new BranchingPolicy(rememberWhole(Pattern.quote(name)));
        return bp;
    }

    /**
     * Builds a BranchingPolicy object that matches the given name for which the recognized branch name has not to be used, ie ignored.
     * @param name the exact name to match against the git branch to ignore
     */
    public static BranchingPolicy ignoreBranchName(String name) {
        BranchingPolicy bp = new BranchingPolicy(rememberWhole(Pattern.quote(name)),
                Collections.singletonList(BranchNameTransformations.IGNORE.name()));
        return bp;
    }

    private static String rememberWhole(String pattern) {
        return "(" + pattern + ")";
    }

    public boolean appliesOn(String branch) {
        return recogniseBranch(branch).isPresent();
    }

    private Optional<String> recogniseBranch(String branch) {
        if (branch != null) {
            Matcher matcher = recognitionPattern.matcher(branch);
            if (matcher.matches()) {
                int i = 0;
                while (++i <= matcher.groupCount()) {
                    String name = matcher.group(i);
                    if (name != null && !name.isEmpty()) {
                        return Optional.of(name);
                    }
                }
            }
        }

        return empty();
    }

    public Optional<String> qualifier(String branch) {
        return recogniseBranch(branch)
                .map(extractedName -> transformations.stream().reduce(extractedName, (bName, f) -> f.apply(bName), (b1, b2) -> b2));
    }

    public enum BranchNameTransformations implements Function<String, String> {
        IDENTITY, REMOVE_UNEXPECTED_CHARS {
            @Override
            public String apply(String branch) {
                if (branch != null) {
                    return branch.replaceAll("[\\s\\-#/\\\\]+", "");
                }
                return null;
            }
        },
        REPLACE_UNEXPECTED_CHARS_UNDERSCORE {
            @Override
            public String apply(String branch) {
                if (branch != null) {
                    return branch.replaceAll("[\\s\\-#/\\\\]+", "_");
                }
                return null;
            }
        },
        UPPERCASE {
            @Override
            public String apply(String branch) {
                if (branch != null) {
                    return branch.toUpperCase();
                }
                return null;
            }
        },
        LOWERCASE {
            @Override
            public String apply(String branch) {
                if (branch != null) {
                    return branch.toLowerCase();
                }
                return null;
            }
        },
        UPPERCASE_EN {
            @Override
            public String apply(String branch) {
                if (branch != null) {
                    return branch.toUpperCase(Locale.ENGLISH);
                }
                return null;
            }
        },
        LOWERCASE_EN {
            @Override
            public String apply(String branch) {
                if (branch != null) {
                    return branch.toLowerCase(Locale.ENGLISH);
                }
                return null;
            }
        },
        IGNORE {
            @Override
            public String apply(String branch) {
                return null;
            }
        };

        @Override
        public String apply(String branch) {
            return branch;
        }
    }

    public Optional<String> getVersionPattern() {
        return versionPattern;
    }
}
