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

package com.ninja_squad.dbsetup.operation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.ninja_squad.dbsetup.bind.BinderConfiguration;

/**
 * An operation which deletes everything from a given database table using a TRUNCATE statement., which is sometimes
 * faster that using a DELETE statement.
 * @author JB Nizet
 */
@Immutable
public final class Truncate implements Operation {

    private final String tableToTruncate;

    private Truncate(String table) {
        this.tableToTruncate = table;
    }

    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate("truncate table " + tableToTruncate);
        }
        finally {
            stmt.close();
        }
    }

    /**
     * Returns an operation which truncates the given table.
     * @param table the table to delete everything from.
     */
    public static Truncate table(@Nonnull String table) {
        return new Truncate(table);
    }

    /**
     * Returns a composite operation which truncates the given tables, in the same order as the
     * tables. If A has a foreign key to B, which has a foreign key to C, tables should be listed in the following
     * order: A, B, C. Otherwise, referential constraint will break. If there is a cycle in the dependencies, you might
     * want to use a sequence of {@link SqlOperation} to disable the foreign key constraints, then truncate the tables,
     * then use another sequence of {@link SqlOperation} to re-enable the foreign key constraints.
     * @param tables the tables to truncate.
     */
    public static Operation tables(String... tables) {
        return tables(Arrays.asList(tables));
    }

    /**
     * Returns a composite operation which truncates the given tables, in the same order as the
     * tables. If A has a foreign key to B, which has a foreign key to C, tables should be listed in the following
     * order: A, B, C. Otherwise, referential constraint will break. If there is a cycle in the dependencies, you might
     * want to use a sequence of {@link SqlOperation} to disable the foreign key constraints, then truncate the tables,
     * then use another sequence of {@link SqlOperation} to re-enable the foreign key constraints.
     * @param tables the tables to truncate.
     */
    public static Operation tables(List<String> tables) {
        List<Truncate> operations = new ArrayList<Truncate>(tables.size());
        for (String table : tables) {
            operations.add(new Truncate(table));
        }
        return CompositeOperation.sequenceOf(operations);
    }

    @Override
    public String toString() {
        return "truncate table " + tableToTruncate;
    }

    @Override
    public int hashCode() {
        return tableToTruncate.hashCode();
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
        Truncate other = (Truncate) obj;
        return this.tableToTruncate.equals(other.tableToTruncate);
    }
}
