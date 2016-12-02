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

public class BranchingPolicy {
    private final Pattern recognitionPattern;
    private List<Function<String, String>> transformations;

    /**
     * Default branching policy that sanitizes branch names as per jgtiver <=
     * 0.2.0-alpha1;
     */
    public static BranchingPolicy DEFAULT_FALLBACK = new BranchingPolicy("(.*)",
            Arrays.asList(
                    BranchNameTransformations.REPLACE_UNEXPECTED_CHARS_UNDERSCORE.name(), 
                    BranchNameTransformations.LOWERCASE_EN.name())
            );

    public BranchingPolicy(String recognitionPattern) {
        this(recognitionPattern, Arrays.asList(
                BranchNameTransformations.REPLACE_UNEXPECTED_CHARS_UNDERSCORE.name(),
                BranchNameTransformations.LOWERCASE_EN.name())
        );
    }

    public BranchingPolicy(String recognitionPattern, List<String> transformationsNames) {
        this.recognitionPattern = Pattern.compile(recognitionPattern);
        transformations = transformationsNames.stream().map(BranchNameTransformations::valueOf).collect(Collectors.toList());
    }

    public static BranchingPolicy fixedBranchName(String name, List<String> transformations) {
        BranchingPolicy bp = new BranchingPolicy(rememberWhole(Pattern.quote(name)), transformations);
        return bp;
    }

    public static BranchingPolicy fixedBranchName(String name) {
        BranchingPolicy bp = new BranchingPolicy(rememberWhole(Pattern.quote(name)));
        return bp;
    }

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
            if (matcher.matches() && matcher.groupCount() >= 1) {
                return Optional.of(matcher.group(1));
            }
        }

        return Optional.empty();
    }

    public Optional<String> qualifier(String branch) {
        return recogniseBranch(branch)
                .map(extractedName -> transformations.stream().reduce(extractedName, (bName, f) -> f.apply(bName), (b1, b2) -> b2));
    }

    public static enum BranchNameTransformations implements Function<String, String> {
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
}
