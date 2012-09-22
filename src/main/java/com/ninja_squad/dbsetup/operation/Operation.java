package com.ninja_squad.dbsetup.operation;

import java.sql.Connection;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.bind.BinderConfiguration;

/**
 * An operation that the database setup executes. It's advised to make implementations of this interface
 * immutable, and to make them implement equals and hashCode in order for {@link DbSetupTracker} to function
 * properly.
 * @author JB Nizet
 */
public interface Operation {

    /**
     * Executes the operation
     * @param connection the connection used to execute the operation
     * @param configuration the binder configuration, used to get appropriate binders based on the metadata of
     * the prepared statements
     * @throws SQLException if the execution throws a SQLException
     */
    void execute(Connection connection, BinderConfiguration configuration) throws SQLException;
}
