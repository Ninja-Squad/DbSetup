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

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Default implementation of {@link BinderConfiguration}, used by default by {@link DbSetup}.
 * @author JB Nizet
 */
public class DefaultBinderConfiguration implements BinderConfiguration {

    /**
     * A shareable, reusable instance of this class.
     */
    public static final DefaultBinderConfiguration INSTANCE = new DefaultBinderConfiguration();

    /**
     * Constructor. Protected because it doesn't make much sense to instantiate this class,
     * but extending it can be useful.
     */
    protected DefaultBinderConfiguration() {
    }

    /**
     * Uses the parameter type of the given parameter and returns the following Binders depending on the type
     * got from the metadata.
     * <ul>
     *   <li>null metadata (i.e. metadata not supported): {@link Binders#defaultBinder()}</li>
     *   <li>VARCHAR, CHAR, LONGNVARCHAR, LONGVARCHAR, NCHAR, NVARCHAR :
     *       {@link Binders#stringBinder()}</li>
     *   <li>DATE : {@link Binders#dateBinder()}</li>
     *   <li>TIME : {@link Binders#timeBinder()}</li>
     *   <li>TIMESTAMP : {@link Binders#timestampBinder()}</li>
     *   <li>INTEGER, BIGINT, SMALLINT, TINYINT : {@link Binders#integerBinder()}</li>
     *   <li>DECIMAL, DOUBLE, FLOAT, NUMERIC, REAL : {@link Binders#decimalBinder()}</li>
     *   <li>other : {@link Binders#defaultBinder()}</li>
     */
    @Override
    public Binder getBinder(ParameterMetaData metadata, int param) throws SQLException {
        if (metadata == null) {
            return Binders.defaultBinder();
        }
        int sqlType = 0;
        try {
            sqlType = metadata.getParameterType(param);
        } catch (SQLException e) {
            return Binders.defaultBinder();
            // if the parameter type is not supported by the database,
            // the default binder is used, just as if metadata is null (= useMetadata(false) had been used)
        }
        if (sqlType == Types.DATE) {
            return Binders.dateBinder();
        }
        if (sqlType == Types.TIME) {
            return Binders.timeBinder();
        }
        if (sqlType == Types.TIMESTAMP) {
            return Binders.timestampBinder();
        }
        if (sqlType == Types.BIGINT
            || sqlType == Types.INTEGER
            || sqlType == Types.SMALLINT
            || sqlType == Types.TINYINT) {
            return Binders.integerBinder();
        }
        if (sqlType == Types.DECIMAL
            || sqlType == Types.DOUBLE
            || sqlType == Types.FLOAT
            || sqlType == Types.NUMERIC
            || sqlType == Types.REAL) {
            return Binders.decimalBinder();
        }
        if (sqlType == Types.VARCHAR
            || sqlType == Types.CHAR
            || sqlType == Types.LONGNVARCHAR
            || sqlType == Types.LONGVARCHAR
            || sqlType == Types.NCHAR
            || sqlType == Types.NVARCHAR) {
            return Binders.stringBinder();
        }
        return Binders.defaultBinder();
    }

    @Override
    public String toString() {
        return "DefaultBinderConfiguration";
    }
}
