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

/**
 * @author JB Nizet
 */
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
