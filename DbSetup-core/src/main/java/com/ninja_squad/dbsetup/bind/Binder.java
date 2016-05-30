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

package com.ninja_squad.dbsetup.bind;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.DbSetupTracker;

/**
 * An object which binds a value to a prepared statement parameter. It's advised to make implementations of this
 * interface immutable, and to make them implement equals and hashCode in order for {@link DbSetupTracker} to function
 * properly, or to make them singletons.
 * @author JB Nizet
 */
public interface Binder {
    /**
     * Binds the given value to the given parameter in the given prepared statement.
     * @param statement the statement to bind the parameter to
     * @param param The index of the parameter to bind in the statement
     * @param value The value to bind (may be <code>null</code>)
     * @throws SQLException if the binding throws a {@link SQLException}
     */
    void bind(PreparedStatement statement, int param, Object value) throws SQLException;
}
