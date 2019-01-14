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
package fr.brouillard.oss.jgitver.cli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class CLICaller {
    private final File location;

    private ByteArrayOutputStream normal;
    private ByteArrayOutputStream error;

    /**
     * Builds a CLI caller object that will used given directory
     * as base location if not overridden during standard call.
     * @param location the base directory location for the jgitver CLI call
     */
    public CLICaller(File location) {
        this.location = location;
    }

    /**
     * Calls {@link RunnableCLI} with the given arguments.
     * The arguments will be augmented with '--directory' if no location directory is providing in the given args.
     * @param args the arguments to call the CLI with
     * @return the status int value of the call
     */
    public int call(String ... args) {
        String[] callArgs = args;

        boolean containsDirectory = Arrays.asList(args).stream()
                .anyMatch(arg -> arg.startsWith("--dir=") || arg.startsWith("--directory="));

        if (!containsDirectory) {
            callArgs = new String[args.length + 1];
            callArgs[0] = String.format("--directory=%s", location.getPath());
            System.arraycopy(args, 0, callArgs, 1, args.length);
        }

        normal = new ByteArrayOutputStream();
        error = new ByteArrayOutputStream();
        return RunnableCLI.execute(callArgs, new PrintStream(normal), new PrintStream(error));
    }

    /**
     * Retrieves the output of the last call as a String.
     * @return the normal output of last call
     */
    public String getOutput() {
        try {
            return normal.toString(StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            throw new IllegalStateException("cannot retrieve normal output", ex);
        }
    }

    /**
     * Retrieves the output of the last call as a list of strings, each line being an element in the list.
     * @return a non null list of string lines.
     */
    public List<String> getLines() {
        return Arrays.asList(getOutput().split(System.lineSeparator()));
    }

    /**
     * Retrieves the error output of the last call as a String.
     * @return the error output of last call
     */
    public String getErrorOutput() {
        try {
            return error.toString(StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            throw new IllegalStateException("cannot retrieve error output", ex);
        }
    }
}
