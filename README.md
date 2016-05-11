# jgitver: git versioning library [![Build Status](https://travis-ci.org/jgitver/jgitver.svg)](https://travis-ci.org/jgitver/jgitver)

The goal of `jgitver` is to provide a common way, via a library, to calculate a project [semver](http://semver.org) compatible version from a git repository and the tags it contains.
By doing so, it will then be _easy_ to integrate it into build systems like maven, gradle or ant.

## How it works

`jgitver` uses annotated tags, lightweight tags, branches names & commits to deduce the version of a particular git commit. 
From a given commit, a little bit like `git describe` command, `jgitver` walks thru the commit tree to retrieve tag(s) on ancestor commit(s). From there , depending on the configuration, a version will be deducted/calculated.

## Simplicity & power

`jgitver` comes with default modes that follow best practices & conventions making it a no brainer to use with good defaults but you can configure it to work as you would like to.

### versions, identifier & qualifiers

When computing versions, `jgitver` focuses on providing [semver](http://semver.org) compatible versions.

- version: as defined by [semver](http://semver.org), a serie of X.Y.Z where X, Y & Z are non-negative integers
- identifier: a textual information following the version, build from alphanumeric characters & hyphen
- qualifiers: qualifiers are textual information that can be combined to build a [semver](http://semver.org) identifier

### Quick examples

Before going into deep explanations & documentation let's first show what you will have when using `jgitver` on your git projects.

#### Using default configuration

![Default configuration](src/doc/images/jgitver-configurable-defaults.gif?raw=true "default configuration")

#### Using default _maven like_ configuration
 
![Default maven like](src/doc/images/jgitver-maven-like.gif?raw=true "maven like")

## Usage

Most of the time you will want to use `jgitver` via one of its extensions/plugins:

- [jgitver maven plugin](http://www.github.com/jgitver/jgitver-maven-plugin), which can be used in its simplest form as:
    ```
    ...
      <build>
          <extensions>
              <extension>
                  <groupId>fr.brouillard.oss</groupId>
                  <artifactId>jgitver-maven-plugin</artifactId>
                  <version>X.Y.Z</version>
              </extension>
          </extensions>
         ...
      </build>
    ...
    ```
    find the latest version on [maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22fr.brouillard.oss%22%20AND%20a%3A%22jgitver-maven-plugin%22) 
    
- [jgitver gradle plugin](https://github.com/jgitver/gradle-jgitver-plugin) which can be used using plugins DSL syntax:
    ```
    plugins {
      id "fr.brouillard.oss.gradle.jgitver" version "X.Y.Z"
    }
    ```
    find the latest version in [gradle plugin portal](https://plugins.gradle.org/plugin/fr.brouillard.oss.gradle.jgitver)

But of course, `jgitver` is a java library published on [maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22fr.brouillard.oss%22%20AND%20a%3A%22jgitver%22) and can be used as such.

```java
package fr.brouillard.oss.jgitver;

import java.io.File;

public class UsageExample {
    /**
     * Display the calculated version of the working directory, using jgitver in mode 'maven like'.
     */
    public static void main(String[] args) throws Exception {
        File workDir = new File(System.getProperty("user.dir"));
        try (GitVersionCalculator jgitver = GitVersionCalculator.location(workDir).setMavenLike(true)) {
            System.out.println(jgitver.getVersion());
        }
    }
}
```

## Concepts

### Annotated tags

When the HEAD is on a git commit which contains an annotated tag that matches a version definition, this annotated tag is used to extract the version.

### Lightweight tags

Lightweight tags are used by jgitver to better control the resulting version calculation (for example jump from 1.0.X scheme to 2.0.X starting from commit _ABCDEF_).

If you do not know the difference between lightweight & annotated tags, please refer to [git documentation](https://git-scm.com/docs/git-tag) ; here is an extract of _git tag_ man page. 

> Annotated tags are meant for release while lightweight tags are meant for private or temporary object labels. 
> For this reason, some git commands for naming objects (like git describe) will ignore lightweight tags by default.

In contrary to annotated tags, Lightweight tags are considered by jgitver as _ìndicators_ and will be used as a basis for other computation/calculations depending on the configuration:
- adding SNAPSHOT qualifier
- adding distance qualifier
- adding SHA1 qualifier
- ...

### default version 

When no suitable tag can be found in the commit history, then `jgitver` will consider that a virtual lightweight tag was found on first commit with a version `0.0.0`.  

## Configuration, modes & strategies

### Maven strategy

In this mode (_which is the default mode of the [jgitver maven plugin](http://www.github.com/jgitver/jgitver-maven-plugin)_) activated by a call to `GitVersionCalculator#setMavenLike(true)`, `jgitver` will:

- on a DETACHED HEAD having an annotated tag, use the tag name without further computation
- add SNAPSHOT qualifier to the calculated version
- increase the patch version except if it comes from a lightweight tag
- use annotated tags before lightweight ones when on a DETACHED HEAD 
- use lightweight tags before annotated ones when on a normal branch (master or any other branch)
- add a branch qualifier on purpose

Parameters affecting this mode:

- `GitVersionCalculator#setNonQualifierBranches(String)`: comma separated list of branch name for which no branch qualifier will be used. Default value is _master_.

### Default strategy

In this mode, which is the default one, `jgitver` will:

- on a DETACHED HEAD having an annotated tag, use the tag name without further computation
- use annotated tags before lightweight ones when on a DETACHED HEAD
- use lightweight tags before annotated ones when on a normal branch (master or any other branch)
- add a branch qualifier on purpose

Then depending on the configuration it will also:

- `GitVersionCalculator#setUseDistance(boolean)`: add distance from HEAD as a qualifer, default is _true_
- `GitVersionCalculator#setAutoIncrementPatch(boolean)`: increment the patch version except if it comes from a lightweight tag, default is _false_
- `GitVersionCalculator#setNonQualifierBranches(String)`: comma separated list of branch name for which no branch qualifier will be used. Default value is _master_.
- `GitVersionCalculator#setUseGitCommitId(boolean)`: add git commit HEAD SHA1 as a qualifier, default is _false_
- `GitVersionCalculator#setGitCommitIdLength(int)`: truncate the previous qualifier to the given length. Valid value must be between 8 & 40, default is _8_ 

### Versions naming & extraction

`jgitver` uses a pattern recognition in order to filter the tags it uses for any version computation.

The pattern used is the following (_interpreted as [java.util.regex.Pattern](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html))_: `v?([0-9]+(?:\.[0-9]+){0,2}(?:-[a-zA-Z0-9\-_]+)?)``

For non regex experts basically it identifies:

- dotted versions in the form MAJOR.MINOR.PATCH, where MAJOR, MINOR & PATCH are integers and having MINOR & PATCH as optional
- followed optionally by a `-` (_minus_) sign and an identifier. The identifier can be interpreted by `jgitver` as a serie of qualifiers separated by the `-` (_minus_) sign
- the version can be optionally preceded by the 'v' (letter V) character
