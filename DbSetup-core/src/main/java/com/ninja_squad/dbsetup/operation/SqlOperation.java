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
import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * An operation which simply executes a SQL statement (using {@link Statement#executeUpdate(String)}). It can be useful,
 * for example, to disable or re-enable constraints before/after deleting everything from tables, or inserting into
 * tables having cross references.
 * @author JB Nizet
 */
@Immutable
public final class SqlOperation implements Operation {

    private final String sql;

    /**
     * Constructor
     * @param sql the SQL query to execute
     */
    private SqlOperation(String sql) {
        Preconditions.checkNotNull(sql, "sql may not be null");
        this.sql = sql;
    }

    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate(sql);
        }
        finally {
            stmt.close();
        }
    }

    /**
     * Creates a SqlOperation for the given SQL statement
     * @param sqlStatement the SQL statement to execute
     * @return the created SqlOperation
     */
    public static SqlOperation of(@Nonnull String sqlStatement) {
        return new SqlOperation(sqlStatement);
    }

    /**
     * Creates a sequence of SqlOperation for the given SQL statements.
     * @param sqlStatements the SQL statements to execute
     * @return the created sequence of operations
     */
    public static Operation of(@Nonnull String... sqlStatements) {
        return of(Arrays.asList(sqlStatements));
    }

    /**
     * Creates a sequence of SqlOperation for the given SQL statements.
     * @param sqlStatements the SQL statements to execute
     * @return the created sequence of operations
     */
    public static Operation of(@Nonnull List<String> sqlStatements) {
        List<SqlOperation> operations = new ArrayList<SqlOperation>(sqlStatements.size());
        for (String sql : sqlStatements) {
            operations.add(new SqlOperation(sql));
        }
        return CompositeOperation.sequenceOf(operations);
    }

    @Override
    public String toString() {
        return sql;
    }

    @Override
    public int hashCode() {
        return sql.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }

        SqlOperation other = (SqlOperation) o;
        return this.sql.equals(other.sql);
    }
}
