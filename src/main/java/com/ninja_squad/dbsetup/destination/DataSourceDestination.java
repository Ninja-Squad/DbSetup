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

package com.ninja_squad.dbsetup.destination;

import com.ninja_squad.dbsetup.util.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A destination which wraps a DataSource and gets its connection from the wrapped DataSource
 * @author JB Nizet
 */
@Immutable
public final class DataSourceDestination implements Destination {
    private final DataSource dataSource;

    /**
     * Factory method creating a new DataSourceDestination. This allows a more readable style than using the
     * constructor:
     *
     * <pre>
     *    DbSetup dbSetup = new DbSetup(DataSourceDestination.with(dataSource), operation);
     * </pre>
     *
     * or, if this method is statically imported:
     *
     * <pre>
     *    DbSetup dbSetup = new DbSetup(with(dataSource), operation);
     * </pre>
     *
     * instead of
     *
     * <pre>
     *    DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
     * </pre>
     *
     * @param dataSource the wrapped DataSource
     */
    public static DataSourceDestination with(@Nonnull DataSource dataSource) {
        return new DataSourceDestination(dataSource);
    }

    /**
     * Constructor
     * @param dataSource the wrapped DataSource
     */
    public DataSourceDestination(@Nonnull DataSource dataSource) {
        Preconditions.checkNotNull(dataSource, "dataSource may not be null");
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public String toString() {
        return "DataSourceDestination [dataSource=" + dataSource + "]";
    }

    @Override
    public int hashCode() {
        return dataSource.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataSourceDestination other = (DataSourceDestination) obj;
        return dataSource.equals(other.dataSource);
    }
}
