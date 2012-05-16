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
import com.ninja_squad.dbsetup.DbSetupRuntimeException;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Insert;

public class RollbackIntegrationTest {
    private Connection connection;

    @Before
    public void prepare() throws SQLException {
        new DbSetup(Database.DESTINATION, Operations.sequenceOf(CommonOperations.DROP_TABLES,
                                                                CommonOperations.CREATE_TABLES)).launch();
        connection = Database.getConnection();
    }

    @After
    public void cleanup() throws SQLException {
        connection.close();
    }

    @Test
    public void testRollbackIfSQLException() throws SQLException {
        Insert insertA1 =
            Insert.into("A")
                  .columns("a_id", "nu")
                  .values(1L, 12.6)
                  .build();
        Insert insertA2 =
            Insert.into("A")
                  .columns("a_id", "fooo")
                  .values(1L, "hello")
                  .build();
        try {
            new DbSetup(Database.DESTINATION, Operations.sequenceOf(insertA1, insertA2)).launch();
            fail("expected a DbSetupRuntimeException");
        }
        catch (DbSetupRuntimeException e) {
            // expected
        }
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from A order by a_id");
        assertFalse(rs.next());
    }

    @Test
    public void testRollbackIfOtherException() throws SQLException {
        Insert insertA1 =
            Insert.into("A")
                  .columns("a_id", "nu")
                  .values(1L, 12.6)
                  .build();
        Insert insertA2 =
            Insert.into("A")
                  .columns("a_id", "nu")
                  .values(1L, "hello")
                  .build();
        try {
            new DbSetup(Database.DESTINATION, Operations.sequenceOf(insertA1, insertA2)).launch();
            fail("expected a NumberFormatException");
        }
        catch (NumberFormatException e) {
            // expected
        }
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from A order by a_id");
        assertFalse(rs.next());
    }
}
