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
 * @author JB
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
