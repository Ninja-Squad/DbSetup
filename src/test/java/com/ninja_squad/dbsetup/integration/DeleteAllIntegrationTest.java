package com.ninja_squad.dbsetup.integration;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
public class DeleteAllIntegrationTest {

    private Connection connection;

    @Before
    public void prepare() throws SQLException {
        new DbSetup(Database.DESTINATION, Operations.sequenceOf(CommonOperations.DROP_TABLES,
                                                                CommonOperations.CREATE_TABLES,
                                                                CommonOperations.INSERT_ROWS)).launch();
        connection = Database.getConnection();
    }

    @After
    public void cleanup() throws SQLException {
        connection.close();
    }

    @Test
    public void testDeleteAll() throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from A");
        assertTrue(rs.next());
        rs.close();
        rs = stmt.executeQuery("select * from B");
        assertTrue(rs.next());
        connection.close();

        new DbSetup(Database.DESTINATION, Operations.deleteAllFrom("B", "A")).launch();
        connection = Database.getConnection();
        stmt = connection.createStatement();
        rs = stmt.executeQuery("select * from A");
        assertFalse(rs.next());
        rs.close();
        rs = stmt.executeQuery("select * from B");
        assertFalse(rs.next());
    }
}
