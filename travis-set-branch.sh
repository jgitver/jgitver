#!/bin/bash
#
# Copyright (C) 2016 Matthieu Brouillard [http://oss.brouillard.fr/jgitver] (matthieu@brouillard.fr)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -ev

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
    # this is not a pull request build

    # even knowing https://stackoverflow.com/questions/3601515/how-to-check-if-a-variable-is-set-in-bash
    # we test for existence or emptiness
    if [ -z "$TRAVIS_TAG"]; then
        # normal build on a branch
        echo "branch build detected, adjusting 'git checkout' local branch to git commit SHA1"
        git checkout $TRAVIS_BRANCH
        git reset --hard $TRAVIS_COMMIT
    else
        # tag build
        echo "tag build detected, keeping travis defaults for 'git checkout'"
    fi;

else
    # this is a pull request build
        echo "PR build detected, adjusting 'git checkout' to a local PR branch "
        git checkout -b PR$TRAVIS_PULL_REQUEST
fi;

echo "asked to build: $TRAVIS_COMMIT"
git status
git log --oneline -n 1
