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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class CLIPrintVersionTestIT {
    @Test
    public void cli_shows_version() throws IOException {
        String[][] executionArguments = {
                {"-V"},
                {"--version"}
        };

        for (String[] args : executionArguments) {
            ByteArrayOutputStream dataContainer = new ByteArrayOutputStream();
            PrintStream normalStream = new PrintStream(dataContainer);
            PrintStream errorStream = Streams.noop();
            int exitCode = RunnableCLI.execute(args, normalStream, errorStream);

            assertEquals(2, exitCode, "exit code when showing version should be 2");
            String printedVersionMessage = dataContainer.toString(StandardCharsets.UTF_8.name());

            String firstLine = printedVersionMessage.split(System.lineSeparator())[0];

            Pattern messageRecognizerPattern = Pattern.compile("^jgitver [0-9]+\\.[0-9]+\\.[0-9]+(?:[^\\s]*)\\s\\([a-z0-9]{40}\\)$");
            Matcher messageRecognizerMatcher = messageRecognizerPattern.matcher(firstLine);

            assertTrue(
                messageRecognizerMatcher.matches(),
                () -> String.format(
                    "for option %s, output message:%n%s%nwas not recognized%n",
                    args[0],
                    firstLine
                )
            );
        }
    }
}
