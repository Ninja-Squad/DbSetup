package com.ninja_squad.dbsetup.destination;

import java.sql.Connection;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.DbSetupTracker;

/**
 * The destination of a database setup. It's advised to make implementations of this
 * interface immutable, and to make them implement equals and hashCode in order for {@link DbSetupTracker} to function
 * properly, or to make them singletons.
 * @author JB
 */
public interface Destination {
    /**
     * Returns a connection to the destination database
     * @return a connection to the destination database
     * @throws SQLException if a connection can't be returned
     */
    Connection getConnection() throws SQLException;
}
