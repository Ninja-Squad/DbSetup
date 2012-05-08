package com.ninja_squad.dbsetup.bind;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.DbSetupTracker;

/**
 * An object which binds a value to a prepared statement parameter. It's advised to make implementations of this
 * interface immutable, and to make them implement equals and hashCode in order for {@link DbSetupTracker} to function
 * properly, or to make them singletons.
 * @author JB
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
