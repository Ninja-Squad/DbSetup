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
 * An operation which deletes everything from a given database table.
 * @author JB
 */
@Immutable
public final class DeleteAll implements Operation {

    private final String table;

    protected DeleteAll(String table) {
        Preconditions.checkNotNull(table, "table may not be null");
        this.table = table;
    }

    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate("delete from " + table);
        }
        finally {
            stmt.close();
        }
    }

    /**
     * Returns an operation which deletes all the rows from the given table.
     * @param table the table to delete everything from.
     */
    public static DeleteAll from(@Nonnull String table) {
        return new DeleteAll(table);
    }

    /**
     * Returns a composite operation which deletes all the rows from the given tables, in the same order as the
     * tables. If A has a foreign key to B, which has a foreign key to C, tables should be listed in the following
     * order: A, B, C. Otherwise, referential constraint will break. If there is a cycle in the dependencies, you might
     * want to use a sequence of {@link SqlOperation} to disable the foreign key constraints, then delete everything
     * from the tables, then use another sequence of {@link SqlOperation} to re-enable the foreign key constraints.
     * @param tables the tables to delete everything from.
     */
    public static Operation from(@Nonnull String... tables) {
        return from(Arrays.asList(tables));
    }

    /**
     * Returns a composite operation which deletes all the rows from the given tables, in the same order as the
     * tables. If A has a foreign key to B, which has a foreign key to C, tables should be listed in the following
     * order: A, B, C. Otherwise, referential constraint will break. If there is a cycle in the dependencies, you might
     * want to use a sequence of {@link SqlOperation} to disable the foreign key constraints, then delete everything
     * from the tables, then use another sequence of {@link SqlOperation} to re-enable the foreign key constraints.
     * @param tables the tables to delete everything from.
     */
    public static Operation from(@Nonnull List<String> tables) {
        List<DeleteAll> operations = new ArrayList<DeleteAll>(tables.size());
        for (String table : tables) {
            operations.add(new DeleteAll(table));
        }
        return CompositeOperation.sequenceOf(operations);
    }

    @Override
    public String toString() {
        return "delete from " + table;
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
        DeleteAll other = (DeleteAll) obj;
        return this.table.equals(other.table);
    }
}
