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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.google.common.io.Files;

@SuppressWarnings({"checkstyle:nonemptyatclausedescription","checkstyle:javadocparagraph", "checkstyle:summaryjavadoc"})
public class Scenarios {
    /**
     * Builds the following repository
     * <pre>
     * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
     * * 80eee6d - (18 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
     * * 98358d0 - (18 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
     * * 00a993e - (18 seconds ago) content C - Matthieu Brouillard
     * * 183ccc6 - (18 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
     * * b048402 - (18 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s1_linear_with_only_annotated_tags() {
        return Builders.s1_linear_with_only_annotated_tags().getScenario();
    }

    /**
     * Builds the following repository, tag 1.1.0 is a lightweight one, others are annotated ones
     * <pre>
     * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
     * * 47eb212 - (60 seconds ago) content E - Matthieu Brouillard  (HEAD -> master)
     * * 01ee9e7 - (60 seconds ago) content D - Matthieu Brouillard  (tag: 2.0.0)
     * * 84afe52 - (60 seconds ago) content C - Matthieu Brouillard
     * * 6b4a7d2 - (60 seconds ago) content B - Matthieu Brouillard  (tag: 1.1.0, tag: 1.0.0)
     * * 368516a - (60 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s2_linear_with_both_tags() {
        return Builders.s2_linear_with_both_tags().getScenario();
    }

    /**
     * Builds the following repository, tags 1.1.0-SNAPSHOT &amp; 3.0.0-SNAPSHOT are lightweight ones, others are annotated ones
     * <pre>
     * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
     * * 64a5bf6 - (60 seconds ago) content F - Matthieu Brouillard  (HEAD -> master)
     * * 47eb212 - (60 seconds ago) content E - Matthieu Brouillard
     * * 01ee9e7 - (60 seconds ago) content D - Matthieu Brouillard  (tag: 3.0.0-SNAPSHOT, 2.0.0)
     * * 84afe52 - (60 seconds ago) content C - Matthieu Brouillard
     * * 6b4a7d2 - (60 seconds ago) content B - Matthieu Brouillard  (tag: 1.1.0-SNAPSHOT, tag: 1.0.0)
     * * 368516a - (60 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s3_linear_with_snapshots_light_tags() {
        return Builders.s3_linear_with_snapshots_light_tags().getScenario();
    }

    /**
     * Builds the following repository
     * <pre>
     * $ git lg
     * * 7454c23 - (23 seconds ago) content G - Matthieu Brouillard (issue-10)
     * * 17716f2 - (23 seconds ago) content F - Matthieu Brouillard
     * | * 6769dbc - (23 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
     * | * e5c7b86 - (23 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
     * |/
     * * 4640726 - (23 seconds ago) content C - Matthieu Brouillard
     * * e6231b0 - (23 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
     * * b4e3196 - (23 seconds ago) content A - Matthieu Brouillard
     </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s4_linear_with_only_annotated_tags_and_branch() {
        return Builders.s4_linear_with_only_annotated_tags_and_branch().getScenario();
    }

    /**
     * Builds the following repository
     * <pre>
     * $ git lg
     * * 1b48dc9 - (19 seconds ago) content D - Matthieu Brouillard (dev)
     * | * 1e563e6 - (19 seconds ago) content C - Matthieu Brouillard (int)
     * |/
     * | * 5a7d916 - (19 seconds ago) content B - Matthieu Brouillard (HEAD -> master)
     * |/
     * * 338e4e2 - (19 seconds ago) content A - Matthieu Brouillard (tag: 1.0.0)
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s5_several_branches() {
        return Builders.s5_several_branches().getScenario();
    }

    /**
     * Builds the following repository
     * <pre>
     * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
     * * 2edd5f0 - (23 seconds ago) content E - Matthieu Brouillard  (HEAD -> master)
     * * 1fd4fc5 - (23 seconds ago) content D - Matthieu Brouillard  (tag: dummy)
     * * 8b7702c - (23 seconds ago) content C - Matthieu Brouillard  (tag: a3.0)
     * * bf740e5 - (23 seconds ago) content B - Matthieu Brouillard  (tag: v2.0)
     * * 7166362 - (23 seconds ago) content A - Matthieu Brouillard  (tag: 1.0)
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s6_matching_and_non_matching_versions_tags() {
        return Builders.s6_matching_and_non_matching_versions_tags().getScenario();
    }

    /**
     * Builds the following repository.
     * <pre>
     * $ git lg
     * * ba58bfe - (4 minutes ago) content F - Matthieu Brouillard (issue-10)
     * | * f79c238 - (4 minutes ago) content E - Matthieu Brouillard (HEAD -> master)
     * | * c8a6fb0 - (4 minutes ago) content D - Matthieu Brouillard (tag: 1.1.0)
     * |/
     * * 2a5bc65 - (4 minutes ago) content C - Matthieu Brouillard
     * * 3466319 - (4 minutes ago) content B - Matthieu Brouillard (tag: 1.0.0)
     * * d43eee1 - (4 minutes ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s8_main_and_branch_with_intermediate_light_tag() {
        return Builders.s8_main_and_branch_with_intermediate_light_tag().getScenario();
    }

    /**
     * Builds an empty git repository.
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s9_empty_repository() {
        return Builders.s9_empty_repository().getScenario();
    }

    /**
     * Builds a one commit git repository.
     * <pre>
     * $ git lg
     * * ee22e69 - (26 seconds ago) content A - Matthieu Brouillard (HEAD -> master)
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s10_one_commit_no_tag_repository() {
        return Builders.s10_one_commit_no_tag_repository().getScenario();
    }

    /**
     * Builds a linear repository with no tags.
     * <pre>
     * $ git lg
     * * abeca86 - (22 seconds ago) content C - Matthieu Brouillard (HEAD -> master)
     * * 877b5e1 - (22 seconds ago) content B - Matthieu Brouillard
     * * c487e67 - (22 seconds ago) content A - Matthieu Brouillard
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s11_linear_no_tag_repository() {
        return Builders.s11_linear_no_tag_repository().getScenario();
    }

    /**
     * Builds a linear repository with RC tags.
     * <pre>
     * $ git lg
     * * 3c6bad0 - (13 seconds ago) content G - Matthieu Brouillard (HEAD -> master, tag: v2.0.0, tag: 1.0.0)
     * * 54662c0 - (13 seconds ago) content F - Matthieu Brouillard
     * * 1374ed9 - (13 seconds ago) content E - Matthieu Brouillard (tag: 1.0.0-rc02)
     * * a35958a - (13 seconds ago) content D - Matthieu Brouillard
     * * e892883 - (13 seconds ago) content C - Matthieu Brouillard (tag: 1.0.0-rc01)
     * * 0954efd - (13 seconds ago) content B - Matthieu Brouillard
     * * 7510ee8 - (13 seconds ago) content A - Matthieu Brouillard (tag: v1.0.0)
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s12_linear_with_RC_tags() {
        return Builders.s12_linear_with_RC_tags().getScenario();
    }

    /**
     * Builds a repository with branches name like gitflow.
     * <pre>
     * {@code
     * $ git lg
     * * * * 8c102d4 - (develop) content H (33 seconds ago) <Matthieu Brouillard>
     * * * *| * e42d934 - (feature/add-sso) content G (33 seconds ago) <Matthieu Brouillard>
     * *|/
     * *| * c426c65 - (HEAD, tag: 3.0.0, master) content D (33 seconds ago) <Matthieu Brouillard>
     * *| | * d8893cc - (release/1.x) content E (33 seconds ago) <Matthieu Brouillard>
     * *| |/
     * *|/|
     * *| | * 29d7fe2 - (release/2.x) content F (33 seconds ago) <Matthieu Brouillard>
     * *| |/
     * *| * fdbe434 - (tag: v2.0.0) content C (33 seconds ago) <Matthieu Brouillard>
     * *|/
     * * ef278a5 - content B (33 seconds ago) <Matthieu Brouillard>
     * * fb1f2d9 - (tag: 1.0.0) content A (33 seconds ago) <Matthieu Brouillard>
     * }
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s13_gitflow() {
        return Builders.s13_gitflow().getScenario();
    }

    /**
     * Builds repository witch merges H
     * <pre>
     * *   eed0ba5 (HEAD -> master) H :: merge G into E
     * |\
     * | * 5d514bf (tag: 1.0.1, hotfix) content G
     * | * 4c8d8d2 content F
     * * | a8d85b2 content E
     * * | cf49743 (tag: 1.1.1, tag: 1.1.0) content D
     * * | 8065bb5 content C
     * |/
     * * 96da53b (tag: 1.0.0, tag: 0.9.1, tag: 0.9.0) content B
     * * 2cd5f31 (tag: 0.8.0) content A
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s14_with_merges() {
        return Builders.s14_with_merges().getScenario();
    }

    /**
     * Builds repository witch merges H
     * <pre>
     * *   eed0ba5 (HEAD -> master) H :: merge G into E
     * |\
     * | * 5d514bf (tag: v1.0.1, hotfix) content G
     * | * 4c8d8d2 content F
     * * | a8d85b2 content E
     * * | cf49743 (tag: v1.1.1, tag: v1.1.0) content D
     * * | 8065bb5 content C
     * |/
     * * 96da53b (tag: v1.0.0, tag: v0.9.1, tag: v0.9.0) content B
     * * 2cd5f31 (tag: v0.8.0) content A
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s14_with_merges_tag_prefixed() {
        return Builders.s14_with_merges_tag_prefixed().getScenario();
    }

    /**
     * Builds repository with complex two branches merge
     * <pre>
     * {@code
     * *   e8a122b (HEAD -> master) I :: merge G into H
     * |\
     * | * 7bae650 (tag: 3.0.0, b2) content G
     * * |   92c9286 H :: merge F into E
     * |\ \
     * | * | 5a75a7a (tag: 2.0.0, b1) content F
     * | |/
     * * | 7da2d41 content E
     * * | d3782b7 content D
     * * | 788dc0e (tag: 4.0.0) content C
     * |/
     * * fe4577e (tag: 1.0.0) content B
     * * 60a8e27 content A
     * }
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s15_complex_merges() {
        return Builders.s15_complex_merges().getScenario();
    }


    /**
     * Builds repository with complex two branches merge
     * <pre>
     * {@code
     * *   318c3c9 (HEAD -> master) J :: merge H into I
     * |\
     * | * a50dde7 (b2) content H
     * | * 4d841f6 content G
     * | * 368e6d8 content F
     * * |   ed0a74c I :: merge E into B
     * |\ \
     * | * | 0d42532 (b1) content E
     * | * | 4a2582a content D
     * | * | 5fcc09c content C
     * | |/
     * * | fe753dd content B
     * |/
     * * d74b251 (tag: 1.0.0) content A
     * }
     * </pre>
     * @return the scenario object corresponding to the above git repository
     */
    public static Scenario s16_merges_with_short_path() {
        return Builders.s16_merges_with_short_path().getScenario();
    }

    public static Scenario s17_feature_branches_with_shorter_path() {
        return Builders.s17_feature_branches_with_shorter_path().getScenario();
    }

    public static class Builders {
        /**
         * Builds the following repository
         * <pre>
         * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
         * * 80eee6d - (18 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
         * * 98358d0 - (18 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
         * * 00a993e - (18 seconds ago) content C - Matthieu Brouillard
         * * 183ccc6 - (18 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
         * * b048402 - (18 seconds ago) content A - Matthieu Brouillard
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s1_linear_with_only_annotated_tags() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .tag("1.0.0")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("2.0.0")
                    .commit("content", "E")
                    .master();
        }

        /**
         * Builds the following repository, tag 1.1.0 is a lightweight one, others are annotated ones
         * <pre>
         * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
         * * 47eb212 - (60 seconds ago) content E - Matthieu Brouillard  (HEAD -> master)
         * * 01ee9e7 - (60 seconds ago) content D - Matthieu Brouillard  (tag: 2.0.0)
         * * 84afe52 - (60 seconds ago) content C - Matthieu Brouillard
         * * 6b4a7d2 - (60 seconds ago) content B - Matthieu Brouillard  (tag: 1.1.0, tag: 1.0.0)
         * * 368516a - (60 seconds ago) content A - Matthieu Brouillard
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s2_linear_with_both_tags() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .tag("1.0.0")
                    .tagLight("1.1.0")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("2.0.0")
                    .commit("content", "E")
                    .master();
        }

        /**
         * Builds the following repository, tags 1.1.0-SNAPSHOT &amp; 3.0.0-SNAPSHOT are lightweight ones, others are annotated ones
         * <pre>
         * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
         * * 64a5bf6 - (60 seconds ago) content F - Matthieu Brouillard  (HEAD -> master)
         * * 47eb212 - (60 seconds ago) content E - Matthieu Brouillard
         * * 01ee9e7 - (60 seconds ago) content D - Matthieu Brouillard  (tag: 3.0.0-SNAPSHOT, 2.0.0)
         * * 84afe52 - (60 seconds ago) content C - Matthieu Brouillard
         * * 6b4a7d2 - (60 seconds ago) content B - Matthieu Brouillard  (tag: 1.1.0-SNAPSHOT, tag: 1.0.0)
         * * 368516a - (60 seconds ago) content A - Matthieu Brouillard
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s3_linear_with_snapshots_light_tags() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .tag("1.0.0")
                    .tagLight("1.1.0-SNAPSHOT")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("2.0.0")
                    .tagLight("3.0.0-SNAPSHOT")
                    .commit("content", "E")
                    .commit("content", "F")
                    .master();
        }

        /**
         * Builds the following repository
         * <pre>
         * $ git lg
         * * 7454c23 - (23 seconds ago) content G - Matthieu Brouillard (issue-10)
         * * 17716f2 - (23 seconds ago) content F - Matthieu Brouillard
         * | * 6769dbc - (23 seconds ago) content E - Matthieu Brouillard (HEAD -> master)
         * | * e5c7b86 - (23 seconds ago) content D - Matthieu Brouillard (tag: 2.0.0)
         * |/
         * * 4640726 - (23 seconds ago) content C - Matthieu Brouillard
         * * e6231b0 - (23 seconds ago) content B - Matthieu Brouillard (tag: 1.0.0)
         * * b4e3196 - (23 seconds ago) content A - Matthieu Brouillard
         </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s4_linear_with_only_annotated_tags_and_branch() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .tag("1.0.0")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("2.0.0")
                    .commit("content", "E")
                    .branchOnAppId("issue-10", "C")
                    .commit("content", "F")
                    .commit("content", "G")
                    .master();
        }

        /**
         * Builds the following repository
         * <pre>
         * $ git lg
         * * 1b48dc9 - (19 seconds ago) content D - Matthieu Brouillard (dev)
         * | * 1e563e6 - (19 seconds ago) content C - Matthieu Brouillard (int)
         * |/
         * | * 5a7d916 - (19 seconds ago) content B - Matthieu Brouillard (HEAD -> master)
         * |/
         * * 338e4e2 - (19 seconds ago) content A - Matthieu Brouillard (tag: 1.0.0)
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s5_several_branches() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("1.0.0")
                    .commit("content", "B")
                    .branchOnAppId("int", "A")
                    .commit("content", "C")
                    .branchOnAppId("dev", "A")
                    .commit("content", "D")
                    .master();
        }

        /**
         * Builds the following repository
         * <pre>
         * $ git log --graph --abbrev-commit --decorate --format=format:'%h - (%ar) %s - %an %d'
         * * 2edd5f0 - (23 seconds ago) content E - Matthieu Brouillard  (HEAD -> master)
         * * 1fd4fc5 - (23 seconds ago) content D - Matthieu Brouillard  (tag: dummy)
         * * 8b7702c - (23 seconds ago) content C - Matthieu Brouillard  (tag: a3.0)
         * * bf740e5 - (23 seconds ago) content B - Matthieu Brouillard  (tag: v2.0)
         * * 7166362 - (23 seconds ago) content A - Matthieu Brouillard  (tag: 1.0)
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s6_matching_and_non_matching_versions_tags() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("1.0")
                    .commit("content", "B")
                    .tag("v2.0")
                    .commit("content", "C")
                    .tag("a3.0")
                    .commit("content", "D")
                    .tag("dummy")
                    .commit("content", "E")
                    .master();
        }

        /**
         * Builds the following repository.
         * <pre>
         * $ git lg
         * * ba58bfe - (4 minutes ago) content F - Matthieu Brouillard (issue-10)
         * | * f79c238 - (4 minutes ago) content E - Matthieu Brouillard (HEAD -> master)
         * | * c8a6fb0 - (4 minutes ago) content D - Matthieu Brouillard (tag: 1.1.0)
         * |/
         * * 2a5bc65 - (4 minutes ago) content C - Matthieu Brouillard
         * * 3466319 - (4 minutes ago) content B - Matthieu Brouillard (tag: 1.0.0)
         * * d43eee1 - (4 minutes ago) content A - Matthieu Brouillard
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s8_main_and_branch_with_intermediate_light_tag() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .tag("1.0.0")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tagLight("1.1.0")
                    .commit("content", "E")
                    .branchOnAppId("issue-10", "C")
                    .commit("content", "F")
                    .master();
        }

        /**
         * Builds an empty git repository.
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s9_empty_repository() {
            return new ScenarioBuilder();
        }

        /**
         * Builds a one commit git repository.
         * <pre>
         * $ git lg
         * * ee22e69 - (26 seconds ago) content A - Matthieu Brouillard (HEAD -> master)
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s10_one_commit_no_tag_repository() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .master();
        }

        /**
         * Builds a linear repository with no tags.
         * <pre>
         * $ git lg
         * * abeca86 - (22 seconds ago) content C - Matthieu Brouillard (HEAD -> master)
         * * 877b5e1 - (22 seconds ago) content B - Matthieu Brouillard
         * * c487e67 - (22 seconds ago) content A - Matthieu Brouillard
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s11_linear_no_tag_repository() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .commit("content", "C")
                    .master();
        }

        /**
         * Builds a linear repository with RC tags.
         * <pre>
         * $ git lg
         * * 3c6bad0 - (13 seconds ago) content G - Matthieu Brouillard (HEAD -> master, tag: v2.0.0, tag: 1.0.0)
         * * 54662c0 - (13 seconds ago) content F - Matthieu Brouillard
         * * 1374ed9 - (13 seconds ago) content E - Matthieu Brouillard (tag: 1.0.0-rc02)
         * * a35958a - (13 seconds ago) content D - Matthieu Brouillard
         * * e892883 - (13 seconds ago) content C - Matthieu Brouillard (tag: 1.0.0-rc01)
         * * 0954efd - (13 seconds ago) content B - Matthieu Brouillard
         * * 7510ee8 - (13 seconds ago) content A - Matthieu Brouillard (tag: v1.0.0)
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s12_linear_with_RC_tags() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tagLight("v1.0.0")
                    .commit("content", "B")
                    .commit("content", "C")
                    .tag("1.0.0-rc01")
                    .commit("content", "D")
                    .commit("content", "E")
                    .tag("1.0.0-rc02")
                    .commit("content", "F")
                    .commit("content", "G")
                    .tag("1.0.0")
                    .tagLight("v2.0.0")
                    .master();
        }

        /**
         * Builds a repository with branches name like gitflow.
         * <pre>
         * {@code
         * $ git lg
         * * * * 8c102d4 - (develop) content H (33 seconds ago) <Matthieu Brouillard>
         * * * *| * e42d934 - (feature/add-sso) content G (33 seconds ago) <Matthieu Brouillard>
         * *|/
         * *| * c426c65 - (HEAD, tag: 3.0.0, master) content D (33 seconds ago) <Matthieu Brouillard>
         * *| | * d8893cc - (release/1.x) content E (33 seconds ago) <Matthieu Brouillard>
         * *| |/
         * *|/|
         * *| | * 29d7fe2 - (release/2.x) content F (33 seconds ago) <Matthieu Brouillard>
         * *| |/
         * *| * fdbe434 - (tag: v2.0.0) content C (33 seconds ago) <Matthieu Brouillard>
         * *|/
         * * ef278a5 - content B (33 seconds ago) <Matthieu Brouillard>
         * * fb1f2d9 - (tag: 1.0.0) content A (33 seconds ago) <Matthieu Brouillard>
         * }
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s13_gitflow() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("1.0.0")
                    .commit("content", "B")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("3.0.0")
                    .branchOnAppId("release/1.x","B")
                    .commit("content", "E")
                    .branchOnAppId("release/2.x","C")
                    .commit("content", "F")
                    .tagLight("v2.0.0")
                    .branchOnAppId("feature/add-sso","B")
                    .commit("content", "G")
                    .branchOnAppId("develop","B")
                    .commit("content", "H")
                    .master();
        }

        /**
         * Builds repository witch merges H
         * <pre>
         * *   eed0ba5 (HEAD -> master) H :: merge G into E
         * |\
         * | * 5d514bf (tag: 1.0.1, hotfix) content G
         * | * 4c8d8d2 content F
         * * | a8d85b2 content E
         * * | cf49743 (tag: 1.1.1, tag: 1.1.0) content D
         * * | 8065bb5 content C
         * |/
         * * 96da53b (tag: 1.0.0, tag: 0.9.1, tag: 0.9.0) content B
         * * 2cd5f31 (tag: 0.8.0) content A
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s14_with_merges() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("0.8.0")
                    .commit("content", "B")
                    .tagLight("1.0.0")
                    .tagLight("0.9.0")
                    .tagLight("0.9.1")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("1.1.0")
                    .tag("1.1.1")
                    .commit("content", "E")
                    .branchOnAppId("hotfix","B")
                    .commit("content", "F")
                    .commit("content", "G")
                    .tag("1.0.1")
                    .master()
                    .merge("G", "H");
        }

        /**
         * Builds repository witch merges H
         * <pre>
         * *   eed0ba5 (HEAD -> master) H :: merge G into E
         * |\
         * | * 5d514bf (tag: v1.0.1, hotfix) content G
         * | * 4c8d8d2 content F
         * * | a8d85b2 content E
         * * | cf49743 (tag: v1.1.1, tag: v1.1.0) content D
         * * | 8065bb5 content C
         * |/
         * * 96da53b (tag: v1.0.0, tag: v0.9.1, tag: v0.9.0) content B
         * * 2cd5f31 (tag: v0.8.0) content A
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s14_with_merges_tag_prefixed() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("v0.8.0")
                    .commit("content", "B")
                    .tagLight("v1.0.0")
                    .tagLight("v0.9.0")
                    .tagLight("v0.9.1")
                    .commit("content", "C")
                    .commit("content", "D")
                    .tag("v1.1.0")
                    .tag("v1.1.1")
                    .commit("content", "E")
                    .branchOnAppId("hotfix","B")
                    .commit("content", "F")
                    .commit("content", "G")
                    .tag("v1.0.1")
                    .master()
                    .merge("G", "H");
        }

        /**
         * Builds repository with complex two branches merge
         * <pre>
         * {@code
         * *   e8a122b (HEAD -> master) I :: merge G into H
         * |\
         * | * 7bae650 (tag: 3.0.0, b2) content G
         * * |   92c9286 H :: merge F into E
         * |\ \
         * | * | 5a75a7a (tag: 2.0.0, b1) content F
         * | |/
         * * | 7da2d41 content E
         * * | d3782b7 content D
         * * | 788dc0e (tag: 4.0.0) content C
         * |/
         * * fe4577e (tag: 1.0.0) content B
         * * 60a8e27 content A
         * }
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s15_complex_merges() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .tag("1.0.0")
                    .commit("content", "C")
                    .tag("4.0.0")
                    .commit("content", "D")
                    .commit("content", "E")
                    .branchOnAppId("b1","B")
                    .commit("content", "F")
                    .tag("2.0.0")
                    .branchOnAppId("b2","B")
                    .commit("content", "G")
                    .tag("3.0.0")
                    .master()
                    .merge("F", "H")
                    .merge("G", "I");
        }


        /**
         * Builds repository with complex two branches merge
         * <pre>
         * {@code
         * *   318c3c9 (HEAD -> master) J :: merge H into I
         * |\
         * | * a50dde7 (b2) content H
         * | * 4d841f6 content G
         * | * 368e6d8 content F
         * * |   ed0a74c I :: merge E into B
         * |\ \
         * | * | 0d42532 (b1) content E
         * | * | 4a2582a content D
         * | * | 5fcc09c content C
         * | |/
         * * | fe753dd content B
         * |/
         * * d74b251 (tag: 1.0.0) content A
         * }
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s16_merges_with_short_path() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("1.0.0")
                    .commit("content", "B")
                    .branchOnAppId("b1","A")
                    .commit("content", "C")
                    .commit("content", "D")
                    .commit("content", "E")
                    .branchOnAppId("b2","A")
                    .commit("content", "F")
                    .commit("content", "G")
                    .commit("content", "H")
                    .master()
                    .merge("E", "I")
                    .merge("H", "J");
        }
        /**
         * Builds repository with a feature branch that contains less commits than the master branch
         * <pre>
         * {@code
         * *   318c3c9 (HEAD -> master) M2 :: merge F into H
         * |\
         * * | a50dde7 content H
         * * | 4d841f6 content G
         * | * 368e6d8 (b2) content F
         * * |   ed0a74c M1 :: merge E into D
         * |\ \
         * | * | 0d42532 (b1) content E
         * * | | 4a2582a content D
         * * | | 5fcc09c content C
         * | |/
         * * | fe753dd content B
         * |/
         * * d74b251 (tag: 1.0.0) content A
         * }
         * </pre>
         * @return the scenario object corresponding to the above git repository
         */
        public static ScenarioBuilder s17_feature_branches_with_shorter_path() {
            return new ScenarioBuilder()
                    .commit("content", "A")
                    .tag("1.0.0")
                    .commit("content", "B")
                    .commit("content", "C")
                    .commit("content", "D")
                    .branchOnAppId("b1","A")
                    .commit("content", "E")
                    .branchOnAppId("b2","A")
                    .commit("content", "F")
                    .master()
                    .merge("E", "M1", MergeCommand.FastForwardMode.NO_FF)
                    .commit("content", "G")
                    .commit("content", "H")
                    .merge("F", "M2", MergeCommand.FastForwardMode.NO_FF)
                    .commit("content", "I");
        }

        /**
         * Feature branch, wrapped inside another one
         * <pre>
         * {@code
         * * c1faa0c - (HEAD -> master) content H
         * *   93ac330 - M2 :: merge C2 into M1
         * |\
         * | * 16adcca - (bC) content C2
         * | * 71b4228 - content C1
         * * |   ae2d477 - M1 :: merge E2 into D
         * |\ \
         * | * | 381c149 - (bE) content E2
         * | * | 0dce471 - content E1
         * |/ /
         * * | 91fc982 - content D
         * |/
         * * f8b17eb - content B
         * * 9e90b91 - content A
         * }
         * </pre>
         * @return
         */
        public static ScenarioBuilder s18_wrapped_feature_branches() {
            return new Scenarios.ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .branchOnAppId("bC", "B")
                    .commit("content", "C1")
                    .commit("content", "C2")
                    .master()
                    .commit("content", "D")
                    .branchOnAppId("bE", "D")
                    .commit("content", "E1")
                    .commit("content", "E2")
                    .master()
                    .merge("E2", "M1", MergeCommand.FastForwardMode.NO_FF)
                    .merge("C2", "M2", MergeCommand.FastForwardMode.NO_FF)
                    .commit("content", "H");
        }

        /**
         * Merges performed inside a feature branch
         * <pre>
         * {@code
         * * 7b786d9 - (HEAD -> master) content E
         * *   3e1fd7f - M2 :: merge C3 into B
         * |\
         * | * 8e25e94 - (bC) content C3
         * | *   bd2a45f - M1 :: merge D2 into C2
         * | |\
         * | | * c2fa692 - (bD) content D2
         * | | * 272629b - content D1
         * | * | ed6874e - content C2
         * | |/
         * | * e9dff65 - content C1
         * |/
         * * 012d0e3 - content B
         * * 6873a91 - content A
         * }
         * </pre>
         * @return
         */
        public static ScenarioBuilder s19_merges_in_feature_branch() {
            return new Scenarios.ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .branchOnAppId("bC", "B")
                    .commit("content", "C1")
                    .commit("content", "C2")
                    .branchOnAppId("bD", "C1")
                    .commit("content", "D1")
                    .commit("content", "D2")
                    .checkoutBranch("bC")
                    .merge("D2", "M1", MergeCommand.FastForwardMode.NO_FF)
                    .commit("content", "C3")
                    .master()
                    .merge("C3", "M2", MergeCommand.FastForwardMode.NO_FF)
                    .commit("content", "E");
        }
        
        public static ScenarioBuilder s20_release_on_head() {
            return new Scenarios.ScenarioBuilder()
                    .commit("content", "A")
                    .commit("content", "B")
                    .commit("content", "C")
                    .tag("1.0.0")
                    .master();
        }
    }

    public static class Scenario {
        private File repositoryLocation;
        private Map<String, ObjectId> commits;

        public Scenario(File repository) {
            this.repositoryLocation = repository;
            commits = new HashMap<>();
        }

        /**
         * Retrieve the git repository location as a {@link File}.
         * @return the file object
         */
        public File getRepositoryLocation() {
            return repositoryLocation;
        }

        /**
         * Retrieves the named commits of the scenario. 
         * @return a non null map of scenario commits
         */
        public Map<String, ObjectId> getCommits() {
            return commits;
        }

        /**
         * Retrieves the name a given commit has been registered with.
         * @param id the identifier of the commit to retrieve the name for
         * @return the name of the id or null if not present
         */
        public String nameOf(final ObjectId id) {
            Predicate<Map.Entry<String, ObjectId>> hasGivenCommit = e -> e.getValue().getName().equals(id.getName());
            return commits.entrySet()
                    .stream()
                    .filter(hasGivenCommit)
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElseThrow(() -> new IllegalStateException("cannot find given commit " + id));
        }

        /**
         * Creates a new file with dummy content inside the git repository to make it dirty.
         * @return the created file
         * @throws IOException if an error occured creating the file
         */
        public File makeDirty() throws IOException {
            File f = File.createTempFile("dirty", ".tmp", repositoryLocation.getParentFile());
            FileWriter fw = new FileWriter(f);
            fw.write("" + System.currentTimeMillis());
            fw.flush();
            fw.close();
            return f;
        }
    }

    public static class ScenarioBuilder {
        private Scenario scenario;
        private Repository repository;
        private Git git;

        /**
         * Creates a ScenarioBuilder object pointing to a temporary fresh new & empty git repository. 
         */
        public ScenarioBuilder() {
            try {
                this.scenario = new Scenario(new File(Files.createTempDir(), ".git"));
                this.repository = FileRepositoryBuilder.create(scenario.getRepositoryLocation());
                repository.create();
                this.git = new Git(repository);
            } catch (Exception ex) {
                throw new IllegalStateException("failure building scenario", ex);
            }
        }

        /**
         * Checkout the given branch.
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder checkoutBranch(String branchName) {
            try {
                git.checkout().setName(branchName).call();
            } catch (Exception ex) {
                throw new IllegalStateException("cannot checkout " + branchName, ex);
            }
            return this;
        }

        /**
         * Checkout the master branch.
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder master() {
            return checkoutBranch("master");
//            try {
//                git.checkout().setName("master").call();
//            } catch (Exception ex) {
//                throw new IllegalStateException("cannot checkout master", ex);
//            }
//            return this;
        }

        /**
         * Creates a branch on the git commit corresponding to the given application commit id.
         * @param branchName the branch to be created
         * @param id the application ID to retrieve the git commit from
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder branchOnAppId(String branchName, String id) {
            String commitId = scenario.getCommits().get(id).name();
            try {
                git.checkout().setName(branchName).setCreateBranch(true).setStartPoint(commitId).call();
            } catch (Exception ex) {
                throw new IllegalStateException("cannot create branch: " + branchName + " on ID " + commitId, ex);
            }
            return this;
        }

        /**
         * Creates a normal/annotated tag on the git commit corresponding to the given application commit id.
         * @param tagName the name of the normal/annotated tag
         * @param id the application ID to retrieve the git commit from
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder tag(String tagName, String id) {
            return tag(tagName, false, id);
        }

        /**
         * Creates a normal/annotated tag at the current HEAD.
         * @param tagName the name of the normal/annotated tag
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder tag(String tagName) {
            return tag(tagName, false);
        }

        private ScenarioBuilder tag(String tagName, boolean light, String id) {
            try {
                TagCommand tagCommand = git.tag().setName(tagName).setAnnotated(!light);

                if (id != null) {   // we do not tag on HEAD
                    String commitId = scenario.getCommits().get(id).name();
                    ObjectId objectId = repository.resolve(commitId);
                    RevCommit commit = repository.parseCommit(objectId);
                    tagCommand.setObjectId(commit);
                }

                tagCommand.call();
            } catch (Exception ex) {
                throw new IllegalStateException(String.format("cannot add tag: %s, lightweight[%s]", tagName, light), ex);
            }

            return this;
        }

        private ScenarioBuilder tag(String tagName, boolean light) {
            return tag(tagName, light, null);
        }

        /**
         * Creates a light tag on the git commit corresponding to the given application commit id.
         * @param tagName the name of the light tag
         * @param id the application ID to retrieve the git commit from
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder tagLight(String tagName, String id) {
            return tag(tagName, true, id);
        }

        /**
         * Creates a light tag at the current HEAD.
         * @param tagName the name of the light tag
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder tagLight(String tagName) {
            return tag(tagName, true);
        }

        /**
         * Creates a commit in the repo, by modifying the given file.
         * The git commitID will be stored in the scenario in front of the given app identifier. 
         * @param fileName the filename to be touched, added and commited into the repo.
         * @param id the application identifier to use to store the git commitID in front of
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder commit(String fileName, String id) {
            File content = new File(scenario.getRepositoryLocation(), fileName);
            try {
                AddCommand add = git.add().addFilepattern(fileName);
                Files.touch(content);
                add.call();
                RevCommit rc = git.commit().setMessage("content " + id).call();
                scenario.getCommits().put(id, rc.getId());
            } catch (Exception ex) {
                throw new IllegalStateException(String.format("error creating a commit with new file %s", content), ex);
            }
            return this;
        }

        /**
         * Merges commit with id given as parameter with current branch using given FastForwardMode.
         * @param sourceId the application identifier to use as merge source
         * @param id the application identifier to use to store the git commitID of merge commit
         * @param mode the non null fast forward strategy to use for the merge
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder merge(String sourceId, String id, MergeCommand.FastForwardMode mode) {
            try {
                ObjectId other = scenario.getCommits().get(sourceId);
                ObjectId head = repository.resolve(Constants.HEAD);
                String nameOfHead = scenario.nameOf(head);
                MergeResult rc = git.merge()
                        .setFastForward(mode)
                        .setMessage(String.format("%s :: merge %s into %s", id, sourceId, nameOfHead))
                        .include(other)
                        .call();
                scenario.getCommits().put(id, rc.getNewHead());
            } catch (Exception ex) {
                throw new IllegalStateException(String.format("error merging %s", id), ex);
            }
            return this;
        }

        /**
         * Merges commit with id given as parameter with current branch. Tries a fast forward merge.
         * @param sourceId the application identifier to use as merge source
         * @param id the application identifier to use to store the git commitID of merge commit
         * @return the builder itself to continue building the scenario
         */
        public ScenarioBuilder merge(String sourceId, String id) {
            return merge(sourceId, id, MergeCommand.FastForwardMode.FF);
        }

        public Scenario getScenario() {
            return scenario;
        }

        public Repository getRepository() {
            return repository;
        }

        public Git git() {
            return git;
        }
    }
}
