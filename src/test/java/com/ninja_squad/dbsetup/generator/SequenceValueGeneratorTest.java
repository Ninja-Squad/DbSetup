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

import static org.junit.Assert.*;

/**
 * @author JB
 */
public class SequenceValueGeneratorTest {
    @Test
    public void startsAtOne() {
        assertEquals(1L, ValueGenerators.sequence().nextValue().longValue());
    }

    @Test
    public void incrementsByOne() {
        SequenceValueGenerator sequence = ValueGenerators.sequence();
        sequence.nextValue();
        assertEquals(2L, sequence.nextValue().longValue());
    }

    @Test
    public void allowsSettingNewStart() {
        SequenceValueGenerator sequence = ValueGenerators.sequence().startingAt(12L);
        assertEquals(12L, sequence.nextValue().longValue());
        sequence.startingAt(5L);
        assertEquals(5L, sequence.nextValue().longValue());
    }

    @Test
    public void allowsSettingNewIncrement() {
        SequenceValueGenerator sequence = ValueGenerators.sequence().incrementingBy(10);
        assertEquals(1L, sequence.nextValue().longValue());
        assertEquals(11L, sequence.nextValue().longValue());
    }
}
