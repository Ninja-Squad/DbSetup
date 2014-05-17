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

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;

import javax.annotation.Nullable;
import java.sql.ParameterMetaData;
import java.sql.SQLException;

/**
 * An object which returns the appropriate {@link Binder} based on the metadata of the prepared statement.
 * The default instance of this interface is {@link DefaultBinderConfiguration}. If the binders returned by this
 * default configuration don't fit for the particular database you're using, or if you would like the binders
 * returned by the configuration to support additional data types, you might want to provide a different implementation
 * of this interface to the {@link DbSetup}.
 * <p>
 * It's advised to make implementations of this interface immutable, and to make them implement equals and hashCode
 * in order for {@link DbSetupTracker} to function properly, or to make them singletons.
 * @author JB Nizet
 */
public interface BinderConfiguration {

    /**
     * Returns the appropriate {@link Binder} for the given parameter, based on the given metadata.
     * @param metadata the metadata allowing to decide which Binder to return. <code>null</code> if the database
     * doesn't support parameter metadata, or if the Insert has been configured to not use metadata.
     * @param param the param for which a binder is requested
     * @return the binder for the given param and its metadata
     * @throws SQLException if a SQLException occurs while using the metadata
     */
    Binder getBinder(@Nullable ParameterMetaData metadata, int param) throws SQLException;
}
