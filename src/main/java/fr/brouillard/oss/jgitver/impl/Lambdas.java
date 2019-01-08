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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Lambdas and java8 utils features ; inspired or copied from:
 *   - https://stackoverflow.com/questions/21488056/how-to-negate-a-method-reference-predicate/30475342#30475342
 *   - https://blog.jooq.org/2016/02/18/the-mute-design-pattern/
 */
public final class Lambdas {
    private Lambdas() {
    }
    
    /**
     * Returns the given argument as a predicate, usefull when called with method reference. 
     * @param predicate the argument already resolved by the compiler as a predicate
     * @param <T> the type of the object used within the predicate
     * @return the given argument as a typed predicate
     */
    public static <T> Predicate<T> as(Predicate<T> predicate) {
        return predicate;
    }

    /**
     * Returns the given argument as a consumer, usefull when called with method reference. 
     * @param consumer the argument already resolved by the compiler as a consumer
     * @param <T> the type of the object consumed by the consumer
     * @return the given argument as a typed consumer
     */
    public static <T> Consumer<T> as(Consumer<T> consumer) {
        return consumer;
    }

    /**
     * Returns the given argument as a supplier, usefull when called with method reference. 
     * @param supplier the argument already resolved by the compiler as a supplier
     * @param <T> the type of the object produced by the supplier
     * @return the given argument as a typed supplier
     */
    public static <T> Supplier<T> as(Supplier<T> supplier) {
        return supplier;
    }

    /**
     * Returns the given argument as a function, usefull when called with method reference. 
     * @param function the argument already resolved by the compiler as a function
     * @param <T> the type of the object consumed by the function
     * @param <R> the type of the object returned by the function
     * @return the given argument as a typed function
     */
    public static <T, R> Function<T, R> as(Function<T, R> function) {
        return function;
    }
    
    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Throwable;
    }

    /**
     * Mute a failure.
     * @param runnable the runnable able to throw a CheckedException
     */
    public static void mute(CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignore) {
            // OK, better stay safe
            ignore.printStackTrace();
        }
    }
    
    /**
     * Mute a failure.
     * @param runnable the runnable able to throw a CheckedException
     */
    public static void unchecked(CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable caught) {
            throw new RuntimeException(caught);
        }
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R, E extends Throwable> {
        R apply(T attr) throws E;
    }

    /**
     * Transforms a function throwing a checked exception into an equivalent
     * throwing a {@link RuntimeException} wrapping the checked one.
     * @param function the given function to wrap
     * @param <T> the type of the function input
     * @param <R> the type of the return value of the function
     * @return a new function wrapping the given one
     */
    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R, Throwable> function) {
        return element -> {
            try {
                return function.apply(element);
            } catch (Throwable caught) {
                throw new RuntimeException(caught);
            }
        };
    }
}