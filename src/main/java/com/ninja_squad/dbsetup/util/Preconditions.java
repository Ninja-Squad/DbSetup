/*
 * The MIT License
 *
 * Copyright (c) 2012, Ninja Squad
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

package com.ninja_squad.dbsetup.util;

/**
 * Utility class to help verifying preconditions
 * @author JB Nizet
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Throws a NullPointerException with the given message if the given argument is <code>null</code>.
     * @param argument the argument to check for <code>null</code>
     * @param message the message of the thrown NullPointerException
     * @throws NullPointerException if argument is <code>null</code>.
     */
    public static void checkNotNull(Object argument, String message) throws NullPointerException {
        if (argument == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * Throws an IllegalStateException with the given message if the given condition is <code>false</code>.
     * @param condition the condition to check
     * @param message the message of the thrown IllegalStateException
     * @throws IllegalStateException if the condition is <code>false</code>.
     */
    public static void checkState(boolean condition, String message) throws IllegalStateException {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Throws an IllegalARgumentException with the given message if the given condition is <code>false</code>.
     * @param condition the condition to check
     * @param message the message of the thrown IllegalArgumentException
     * @throws IllegalArgumentException if the condition is <code>false</code>.
     */
    public static void checkArgument(boolean condition, String message) throws IllegalStateException {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
