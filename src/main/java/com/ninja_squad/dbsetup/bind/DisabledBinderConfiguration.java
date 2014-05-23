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

import java.sql.ParameterMetaData;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.DbSetup;

/**
 * Implementation of {@link BinderConfiguration}, to be used with {@link DbSetup} when metadata is always disabled.
 * @author Joel Takvorian
 */
public class DisabledBinderConfiguration implements BinderConfiguration {

    /**
     * A shareable, reusable instance of this class.
     */
    public static final DisabledBinderConfiguration INSTANCE = new DisabledBinderConfiguration();

    private DisabledBinderConfiguration() {
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public Binder getBinder(ParameterMetaData metadata, int param) throws SQLException {
        throw new UnsupportedOperationException("Binder is disabled");
    }

    @Override
    public String toString() {
        return "DisabledBinderConfiguration";
    }

    @Override
    public boolean isMetadataEnabled() {
        return false;
    }
}
