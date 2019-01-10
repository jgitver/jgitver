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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class LambdasTests {
    static class IllegalLengthStringException extends Exception{
    }

    /**
     * Checks given input string is 4 chars long.
     * @param input the string to check
     * @throws IllegalLengthStringException in case string length is not 4
     */
    public static void ensureInputIs4CharsLong(String input) throws IllegalLengthStringException {
        if (input.length() == 4) {
            throw new IllegalLengthStringException();
        }
    }

    /**
     * Uppercase input string except if it is 4 chars long.
     * @param input the string to transform
     * @return an uppercase string
     * @throws IllegalLengthStringException in case the string is 4 chars long
     */
    public static String uppercaseFailingForLength4(String input) throws IllegalLengthStringException {
        if (input.length() == 4) {
            throw new IllegalLengthStringException();
        }

        return input.toUpperCase();
    }

    @Test
    public void check_unchecked_on_function_throws_a_runtime_excpetion() {
        Function<String, String> upperCase = Lambdas.unchecked(LambdasTests::uppercaseFailingForLength4);

        String str5 = "abcde";
        String str4 = "abcd";

        try {
            upperCase.apply(str5);
            upperCase.apply(str4);
            fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            System.out.println("it is what we expected");
        } catch (Exception re) {
            fail("should not receive a checked exception: " + re.getClass().getName());
        }
    }

    @Test
    public void check_unchecked_on_runnable_throws_a_runtime_excpetion() {
        String str5 = "abcde";
        String str4 = "abcd";

        try {
            Lambdas.unchecked(() -> ensureInputIs4CharsLong(str5));
            Lambdas.unchecked(() -> ensureInputIs4CharsLong(str4));
            fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            System.out.println("it is what we expected");
        } catch (Exception re) {
            fail("should not receive a checked exception: " + re.getClass().getName());
        }
    }
}
