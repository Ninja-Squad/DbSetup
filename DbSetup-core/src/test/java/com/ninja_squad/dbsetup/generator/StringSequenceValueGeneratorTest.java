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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author JB
 */
public class StringSequenceValueGeneratorTest {
    @Test
    public void startsAtOne() {
        assertEquals("CODE_1", ValueGenerators.stringSequence("CODE_").nextValue());
    }

    @Test
    public void incrementsByOne() {
        StringSequenceValueGenerator sequence = ValueGenerators.stringSequence("CODE_");
        sequence.nextValue();
        assertEquals("CODE_2", sequence.nextValue());
    }

    @Test
    public void allowsSettingNewStart() {
        StringSequenceValueGenerator sequence = ValueGenerators.stringSequence("CODE_").startingAt(12L);
        assertEquals("CODE_12", sequence.nextValue());
        sequence.startingAt(5L);
        assertEquals("CODE_5", sequence.nextValue());
    }

    @Test
    public void allowsSettingNewIncrement() {
        StringSequenceValueGenerator sequence = ValueGenerators.stringSequence("CODE_").incrementingBy(10);
        assertEquals("CODE_1", sequence.nextValue());
        assertEquals("CODE_11", sequence.nextValue());
    }

    @Test
    public void allowsSettingLeftPadding() {
        StringSequenceValueGenerator sequence =
            ValueGenerators.stringSequence("CODE_").withLeftPadding(2);
        assertEquals("CODE_01", sequence.nextValue());
        assertEquals("CODE_02", sequence.nextValue());
        sequence.startingAt(10L);
        assertEquals("CODE_10", sequence.nextValue());
        assertEquals("CODE_11", sequence.nextValue());
        sequence.startingAt(100L);
        assertEquals("CODE_100", sequence.nextValue());
        assertEquals("CODE_101", sequence.nextValue());
    }

    @Test
    public void allowsUnsettingLeftPadding() {
        StringSequenceValueGenerator sequence =
            ValueGenerators.stringSequence("CODE_").withLeftPadding(2);
        sequence.withoutLeftPadding();
        assertEquals("CODE_1", sequence.nextValue());
        assertEquals("CODE_2", sequence.nextValue());
    }
}
