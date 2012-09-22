package com.ninja_squad.dbsetup.integration;

import java.sql.Connection;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;

/**
 * @author JB Nizet
 */
public class Database {
    static {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final String URL = "jdbc:hsqldb:mem:mymemdb";
    public static final String USER = "SA";
    public static final String PASSWORD = "";

    public static final Destination DESTINATION = new DriverManagerDestination(URL, USER, PASSWORD);

    public static Connection getConnection() throws SQLException {
        return DESTINATION.getConnection();
    }
}
