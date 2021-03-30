# jgitver changelog

Changelog of [jgitver](https://github.com/jgitver/jgitver) project.

## 0.14.0
### GitHub [#81](https://github.com/jgitver/jgitver/issues/81) Conditional Version Pattern

* [47704ea98f7809e](https://github.com/jgitver/jgitver-maven-plugin/commit/47704ea98f7809e) Add version pattern on branchingPolicies : Zomzog *2021-03-29 09:56:45*

### Without linked issue

* [22205b8c18b6637](https://github.com/jgitver/jgitver-maven-plugin/commit/22205b8c18b6637) force mvnw checkout to use LF : Matthieu Brouillard *2021-03-30 08:30:34*
* [ff1c923e520cdf8](https://github.com/jgitver/jgitver-maven-plugin/commit/ff1c923e520cdf8) update jgit to latest version : Matthieu Brouillard *2021-03-29 09:57:10*
* [4b268f1e3851cfc](https://github.com/jgitver/jgitver-maven-plugin/commit/4b268f1e3851cfc) branchPolicy/pattern will accept the first non-null, non-empty capturing group : Marc Carter *2021-03-27 20:55:08*
* [c7895668a5f2738](https://github.com/jgitver/jgitver-maven-plugin/commit/c7895668a5f2738) add sponsor button : Matthieu Brouillard *2021-01-15 13:45:28*
* [406e6cbc2791658](https://github.com/jgitver/jgitver-maven-plugin/commit/406e6cbc2791658) add FUNDING.yml file to introduce github sponsors : Matthieu Brouillard *2021-01-15 13:45:28*

## 0.13.1
### GitHub [#106](https://github.com/jgitver/jgitver/issues/106) allow to compute &#39;next version&#39; even when HEAD is on a version tag

* [348aa71fe5d9ff4](https://github.com/jgitver/jgitver-maven-plugin/commit/348aa71fe5d9ff4) introduce forceComputation flag for maven strategy : Matthieu Brouillard *2020-06-11 13:03:56*
* [8dac3ff48c8c014](https://github.com/jgitver/jgitver-maven-plugin/commit/8dac3ff48c8c014) start computing &#39;next version&#39; when HEAD is on version tag and in dirty state : Matthieu Brouillard *2020-06-09 08:17:52*

### GitHub [#115](https://github.com/jgitver/jgitver/issues/115) get rid of groovy CVE-2020-17521 

* [047b1e3921ceabc](https://github.com/jgitver/jgitver-maven-plugin/commit/047b1e3921ceabc) force dependency resolution to groovy-2.4.21 : Matthieu Brouillard *2021-01-14 08:03:12*

### GitHub [#116](https://github.com/jgitver/jgitver/issues/116) get rid of guava CVE-2020-8908

* [a7aea38191b4b52](https://github.com/jgitver/jgitver-maven-plugin/commit/a7aea38191b4b52) update to guava-30.1-jre : Matthieu Brouillard *2021-01-14 08:38:58*

### Jira groovy-2 

* [047b1e3921ceabc](https://github.com/jgitver/jgitver-maven-plugin/commit/047b1e3921ceabc) force dependency resolution to groovy-2.4.21 : Matthieu Brouillard *2021-01-14 08:03:12*

### Jira guava-30 

* [a7aea38191b4b52](https://github.com/jgitver/jgitver-maven-plugin/commit/a7aea38191b4b52) update to guava-30.1-jre : Matthieu Brouillard *2021-01-14 08:38:58*

### Without linked issue

* [183e44988741574](https://github.com/jgitver/jgitver-maven-plugin/commit/183e44988741574) update nexus-staging-maven-plugin to 1.6.8 to try to resolve timeout problems during publication : Matthieu Brouillard *2021-01-14 10:13:52*
* [0068e4388c7e66e](https://github.com/jgitver/jgitver-maven-plugin/commit/0068e4388c7e66e) add COMMIT_ISO_TIMESTAMP metadata compatible with ISO format in UTC timezone : Loi?c PE?RON *2021-01-13 11:55:05*
* [f510e329e6993e8](https://github.com/jgitver/jgitver-maven-plugin/commit/f510e329e6993e8) checkout all commits for github actions : Matthieu Brouillard *2020-12-21 08:53:28*
* [f12cb6230b01900](https://github.com/jgitver/jgitver-maven-plugin/commit/f12cb6230b01900) add github actions script : Matthieu Brouillard *2020-12-21 08:53:28*
* [cce7b10dc984c24](https://github.com/jgitver/jgitver-maven-plugin/commit/cce7b10dc984c24) Update README : Cédric Chantepie *2020-07-28 18:34:29*
* [bd6701fb2ac77b1](https://github.com/jgitver/jgitver-maven-plugin/commit/bd6701fb2ac77b1) introduce caching of maven artifacts in travis-ci build : Matthieu Brouillard *2020-07-28 16:02:45*
* [d186dff6d3966aa](https://github.com/jgitver/jgitver-maven-plugin/commit/d186dff6d3966aa) use shared base version computation from VersionStrategy : Matthieu Brouillard *2020-07-28 15:38:01*
* [a74a43d3397fcd0](https://github.com/jgitver/jgitver-maven-plugin/commit/a74a43d3397fcd0) add PROVIDED_BRANCH_NAME metadata : Matthieu Brouillard *2020-07-28 15:38:01*
* [7913c841a4bc5d4](https://github.com/jgitver/jgitver-maven-plugin/commit/7913c841a4bc5d4) introduce ANNOTATED metadata : Matthieu Brouillard *2020-07-28 15:38:01*
* [6a5101a5eba91b2](https://github.com/jgitver/jgitver-maven-plugin/commit/6a5101a5eba91b2) introduce DETACHED_HEAD metadata : Matthieu Brouillard *2020-07-28 15:38:01*
* [562a2910a06eeec](https://github.com/jgitver/jgitver-maven-plugin/commit/562a2910a06eeec) publish COMMIT_TIMESTAMP as metadata for SCRIPT version strategy : Matthieu Brouillard *2020-07-28 15:38:01*
* [55b8e8b43f49c59](https://github.com/jgitver/jgitver-maven-plugin/commit/55b8e8b43f49c59) introduce transformers for metadatas before script evaluation : Matthieu Brouillard *2020-07-28 15:38:01*
* [40f78cf860e8c52](https://github.com/jgitver/jgitver-maven-plugin/commit/40f78cf860e8c52) introduce BASE_COMMIT_ON_HEAD metadata : Matthieu Brouillard *2020-07-28 15:38:01*
* [d92e784def955ae](https://github.com/jgitver/jgitver-maven-plugin/commit/d92e784def955ae) refactor ScriptVersionStrategy : Matthieu Brouillard *2020-07-28 15:38:00*
* [888eeb588cb25d2](https://github.com/jgitver/jgitver-maven-plugin/commit/888eeb588cb25d2) Default scenario tests for SCRIPT strategy : Cédric Chantepie *2020-07-28 15:38:00*
* [41aed210be8f0e8](https://github.com/jgitver/jgitver-maven-plugin/commit/41aed210be8f0e8) New SCRIPT strategy : Cédric Chantepie *2020-07-28 15:38:00*
* [023b4348d394d99](https://github.com/jgitver/jgitver-maven-plugin/commit/023b4348d394d99) Other tests for SCRIPT strategy : Cédric Chantepie *2020-07-28 15:38:00*
* [f96f2d7d4d5c622](https://github.com/jgitver/jgitver-maven-plugin/commit/f96f2d7d4d5c622) add forceComputation in README cli commands : Matthieu Brouillard *2020-07-20 15:20:55*
* [00ce14a1f031b02](https://github.com/jgitver/jgitver-maven-plugin/commit/00ce14a1f031b02) update README statuses section : Matthieu Brouillard *2020-06-11 13:47:10*
* [235526b9ea4934f](https://github.com/jgitver/jgitver-maven-plugin/commit/235526b9ea4934f) Update README.md : Jack Warren *2019-12-02 21:00:12*

## 0.13.0
### Without linked issue

* [5bca22ab091df68](https://github.com/jgitver/jgitver-maven-plugin/commit/5bca22ab091df68) introduce compatibility classes with maven-script-interpreter:1.3 : Matthieu Brouillard *2020-11-23 19:44:34*
* [41a8f4c463bf734](https://github.com/jgitver/jgitver-maven-plugin/commit/41a8f4c463bf734) update dependencies to remove vulnerabilities : Matthieu Brouillard *2020-11-23 19:15:23*

## 0.12.0
### GitHub [#91](https://github.com/jgitver/jgitver/issues/91) Set strategy parameters from CLI

* [11af5f96a973bd8](https://github.com/jgitver/jgitver-maven-plugin/commit/11af5f96a973bd8) add CLI parameters to control pattern strategy options : Jack Warren *2019-11-04 07:20:09*

### Jira jdk-8 

* [9405fc693aaa061](https://github.com/jgitver/jgitver-maven-plugin/commit/9405fc693aaa061) for travis-ci build, use Ubuntu Trusty to allow usage of oraclejdk8 : Jack Warren *2019-11-04 07:15:13*

### Without linked issue

* [de52d3b4a6546fd](https://github.com/jgitver/jgitver-maven-plugin/commit/de52d3b4a6546fd) add explicit tests for DIRTY_TEXT metatada : Matthieu Brouillard *2019-11-29 07:53:48*
* [16c631ba8795ab8](https://github.com/jgitver/jgitver-maven-plugin/commit/16c631ba8795ab8) Add DIRTY_TEXT metadata : Alexey Genus *2019-11-26 09:45:09*
* [df422dcc54bc09d](https://github.com/jgitver/jgitver-maven-plugin/commit/df422dcc54bc09d) add unit test : Zomzog *2019-11-25 07:39:24*
* [ccf85d4b75657d2](https://github.com/jgitver/jgitver-maven-plugin/commit/ccf85d4b75657d2) add git commit for tags : Zomzog *2019-11-25 07:39:24*
* [6bce2210d9b21cf](https://github.com/jgitver/jgitver-maven-plugin/commit/6bce2210d9b21cf) Fix license header formatting : Jack Warren *2019-11-11 15:01:16*
* [dc9a22c9b3f366b](https://github.com/jgitver/jgitver-maven-plugin/commit/dc9a22c9b3f366b) Add branchPolicy configuration : Jack Warren *2019-11-04 16:49:10*
* [af2f8d03f4bf3be](https://github.com/jgitver/jgitver-maven-plugin/commit/af2f8d03f4bf3be) Picocli parse depcrecated in favor of parseArgs : Jack Warren *2019-11-04 16:49:09*
* [8e0fd192b01ecba](https://github.com/jgitver/jgitver-maven-plugin/commit/8e0fd192b01ecba) Bump picocli version (4.0.0 and above have ArgGroup support) : Jack Warren *2019-11-04 16:49:09*
* [01cf6ef9a60e708](https://github.com/jgitver/jgitver-maven-plugin/commit/01cf6ef9a60e708) add @SuppressWarnings annotations to unused versionHelp and usageHelp CLI options : Jack Warren *2019-11-04 07:19:20*
* [e195600dcb76ad2](https://github.com/jgitver/jgitver-maven-plugin/commit/e195600dcb76ad2) add snapshot usage to configurable strategy : Thibault Duperron *2019-11-04 07:16:05*

## 0.11.2
### GitHub [#24](https://github.com/jgitver/jgitver/issues/24) Allow for the &quot;-g&quot; prefix to be part of the generated version number

* [7f4dfbecc745f68](https://github.com/jgitver/jgitver-maven-plugin/commit/7f4dfbecc745f68) improve performance and prevent loops by not following parents which have already been processed : Andreas Schläpfer *2019-06-21 06:02:49*

### GitHub [#82](https://github.com/jgitver/jgitver/issues/82) Wrong calculation of commit distance

* [7f4dfbecc745f68](https://github.com/jgitver/jgitver-maven-plugin/commit/7f4dfbecc745f68) improve performance and prevent loops by not following parents which have already been processed : Andreas Schläpfer *2019-06-21 06:02:49*

## 0.11.1
### GitHub [#84](https://github.com/jgitver/jgitver/issues/84) update project dependencies

* [8e8b2284042cb04](https://github.com/jgitver/jgitver-maven-plugin/commit/8e8b2284042cb04) revert jgit version update : Matthieu Brouillard *2019-06-03 10:43:29*
* [ee8ffdff5f8cb7a](https://github.com/jgitver/jgitver-maven-plugin/commit/ee8ffdff5f8cb7a) bump relevant versions of dependencies &amp; plugins : Matthieu Brouillard *2019-06-03 06:39:43*

### GitHub [#85](https://github.com/jgitver/jgitver/issues/85) jgitver does not work with latest jgit version

* [ade020477dfae9d](https://github.com/jgitver/jgitver-maven-plugin/commit/ade020477dfae9d) use commits without CRLF issues in tests : Matthieu Brouillard *2019-06-04 12:25:49*
* [1b63fdd7af3c4c4](https://github.com/jgitver/jgitver-maven-plugin/commit/1b63fdd7af3c4c4) update to latest working jgit version : Matthieu Brouillard *2019-06-03 13:35:22*

### Without linked issue

* [93c67666286f347](https://github.com/jgitver/jgitver-maven-plugin/commit/93c67666286f347) normalize line ending with &#39;git add --renormalize .&#39; : Matthieu Brouillard *2019-06-04 12:46:57*
* [471c8faac1c6169](https://github.com/jgitver/jgitver-maven-plugin/commit/471c8faac1c6169) use latest jgit 5.3.X version : Matthieu Brouillard *2019-06-04 12:44:14*
* [8bfc03c6b2ade57](https://github.com/jgitver/jgitver-maven-plugin/commit/8bfc03c6b2ade57) use maven wrapper 3.6.1 : Matthieu Brouillard *2019-06-03 13:01:30*
* [47374aa9bca7b0c](https://github.com/jgitver/jgitver-maven-plugin/commit/47374aa9bca7b0c) document test scenario s19 : Matthieu Brouillard *2019-06-03 07:15:27*
* [6f9a3bd838bad51](https://github.com/jgitver/jgitver-maven-plugin/commit/6f9a3bd838bad51) add static method factory for Pair class : Matthieu Brouillard *2019-06-03 06:17:02*

## 0.11.0
### GitHub [#65](https://github.com/jgitver/jgitver/issues/65) InvalidStateException when running build on worktree

* [e42d69b2aa2d05b](https://github.com/jgitver/jgitver-maven-plugin/commit/e42d69b2aa2d05b) add test case to ckeck git worktree does not fail with jgitver : Matthieu Brouillard *2019-01-25 09:18:35*

### GitHub [#74](https://github.com/jgitver/jgitver/pull/74) Add ability to use the plugin on `git worktree`

* [fc4ef21ba83f622](https://github.com/jgitver/jgitver-maven-plugin/commit/fc4ef21ba83f622) update docker image used on gitlab-ci to openjdk:11 : Matthieu Brouillard *2019-01-25 09:18:35*
* [e42d69b2aa2d05b](https://github.com/jgitver/jgitver-maven-plugin/commit/e42d69b2aa2d05b) add test case to ckeck git worktree does not fail with jgitver : Matthieu Brouillard *2019-01-25 09:18:35*

### GitHub [#80](https://github.com/jgitver/jgitver/issues/80) upgrade checkstyle

* [ba4e1e127e317c6](https://github.com/jgitver/jgitver-maven-plugin/commit/ba4e1e127e317c6) update checkstyle to latest 8.19 : Matthieu Brouillard *2019-04-08 12:12:57*

### GitHub [#82](https://github.com/jgitver/jgitver/issues/82) Wrong calculation of commit distance

* [649b86484567eef](https://github.com/jgitver/jgitver-maven-plugin/commit/649b86484567eef) add a new default distance calculation based on visiting the &#39;first parent&#39; first then the other branches. : Matthieu Brouillard *2019-06-03 05:24:25*

### GitHub [#83](https://github.com/jgitver/jgitver/issues/83) add a meta meta.COMMIT_DISTANCE_TO_ROOT

* [eaa483abf41ef6a](https://github.com/jgitver/jgitver-maven-plugin/commit/eaa483abf41ef6a) change root commit detection for a more efficient implementation : Matthieu Brouillard *2019-05-31 15:24:19*
* [d150b23c5866cf9](https://github.com/jgitver/jgitver-maven-plugin/commit/d150b23c5866cf9) revert automatic computation of distance to root computation. : Andreas Schläpfer *2019-05-31 15:17:57*
* [fc3cbfc1b8d243f](https://github.com/jgitver/jgitver-maven-plugin/commit/fc3cbfc1b8d243f) compute Metadatas.COMMIT_DISTANCE_TO_ROOT in relevant strategies : Matthieu Brouillard *2019-05-22 12:03:11*

### Without linked issue

* [df41ecc304a23fe](https://github.com/jgitver/jgitver-maven-plugin/commit/df41ecc304a23fe) correct s14 scenarios log : Matthieu Brouillard *2019-05-22 12:03:10*
* [5ca6b81bbd55d4f](https://github.com/jgitver/jgitver-maven-plugin/commit/5ca6b81bbd55d4f) add utility method to compute distance to root : Matthieu Brouillard *2019-05-22 12:03:10*
* [4a5f81f8befd193](https://github.com/jgitver/jgitver-maven-plugin/commit/4a5f81f8befd193) modify DistanceCalculator API, make it use a more generic type : Matthieu Brouillard *2019-05-22 12:03:10*
* [22e2fa6c6ed451c](https://github.com/jgitver/jgitver-maven-plugin/commit/22e2fa6c6ed451c) add test for root distance : Matthieu Brouillard *2019-05-22 12:03:10*
* [6aa0a46bf68e38a](https://github.com/jgitver/jgitver-maven-plugin/commit/6aa0a46bf68e38a) expose ScenarioBuilder objects for test scenarios : Matthieu Brouillard *2019-05-22 12:03:09*
* [5af4340ff873d35](https://github.com/jgitver/jgitver-maven-plugin/commit/5af4340ff873d35) add .gitattributes file to force LF on *.sh files : Matthieu Brouillard *2019-01-25 09:18:35*
* [7e4c55c31ae4211](https://github.com/jgitver/jgitver-maven-plugin/commit/7e4c55c31ae4211) Add ability to use the plugin on `git worktree` : Marco Jorge *2019-01-24 04:58:35*

## 0.10.2
### GitHub [#73](https://github.com/jgitver/jgitver/issues/73) IllegalStateException: failure calculating version

* [c8a6279bf1ca4d8](https://github.com/jgitver/jgitver-maven-plugin/commit/c8a6279bf1ca4d8) with LATEST lookup policy, do not use lightweight tags that do not have date information : Matthieu Brouillard *2019-01-21 08:23:28*

## 0.10.1
### GitHub [#71](https://github.com/jgitver/jgitver/issues/71) enhance lookup of GitVersionCalculatorBuilder classes

* [cac57b16415e4a2](https://github.com/jgitver/jgitver-maven-plugin/commit/cac57b16415e4a2) fallback to internal GitVersionCalculatorImplBuilder if no builder found : Matthieu Brouillard *2019-01-15 09:28:15*
* [76f03dc34f74088](https://github.com/jgitver/jgitver-maven-plugin/commit/76f03dc34f74088) try several ClassLoader to load GitVersionCalculatorBuilder classes : Matthieu Brouillard *2019-01-15 09:26:01*

## 0.10.0
### GitHub [#26](https://github.com/jgitver/jgitver/issues/26) allow to execute jgitver from command line

* [f091f988ac5fde9](https://github.com/jgitver/jgitver-maven-plugin/commit/f091f988ac5fde9) introduce an auto-exectuable artifact to use as CLI : Matthieu Brouillard *2019-01-14 13:54:52*
* [ec4fe80b8e1ac17](https://github.com/jgitver/jgitver-maven-plugin/commit/ec4fe80b8e1ac17) add a &#39;jar-with-dependencies&#39; output to the project to be able to be self runnable : Matthieu Brouillard *2019-01-14 07:38:21*

### GitHub [#70](https://github.com/jgitver/jgitver/issues/70) introduce a lookup strategy

* [fbe0e9d19b47d58](https://github.com/jgitver/jgitver-maven-plugin/commit/fbe0e9d19b47d58) implements LookupPolicy.NEAREST policy : Matthieu Brouillard *2019-01-09 08:57:56*
* [c0ef519483cd9c6](https://github.com/jgitver/jgitver-maven-plugin/commit/c0ef519483cd9c6) implements LookupPolicy.LATEST policy : Matthieu Brouillard *2019-01-09 08:09:00*
* [dfc1df0057e192a](https://github.com/jgitver/jgitver-maven-plugin/commit/dfc1df0057e192a) introduce configurable LookupPolicy : Matthieu Brouillard *2019-01-08 15:14:46*

### Without linked issue

* [5e8d596455a993f](https://github.com/jgitver/jgitver-maven-plugin/commit/5e8d596455a993f) correct failing IT test on tags : Matthieu Brouillard *2019-01-14 17:18:57*
* [54d72b3c2e7e9b2](https://github.com/jgitver/jgitver-maven-plugin/commit/54d72b3c2e7e9b2) add snyk.io badge : Matthieu Brouillard *2019-01-11 14:44:11*
* [bf6a0fb4cd2fc55](https://github.com/jgitver/jgitver-maven-plugin/commit/bf6a0fb4cd2fc55) make Commit immutable, add tests for it : Matthieu Brouillard *2019-01-11 14:17:07*
* [27ddf5438fc136a](https://github.com/jgitver/jgitver-maven-plugin/commit/27ddf5438fc136a) correct or suppress compiler warnings : Matthieu Brouillard *2019-01-11 12:36:10*
* [14da855c499c177](https://github.com/jgitver/jgitver-maven-plugin/commit/14da855c499c177) enhance test coverage on Version class : Matthieu Brouillard *2019-01-11 12:30:26*
* [ed8fc9fc92bbb6a](https://github.com/jgitver/jgitver-maven-plugin/commit/ed8fc9fc92bbb6a) add test to check directory existence &amp; readability : Matthieu Brouillard *2019-01-11 10:00:32*
* [56f413b9222620f](https://github.com/jgitver/jgitver-maven-plugin/commit/56f413b9222620f) change order of badges : Matthieu Brouillard *2019-01-11 08:51:34*
* [8411e844051cd12](https://github.com/jgitver/jgitver-maven-plugin/commit/8411e844051cd12) add coveralls plugin and travis-ci integration : Matthieu Brouillard *2019-01-11 08:30:47*
* [6256bd7f9fc832b](https://github.com/jgitver/jgitver-maven-plugin/commit/6256bd7f9fc832b) add coverage profile with jacoco : Matthieu Brouillard *2019-01-11 08:13:55*
* [ba6d4c91aee0634](https://github.com/jgitver/jgitver-maven-plugin/commit/ba6d4c91aee0634) checkstyle corrections : Matthieu Brouillard *2019-01-10 11:35:17*
* [085af36b979e909](https://github.com/jgitver/jgitver-maven-plugin/commit/085af36b979e909) finish removal of junit 4 from pom.xml : Matthieu Brouillard *2019-01-10 09:36:07*
* [00bddd4f64d042f](https://github.com/jgitver/jgitver-maven-plugin/commit/00bddd4f64d042f) move to junit5, make lifecycle of all ScenarioTest a TestInstance.Lifecycle.PER_CLASS : Matthieu Brouillard *2019-01-10 09:32:02*
* [8ac0a22d0acc6b4](https://github.com/jgitver/jgitver-maven-plugin/commit/8ac0a22d0acc6b4) correct logging in NoTagsBigRepositoryTest : Matthieu Brouillard *2019-01-10 07:51:02*
* [7f180dfe53905a2](https://github.com/jgitver/jgitver-maven-plugin/commit/7f180dfe53905a2) introduce SlowTests junit category : Matthieu Brouillard *2019-01-09 15:26:07*
* [903d3d5288a5ff5](https://github.com/jgitver/jgitver-maven-plugin/commit/903d3d5288a5ff5) document link to issue requirement for the introduction of LookupPolicy : Matthieu Brouillard *2019-01-08 15:18:59*
* [c2f7b9dc48d618a](https://github.com/jgitver/jgitver-maven-plugin/commit/c2f7b9dc48d618a) move more classes to impl package : Matthieu Brouillard *2019-01-08 15:14:47*
* [b00d2411d6e5117](https://github.com/jgitver/jgitver-maven-plugin/commit/b00d2411d6e5117) continue api/impl separation : Matthieu Brouillard *2019-01-08 15:14:47*
* [85824567e72606a](https://github.com/jgitver/jgitver-maven-plugin/commit/85824567e72606a) introduce a public GitVersionCalculator interface allowing separation of api &amp; implementation : Matthieu Brouillard *2019-01-08 15:14:47*
* [0f208fced9be253](https://github.com/jgitver/jgitver-maven-plugin/commit/0f208fced9be253) base commit lookup now return a single commit instead of a set : Matthieu Brouillard *2019-01-08 15:14:46*
* [60d9deb133ef7b3](https://github.com/jgitver/jgitver-maven-plugin/commit/60d9deb133ef7b3) use jgitver-maven-plugin 1.4.5 : Matthieu Brouillard *2019-01-07 12:54:08*

## 0.9.0
### GitHub [#66](https://github.com/jgitver/jgitver/issues/66) get rid of httpclient transitive dependency of jgit

* [e52009e359c73d1](https://github.com/jgitver/jgitver-maven-plugin/commit/e52009e359c73d1) update jgit to 5.1.3.201810200350-r : Matthieu Brouillard *2018-11-30 13:04:28*

### GitHub [#67](https://github.com/jgitver/jgitver/issues/67) fix exponential lookup introduce in #49

* [8abde222a8b5bd4](https://github.com/jgitver/jgitver-maven-plugin/commit/8abde222a8b5bd4) change implementation of computation of tags/commits retrieval : Matthieu Brouillard *2019-01-07 07:23:02*

### GitHub [#68](https://github.com/jgitver/jgitver/issues/68) avoid unnecessary re-computation of version

* [1a6d580d034d98b](https://github.com/jgitver/jgitver-maven-plugin/commit/1a6d580d034d98b) introduce caching of computed version if no parameters or HEAD have changed between 2 executions. : Matthieu Brouillard *2019-01-07 09:12:06*

### GitHub [#69](https://github.com/jgitver/jgitver/issues/69) ossindex report CVE-2018-10237 because of guava-19 usage

* [5b91d429bfbf959](https://github.com/jgitver/jgitver-maven-plugin/commit/5b91d429bfbf959) update guava to 27.0.1-jre : Matthieu Brouillard *2019-01-07 09:36:48*

### Without linked issue

* [d50d2d8dafabd54](https://github.com/jgitver/jgitver-maven-plugin/commit/d50d2d8dafabd54) use jgitver-maven-plugin 1.4.4 : Matthieu Brouillard *2018-12-10 12:56:41*
* [19ad2c47cb369fb](https://github.com/jgitver/jgitver-maven-plugin/commit/19ad2c47cb369fb) use an environment variable instead of a system property for snyk.io activation : Matthieu Brouillard *2018-11-30 13:46:04*
* [a12d329293ef12b](https://github.com/jgitver/jgitver-maven-plugin/commit/a12d329293ef12b) introduce checks profile with snyk.io dependency analysis : Matthieu Brouillard *2018-11-30 13:31:51*

## 0.8.6
### GitHub [#63](https://github.com/jgitver/jgitver/issues/63) allow to configure a maximum search depth

* [89036e031d6a74b](https://github.com/jgitver/jgitver-maven-plugin/commit/89036e031d6a74b) allow to set a maximum depth to limit searches : Matthieu Brouillard *2018-11-20 09:02:55*

### Without linked issue

* [d8b3292c7e6b7c6](https://github.com/jgitver/jgitver-maven-plugin/commit/d8b3292c7e6b7c6) extract dependencies &amp; plugin versions inside variables : Matthieu Brouillard *2018-11-23 15:12:56*
* [85ce6f88d3c72b0](https://github.com/jgitver/jgitver-maven-plugin/commit/85ce6f88d3c72b0) correct the accidentaly removed indentation in GitVersionCalculator : Matthieu Brouillard *2018-11-20 12:04:32*

## 0.8.5
### GitHub [#54](https://github.com/jgitver/jgitver/issues/54) possible StackOverflow due to recursive lookup of commits

* [d21a2204cfd8bca](https://github.com/jgitver/jgitver-maven-plugin/commit/d21a2204cfd8bca) provide test case for #54 : Matthieu Brouillard *2018-11-16 10:38:07*
* [a235f4ec571f92c](https://github.com/jgitver/jgitver-maven-plugin/commit/a235f4ec571f92c) reduce recursion depth when looking for version tags : Matthieu Brouillard *2018-11-16 10:38:07*

### GitHub [#59](https://github.com/jgitver/jgitver/issues/59) remove relocated version of petitparser

* [330edab772fa60f](https://github.com/jgitver/jgitver-maven-plugin/commit/330edab772fa60f) use standard petitparser-core version from maven central : Matthieu Brouillard *2018-11-15 17:41:44*

### Without linked issue

* [59bd3d6166f9400](https://github.com/jgitver/jgitver-maven-plugin/commit/59bd3d6166f9400) fix typos in README.md : flacki *2018-11-10 07:23:39*
* [440cd9be53b673c](https://github.com/jgitver/jgitver-maven-plugin/commit/440cd9be53b673c) use jgitver-maven-plugin 1.4.2 : Matthieu Brouillard *2018-11-07 10:38:34*

## 0.8.4
### Without linked issue

* [37f9b94cf8ec3a8](https://github.com/jgitver/jgitver-maven-plugin/commit/37f9b94cf8ec3a8) enable dirty flag usage : Matthieu Brouillard *2018-11-05 18:12:54*
* [a9ef040ecae420c](https://github.com/jgitver/jgitver-maven-plugin/commit/a9ef040ecae420c) adding new scenario to test prefixed tags : Matthieu Brouillard *2018-11-05 18:12:01*
* [e6ec0972f3b870d](https://github.com/jgitver/jgitver-maven-plugin/commit/e6ec0972f3b870d) IllegalStateException when parsing tags like v1.2.3 : Dawid Jarosz *2018-11-05 14:41:26*

## 0.8.3
### GitHub [#55](https://github.com/jgitver/jgitver/issues/55) add enforce rule to force JDK8 when releasing

* [1c9f4c4e0403f20](https://github.com/jgitver/jgitver-maven-plugin/commit/1c9f4c4e0403f20) enforce java 8 is used while releasing : Matthieu Brouillard *2018-11-04 11:59:46*

### GitHub [#56](https://github.com/jgitver/jgitver/issues/56) add enforce rule to publish without qualifier when releasing

* [3773f7bc452b6fa](https://github.com/jgitver/jgitver-maven-plugin/commit/3773f7bc452b6fa) enforce release to occure on detached head or on master branch, also protect to release in dirty state : Matthieu Brouillard *2018-11-04 12:06:22*

### GitHub [#57](https://github.com/jgitver/jgitver/issues/57) travis-ci limit clone depth to 50 leading to test failures

* [6d2cd1ebee80787](https://github.com/jgitver/jgitver-maven-plugin/commit/6d2cd1ebee80787) make travis-ci to clone entire jgitver repository : Matthieu Brouillard *2018-11-04 14:00:52*

### GitHub [#58](https://github.com/jgitver/jgitver/issues/58) use relocated version of petitparser-core dependency

* [b94aba7ff0d3350](https://github.com/jgitver/jgitver-maven-plugin/commit/b94aba7ff0d3350) use relocated version of petitparser-core : Matthieu Brouillard *2018-11-05 10:46:09*

### Jira openjdk-8 

* [0ce1538b2c7518a](https://github.com/jgitver/jgitver-maven-plugin/commit/0ce1538b2c7518a) add openjdk-8 as a build JVM jdk on travis-ci : Matthieu Brouillard *2018-11-05 07:13:38*

### Without linked issue

* [3cd9b395486e415](https://github.com/jgitver/jgitver-maven-plugin/commit/3cd9b395486e415) bump jgitver-maven-version to 1.4.0 : Matthieu Brouillard *2018-11-04 12:11:24*
* [2b4dedb4ce7a62b](https://github.com/jgitver/jgitver-maven-plugin/commit/2b4dedb4ce7a62b) move reusable ScenarioTest in upper package : Matthieu Brouillard *2018-11-04 10:23:27*

## 0.8.2
### GitHub [#53](https://github.com/jgitver/jgitver/issues/53) maxVersion and lookup for tags in merged branches should be default behavior 

* [7a9876fa442054b](https://github.com/jgitver/jgitver-maven-plugin/commit/7a9876fa442054b) remove maxVersion et maxVersionSearchDepth property and related stuff and unused MaxVersionStrategy : Matthieu Brouillard *2018-11-03 17:36:38*
* [16ae317a1d7a1c3](https://github.com/jgitver/jgitver-maven-plugin/commit/16ae317a1d7a1c3) change lookup strategy for commits, taking into account merged branches and all found tags : Matthieu Brouillard *2018-11-03 11:31:34*

## 0.8.1
### GitHub [#52](https://github.com/jgitver/jgitver/issues/52) bad behavior with merged branches

* [07b80bd36fc053f](https://github.com/jgitver/jgitver-maven-plugin/commit/07b80bd36fc053f) enhance test coverage in regard of distance computation : Matthieu Brouillard *2018-11-02 14:41:47*
* [118aaeb434c3506](https://github.com/jgitver/jgitver-maven-plugin/commit/118aaeb434c3506) change computation of commit distance. : Matthieu Brouillard *2018-11-02 13:22:11*

### Without linked issue

* [c3da91e10db83bc](https://github.com/jgitver/jgitver-maven-plugin/commit/c3da91e10db83bc) exclude jvm.config file from license-maven-plugin checks : Matthieu Brouillard *2018-11-02 14:28:18*
* [c6b170093d98c1d](https://github.com/jgitver/jgitver-maven-plugin/commit/c6b170093d98c1d) Use ScenarioTest as superclass in  Scenario14XXXTest&#39;s : Dawid Jarosz *2018-11-01 12:35:07*

## 0.8.0
### GitHub [#48](https://github.com/jgitver/jgitver/issues/48) prepare for java modules

* [421973c251238e9](https://github.com/jgitver/jgitver-maven-plugin/commit/421973c251238e9) add manifest entry &#39;Automatic-Module-Name&#39; to avoid java 9 modules collisions : Matthieu Brouillard *2018-09-25 08:30:34*

### Without linked issue

* [af9cd4f59c1402c](https://github.com/jgitver/jgitver-maven-plugin/commit/af9cd4f59c1402c) correct checkstyle error message in javadocs for tests : Matthieu Brouillard *2018-11-01 11:03:04*
* [5fb67adb1a3da3d](https://github.com/jgitver/jgitver-maven-plugin/commit/5fb67adb1a3da3d) provide some default JVM options to have build messages in english on all platforms and systems : Matthieu Brouillard *2018-11-01 11:02:44*
* [cefc843ddb805c4](https://github.com/jgitver/jgitver-maven-plugin/commit/cefc843ddb805c4) Removed some code duplication in strategy ScenarioXXXTest&#39;s : Dawid Jarosz *2018-11-01 10:21:25*
* [e356abcfa0722a1](https://github.com/jgitver/jgitver-maven-plugin/commit/e356abcfa0722a1) apply checkstyle rules : Matthieu Brouillard *2018-11-01 10:15:50*
* [0d8f6ebcda6fe6a](https://github.com/jgitver/jgitver-maven-plugin/commit/0d8f6ebcda6fe6a) update contributors list in pom.xml : Matthieu Brouillard *2018-11-01 09:57:02*
* [fca235ec08bd70c](https://github.com/jgitver/jgitver-maven-plugin/commit/fca235ec08bd70c) remove sneaky throw compiler hack in favor of a standard thrown RuntimeException : Matthieu Brouillard *2018-11-01 09:44:59*
* [87c7ff8c4d5a3b1](https://github.com/jgitver/jgitver-maven-plugin/commit/87c7ff8c4d5a3b1) Add useMaxVersion semantics all version strategies : Dawid Jarosz *2018-10-07 10:42:08*
* [fc5fbbcb2cfb92c](https://github.com/jgitver/jgitver-maven-plugin/commit/fc5fbbcb2cfb92c) Use max version of annotated tag when guessing tagType : Dawid Jarosz *2018-10-06 21:46:18*
* [b508ab08465c2c8](https://github.com/jgitver/jgitver-maven-plugin/commit/b508ab08465c2c8) Add useMaxVersion semantics to ConfigrableVersionStrategy : Dawid Jarosz *2018-10-06 21:20:12*
* [7ca0983665f1734](https://github.com/jgitver/jgitver-maven-plugin/commit/7ca0983665f1734) add gitlab-ci badge : Matthieu Brouillard *2018-09-17 12:40:13*

## 0.7.0
### GitHub [#33](https://github.com/jgitver/jgitver/issues/33) Feature Request: Ability to specify the pattern how the version is formatted

* [a09607f6e296e39](https://github.com/jgitver/jgitver-maven-plugin/commit/a09607f6e296e39) introduce PatternVersionStrategy : Matthieu Brouillard *2018-08-01 12:48:07*

### GitHub [#45](https://github.com/jgitver/jgitver/issues/45) Add maven wrapper

* [49e7086ab244986](https://github.com/jgitver/jgitver-maven-plugin/commit/49e7086ab244986) add maven wrapper : Marc Saguer *2018-09-06 13:26:44*

### Without linked issue

* [3720064d29d83db](https://github.com/jgitver/jgitver-maven-plugin/commit/3720064d29d83db) adding draw.io source file of images : Matthieu Brouillard *2018-09-12 13:18:38*
* [c06e5d735e28ef5](https://github.com/jgitver/jgitver-maven-plugin/commit/c06e5d735e28ef5) introduce gitlab as additional CI using .gitlab-ci.yml : Matthieu Brouillard *2018-09-06 14:02:27*
* [b2f6561971d82f4](https://github.com/jgitver/jgitver-maven-plugin/commit/b2f6561971d82f4) make mvnw executable : Matthieu Brouillard *2018-09-06 13:26:44*
* [7cf40f977c4af46](https://github.com/jgitver/jgitver-maven-plugin/commit/7cf40f977c4af46) use mvnw to build on travis : Matthieu Brouillard *2018-09-06 13:26:44*
* [7daa9491d09a2bd](https://github.com/jgitver/jgitver-maven-plugin/commit/7daa9491d09a2bd) introduce shell script to checkout correctly on a local branch when needed : Matthieu Brouillard *2018-08-01 14:16:50*
* [3a24bf39b659da6](https://github.com/jgitver/jgitver-maven-plugin/commit/3a24bf39b659da6) use jgitver-maven-plugin 1.3.0 : Matthieu Brouillard *2018-08-01 13:07:03*
* [a021d644dc205b4](https://github.com/jgitver/jgitver-maven-plugin/commit/a021d644dc205b4) introduce ossindex-maven-plugin : Matthieu Brouillard *2018-08-01 12:48:08*
* [8634e0cd213fac6](https://github.com/jgitver/jgitver-maven-plugin/commit/8634e0cd213fac6) correct checkstyle warnings : Matthieu Brouillard *2018-08-01 12:48:08*
* [95fb6b6b4721cdc](https://github.com/jgitver/jgitver-maven-plugin/commit/95fb6b6b4721cdc) add full version handling via &#39;${v}&#39; : Matthieu Brouillard *2018-08-01 09:49:39*
* [b531f4a94f3e1c8](https://github.com/jgitver/jgitver-maven-plugin/commit/b531f4a94f3e1c8) adding grammar for version recognition : Matthieu Brouillard *2018-08-01 09:49:38*

## v0.7.0
### Without linked issue

* [779f2cdfb75d73c](https://github.com/jgitver/jgitver-maven-plugin/commit/779f2cdfb75d73c) introduce strategies instead of mavenLike parameter : Matthieu Brouillard *2018-08-01 09:49:38*
* [48ff18fb3c9f0c9](https://github.com/jgitver/jgitver-maven-plugin/commit/48ff18fb3c9f0c9) relax version parsing regex to match semver versions with &#39;+&#39; and &#39;.&#39; &amp; introducing tests for versions parsing : Matthieu Brouillard *2018-08-01 09:49:38*
* [0b120b2bd2737a2](https://github.com/jgitver/jgitver-maven-plugin/commit/0b120b2bd2737a2) add petitparser as pom dependency : Matthieu Brouillard *2018-08-01 09:49:38*

## 0.6.1
### GitHub [#42](https://github.com/jgitver/jgitver/issues/42) expose version parts as Metadatas

* [81a90c1c159ac92](https://github.com/jgitver/jgitver-maven-plugin/commit/81a90c1c159ac92) expose MAJOR, MINOR &amp; PATCH as Metadatas : Matthieu Brouillard *2018-07-09 20:07:59*

## 0.6.0
### GitHub [#34](https://github.com/jgitver/jgitver/issues/34) update jgit dependency

* [ff8cb8aaffd5a6d](https://github.com/jgitver/jgitver-maven-plugin/commit/ff8cb8aaffd5a6d) update impacting project dependencies : Matthieu Brouillard *2017-12-11 14:15:17*

### GitHub [#35](https://github.com/jgitver/jgitver/issues/35) add new METADATAs

* [162ad5790e5da3f](https://github.com/jgitver/jgitver-maven-plugin/commit/162ad5790e5da3f) add metadats for COMMIT_TIMESTAMP &amp; QUALIFIED_BRANCH_NAME : Matthieu Brouillard *2017-12-12 08:52:34*

### GitHub [#40](https://github.com/jgitver/jgitver/issues/40) Provide the branch or tag explicitly

* [036f381abfdc7ac](https://github.com/jgitver/jgitver-maven-plugin/commit/036f381abfdc7ac) externally provide branch name for detached head : Matthieu Brouillard *2018-04-26 06:38:50*

## 0.5.1
### Without linked issue

* [c7bfe59578b242a](https://github.com/jgitver/jgitver-maven-plugin/commit/c7bfe59578b242a) enhance metadatas, add COMMIT_DISTANCE : Matthieu Brouillard *2017-12-04 18:44:06*

## 0.5.0
### GitHub [#22](https://github.com/jgitver/jgitver/issues/22) allow to define a time based qualifier

* [1ffebf4685ca486](https://github.com/jgitver/jgitver-maven-plugin/commit/1ffebf4685ca486) add implementation of timestamp qualifier : Matthieu Brouillard *2017-11-20 18:40:27*

### Without linked issue

* [d98359be1e72e1d](https://github.com/jgitver/jgitver-maven-plugin/commit/d98359be1e72e1d) use gpg instead of gpg2 : Matthieu Brouillard *2017-11-20 19:31:57*

## 0.4.0
### GitHub [#21](https://github.com/jgitver/jgitver/issues/21) enhance metadata, expose next possible versions

* [b77952b9a033a40](https://github.com/jgitver/jgitver-maven-plugin/commit/b77952b9a033a40) provide metadatas to expose next versions : Matthieu Brouillard *2017-03-21 10:32:59*

### GitHub [#23](https://github.com/jgitver/jgitver/issues/23) IDENTITY BranchNameTransformation is not respected

* [9660113279406a2](https://github.com/jgitver/jgitver-maven-plugin/commit/9660113279406a2) add a test to try to reproduce issue #23 : Matthieu Brouillard *2017-02-24 08:38:00*

### GitHub [#24](https://github.com/jgitver/jgitver/issues/24) Allow for the &quot;-g&quot; prefix to be part of the generated version number

* [94454f6f5a366ee](https://github.com/jgitver/jgitver-maven-plugin/commit/94454f6f5a366ee) correct usage of qualifier with commitId for the very first commit : Matthieu Brouillard *2017-03-11 08:37:17*
* [498cf53a1001161](https://github.com/jgitver/jgitver-maven-plugin/commit/498cf53a1001161) add support for long format like in `git describe --tags --long` : Matthieu Brouillard *2017-03-07 21:33:35*

### GitHub [#25](https://github.com/jgitver/jgitver/pull/25) Create Scenario8WithoutGPrefixCommitTest

* [94454f6f5a366ee](https://github.com/jgitver/jgitver-maven-plugin/commit/94454f6f5a366ee) correct usage of qualifier with commitId for the very first commit : Matthieu Brouillard *2017-03-11 08:37:17*

### Without linked issue

* [1c5330be696c915](https://github.com/jgitver/jgitver-maven-plugin/commit/1c5330be696c915) add gitter badge : Matthieu Brouillard *2017-03-15 08:23:37*
* [8bfe3b6c8217742](https://github.com/jgitver/jgitver-maven-plugin/commit/8bfe3b6c8217742) Create Scenario8WithoutGPrefixCommitTest : Eduardo Zamin *2017-03-10 14:14:07*
* [f0e315bc69ec53c](https://github.com/jgitver/jgitver-maven-plugin/commit/f0e315bc69ec53c) update to jgitver-maven-plugin 0.4.0 : Matthieu Brouillard *2017-02-24 08:39:10*

## 0.3.0
### GitHub [#20](https://github.com/jgitver/jgitver/issues/20) Make tags version search pattern configurable

* [9cd4ca973df9a41](https://github.com/jgitver/jgitver-maven-plugin/commit/9cd4ca973df9a41) add configurable version tag pattern matching, fixes #20 : Matthieu Brouillard *2016-12-03 00:07:41*

### Without linked issue

* [287c7549fbb9b49](https://github.com/jgitver/jgitver-maven-plugin/commit/287c7549fbb9b49) enhance checkstyle configuration, correct some checkstyle problems : Matthieu Brouillard *2016-12-02 23:19:34*

## 0.2.1
### GitHub [#18](https://github.com/jgitver/jgitver/issues/18) handle correctly useDirty even with mavenLike=true

* [9fc2607dc8bc8e5](https://github.com/jgitver/jgitver-maven-plugin/commit/9fc2607dc8bc8e5) handle dirty state &amp; qualifier in maven mode, fixes #18 : Matthieu Brouillard *2016-09-02 14:24:31*

### Without linked issue

* [c498d4a01af2b55](https://github.com/jgitver/jgitver-maven-plugin/commit/c498d4a01af2b55) rework jgitver documentation : Matthieu Brouillard *2016-10-31 14:03:59*
* [baa7665372ebf7a](https://github.com/jgitver/jgitver-maven-plugin/commit/baa7665372ebf7a) update to jgitver-maven-plugin:0.3.0 : Matthieu Brouillard *2016-10-31 14:03:59*

## 0.2.0
### Without linked issue

* [ce619019b24806f](https://github.com/jgitver/jgitver-maven-plugin/commit/ce619019b24806f) update to jgitver-maven-plugin 0.3.0-alpha4 : Matthieu Brouillard *2016-08-04 21:02:17*
* [42f5591990cb6da](https://github.com/jgitver/jgitver-maven-plugin/commit/42f5591990cb6da) update incorrect javadoc in GitVersionCalculator : Matthieu Brouillard *2016-08-04 17:20:11*

## 0.2.0-bp-alpha1
### GitHub [#12](https://github.com/jgitver/jgitver/issues/12) Improved version numbers for release branches / wild cards in nonQualifierBranches

* [5738b646de130cb](https://github.com/jgitver/jgitver-maven-plugin/commit/5738b646de130cb) introduce enhanced branching policy model, fixes #5, fixes #6, fixes #8, fixes #12, fixes #15 : Matthieu Brouillard *2016-08-04 13:22:31*

### GitHub [#15](https://github.com/jgitver/jgitver/issues/15) support gitflow branching model

* [5738b646de130cb](https://github.com/jgitver/jgitver-maven-plugin/commit/5738b646de130cb) introduce enhanced branching policy model, fixes #5, fixes #6, fixes #8, fixes #12, fixes #15 : Matthieu Brouillard *2016-08-04 13:22:31*

### GitHub [#16](https://github.com/jgitver/jgitver/issues/16) add calculated version to provided metadatas

* [ffeabbb27028776](https://github.com/jgitver/jgitver-maven-plugin/commit/ffeabbb27028776) introduce calculated version metadata, fixes #16 : Matthieu Brouillard *2016-07-22 15:44:53*

### GitHub [#17](https://github.com/jgitver/jgitver/issues/17) document metadatas

* [e4dd27da035c5f1](https://github.com/jgitver/jgitver-maven-plugin/commit/e4dd27da035c5f1) correct README with new maven extension mechanism, add metadatas documentation, fixes #17 : Matthieu Brouillard *2016-08-04 13:18:56*

### GitHub [#5](https://github.com/jgitver/jgitver/issues/5) Version matching regex should allow dot (.) in qualifier

* [5738b646de130cb](https://github.com/jgitver/jgitver-maven-plugin/commit/5738b646de130cb) introduce enhanced branching policy model, fixes #5, fixes #6, fixes #8, fixes #12, fixes #15 : Matthieu Brouillard *2016-08-04 13:22:31*

### GitHub [#6](https://github.com/jgitver/jgitver/issues/6) allow to define version matching regexp

* [5738b646de130cb](https://github.com/jgitver/jgitver-maven-plugin/commit/5738b646de130cb) introduce enhanced branching policy model, fixes #5, fixes #6, fixes #8, fixes #12, fixes #15 : Matthieu Brouillard *2016-08-04 13:22:31*

### GitHub [#8](https://github.com/jgitver/jgitver/issues/8) Allow to define branch to qualifier mapping

* [5738b646de130cb](https://github.com/jgitver/jgitver-maven-plugin/commit/5738b646de130cb) introduce enhanced branching policy model, fixes #5, fixes #6, fixes #8, fixes #12, fixes #15 : Matthieu Brouillard *2016-08-04 13:22:31*

### Without linked issue

* [b3826a49a11f489](https://github.com/jgitver/jgitver-maven-plugin/commit/b3826a49a11f489) use new jgitver-maven-plugin extension mechanism : Matthieu Brouillard *2016-08-04 12:53:08*

## 0.2.0-alpha1
### GitHub [#3](https://github.com/jgitver/jgitver/issues/3) provide contextual information, not just the version

* [9c8cccb1381608e](https://github.com/jgitver/jgitver-maven-plugin/commit/9c8cccb1381608e) provide metadatas, fixes #3 : Matthieu Brouillard *2016-07-01 09:37:13*

### GitHub [#9](https://github.com/jgitver/jgitver/issues/9) support dirty state

* [e54d754e2ef50f8](https://github.com/jgitver/jgitver-maven-plugin/commit/e54d754e2ef50f8) introduction of dirty checks &amp; dirty qualifier, fixes #9 : Matthieu Brouillard *2016-07-01 08:42:55*

## 0.1.0
### GitHub [#2](https://github.com/jgitver/jgitver/issues/2) use jgitver-maven-plugin to manage versioning

* [c52a0e212e17097](https://github.com/jgitver/jgitver-maven-plugin/commit/c52a0e212e17097) use jgitver-maven-plugin for version handling, fixes #2 : Matthieu Brouillard *2016-05-02 08:03:53*

### GitHub [#4](https://github.com/jgitver/jgitver/issues/4) provide an automatic SNAPSHOT mode for maven like behavior

* [5d2490cca62e7f5](https://github.com/jgitver/jgitver-maven-plugin/commit/5d2490cca62e7f5) introduce VersionStrategy, split between MavenVersionStrategy &amp; ConfigurableVersionStrategy, fixes #4 : Matthieu Brouillard *2016-05-13 10:55:25*

### Without linked issue

* [376b0a3b9bf7b8a](https://github.com/jgitver/jgitver-maven-plugin/commit/376b0a3b9bf7b8a) enhance README with &#39;Build &amp; release&#39; paragraph, correct headers and javadocs : Matthieu Brouillard *2016-05-13 11:50:46*

## 0.0.1
### Without linked issue

* [bf07503c8fb750a](https://github.com/jgitver/jgitver-maven-plugin/commit/bf07503c8fb750a) missing SCM and issueManagement information in POM : Matthieu Brouillard *2016-04-27 14:30:42*
* [905472be2433f17](https://github.com/jgitver/jgitver-maven-plugin/commit/905472be2433f17) prepare initial release 0.0.1 : Matthieu Brouillard *2016-04-27 14:24:24*
* [fa6fe8e75c57949](https://github.com/jgitver/jgitver-maven-plugin/commit/fa6fe8e75c57949) README cleanup : Matthieu Brouillard *2016-04-27 14:24:07*
* [127e840542420f5](https://github.com/jgitver/jgitver-maven-plugin/commit/127e840542420f5) enhance README with examples &amp; images : Matthieu Brouillard *2016-04-27 14:20:26*
* [a1356c9ede289e4](https://github.com/jgitver/jgitver-maven-plugin/commit/a1356c9ede289e4) add travis-ci continuous integration : Matthieu Brouillard *2016-04-27 12:52:45*
* [8c3aedba215481b](https://github.com/jgitver/jgitver-maven-plugin/commit/8c3aedba215481b) add version tag name pattern matching, prepare for oss release : Matthieu Brouillard *2016-04-27 12:41:27*
* [04d5b427f4c5e61](https://github.com/jgitver/jgitver-maven-plugin/commit/04d5b427f4c5e61) add initial running version with associated tests : Matthieu Brouillard *2016-04-25 16:36:16*
* [5ed3e3f47260f19](https://github.com/jgitver/jgitver-maven-plugin/commit/5ed3e3f47260f19) enhance README with main ideas for jgitver : Matthieu Brouillard *2016-04-21 21:46:20*

