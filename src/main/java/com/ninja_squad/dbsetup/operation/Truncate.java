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
 * @author JB
 */
@Immutable
public final class Truncate implements Operation {

    private final String table;

    private Truncate(String table) {
        this.table = table;
    }

    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate("truncate table " + table);
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
        return "truncate table " + table;
    }

    @Override
    public int hashCode() {
        return table.hashCode();
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
        return this.table.equals(other.table);
    }
}
