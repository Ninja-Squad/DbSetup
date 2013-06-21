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

/**
 * A value generator allows generating values for a specific column in a sequence of inserts. This is useful when you
 * don't want to specify a value for each of the inserted rows in a table, and when a default value is not an option
 * either because, for example, the column has a unique constraint.
 * @param <T> the type of value that this generator generates
 *
 * @see ValueGenerators for useful implementations of this interface
 * @author JB Nizet
 */
public interface ValueGenerator<T> {
    /**
     * Called each time a new row is inserted, to get the value to insert in the column using this value generator.
     * @return the value to insert in the column associated with this generator.
     */
    T nextValue();
}
