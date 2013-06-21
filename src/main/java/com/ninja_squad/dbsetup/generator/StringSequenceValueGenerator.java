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

/**
 * A {@link ValueGenerator} that returns a string prefix followed by a sequence number, optionally left-padded
 * with 0 to ensure a correct ordering (for example: CODE_001, CODE_002, etc.). Instances of this generator
 * are created by {@link ValueGenerators#stringSequence(String)}.
 * @author JB
 */
public final class StringSequenceValueGenerator implements ValueGenerator<String> {
    private String prefix;
    private long next;
    private int increment;

    /**
     * The length of the number once padded. 0 if no padding must be applied
     */
    private int paddedNumberLength;

    StringSequenceValueGenerator(String prefix) {
        this(prefix, 1L, 1, 0);
    }

    private StringSequenceValueGenerator(String prefix, long next, int increment, int paddedNumberLength) {
        this.prefix = prefix;
        this.next = next;
        this.increment = increment;
        this.paddedNumberLength = paddedNumberLength;
    }

    /**
     * Tells the generator to left-pad the number it generates with 0 until the length of the number is the given
     * length. For example, passing 3 to this method will generate numbers 001, 002, 003, 004, etc. If the generated
     * number, before padding, has a length already equal or larger that the given length, the number is not padded.
     * @param paddedNumberLength the length of the number once padded. Must be > 0.
     * @return this instance, for chaining
     */
    public StringSequenceValueGenerator withLeftPadding(int paddedNumberLength) {
        Preconditions.checkArgument(paddedNumberLength > 0, "paddedNumberLength must be > 0");
        this.paddedNumberLength = paddedNumberLength;
        return this;
    }

    /**
     * Tells the generator to avoid left-padding the number it generates with 0
     * @return this instance, for chaining
     */
    public StringSequenceValueGenerator withoutLeftPadding() {
        this.paddedNumberLength = 0;
        return this;
    }

    /**
     * Restarts the sequence at the given value
     * @param start the new starting value of the sequence
     * @return this instance, for chaining
     */
    public StringSequenceValueGenerator startingAt(long start) {
        this.next = start;
        return this;
    }

    /**
     * Increments the number by the given increment.
     * @return this instance, for chaining
     */
    public StringSequenceValueGenerator incrementingBy(int increment) {
        this.increment = increment;
        return this;
    }

    @Override
    public String nextValue() {
        long number = next;
        next += increment;
        return prefix + leftPadIfNecessary(number);
    }

    private String leftPadIfNecessary(long number) {
        String numberAsString = Long.toString(number);
        if (numberAsString.length() >= paddedNumberLength) {
            return numberAsString;
        }
        StringBuilder builder = new StringBuilder(paddedNumberLength);
        for (int i = 0; i < paddedNumberLength - numberAsString.length(); i++) {
            builder.append('0');
        }
        return builder.append(numberAsString).toString();
    }

    @Override
    public String toString() {
        return "StringSequenceValueGenerator["
               + "prefix='" + prefix + '\''
               + ", next=" + next
               + ", increment=" + increment
               + ", paddedNumberLength=" + paddedNumberLength
               + "]";
    }
}
