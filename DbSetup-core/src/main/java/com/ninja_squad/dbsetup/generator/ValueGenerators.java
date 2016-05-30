/*
 * The MIT License
 *
 * Copyright (c) 2013, Ninja Squad
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ninja_squad.dbsetup.generator;

import com.ninja_squad.dbsetup.util.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class containing factory methods for {@link ValueGenerator}
 * @author JB Nizet
 */
public final class ValueGenerators {
    private ValueGenerators() {
    }

    /**
     * Returns a value generator which generates a sequence of long values starting with 1, with an increment of 1.
     * The starting value and increment can be customized using
     * <pre>
     *     ValueGenerators.increment().startingAt(1000).incrementingBy(5)
     * </pre>
     */
    public static SequenceValueGenerator sequence() {
        return new SequenceValueGenerator();
    }

    /**
     * Returns a value generator which always returns the same, given value.
     */
    public static <T> ValueGenerator<T> constant(@Nullable final T constant) {
        return new ValueGenerator<T>() {
            @Override
            public T nextValue() {
                return constant;
            }

            @Override
            public String toString() {
                return "ValueGenerators.constant(" + constant + ")";
            }
        };
    }

    /**
     * Returns a value generator that returns a string prefix followed by a sequence number, optionally left-padded
     * with 0 to ensure a correct ordering (for example: CODE_001, CODE_002, etc.). The returned generator starts the
     * sequence at 1 and increments by 1, and doesn't pad the numbers.
     * @param prefix the prefix before the generated number (for example: <code>"CODE_"</code>).
     */
    public static StringSequenceValueGenerator stringSequence(@Nonnull String prefix) {
        Preconditions.checkNotNull(prefix, "prefix may not be null");
        return new StringSequenceValueGenerator(prefix);
    }

    /**
     * Returns a value generator that returns a sequence of dates, starting at a given date and incremented by a given
     * time, specified as an increment and a calendar field. The returned generator starts today at 00:00:00
     * and increments by 1 day by default.
     */
    public static DateSequenceValueGenerator dateSequence() {
        return new DateSequenceValueGenerator();
    }
}
