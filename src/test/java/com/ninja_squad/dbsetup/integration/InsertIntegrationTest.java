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

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupRuntimeException;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.bind.Binder;
import com.ninja_squad.dbsetup.bind.Binders;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import static org.junit.Assert.*;

/**
 * @author JB Nizet
 */
public class InsertIntegrationTest {
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
    public void testInsert() throws SQLException {
        Insert insertA =
            Insert.into("A")
                  .columns("a_id", "nu", "bo", "da", "tim", "tis")
                  .values(1L, 12.6, true, "1975-07-19", "14:15:22", "2012-12-25 13:05:12")
                  .values(2L, 13.6, false, "1976-10-16", "14:15:23", "2012-12-25 13:05:13")
                  .withDefaultValue("va", "hello")
                  .withGeneratedValue("seq", ValueGenerators.sequence().startingAt(10L).incrementingBy(5))
                  .build();
        Insert insertB1 =
            Insert.into("B")
                  .columns("b_id", "a_id", "va")
                  .values("1", 1, new Foo("foo"))
                  .withBinder(new FooBinder(), "va")
                  .build();
        Insert insertB2 =
            Insert.into("B")
                  .columns("b_id", "a_id", "va")
                  .values(2, TestEnum.BAT, TestEnum.BAR)
                  .build();
        new DbSetup(Database.DESTINATION, Operations.sequenceOf(insertA, insertB1, insertB2)).launch();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from A order by a_id");
        assertTrue(rs.next());
        assertEquals(1L, rs.getLong("a_id"));
        assertTrue(new BigDecimal("12.6").compareTo(rs.getBigDecimal("nu")) == 0);
        assertEquals(true, rs.getBoolean("bo"));
        assertEquals(Date.valueOf("1975-07-19"), rs.getDate("da"));
        assertEquals(Time.valueOf("14:15:22"), rs.getTime("tim"));
        assertEquals(Timestamp.valueOf("2012-12-25 13:05:12"), rs.getTimestamp("tis"));
        assertEquals("hello", rs.getString("va"));
        assertEquals(10L, rs.getLong("seq"));

        assertTrue(rs.next());
        assertEquals(2L, rs.getLong("a_id"));
        assertTrue(new BigDecimal("13.6").compareTo(rs.getBigDecimal("nu")) == 0);
        assertEquals(false, rs.getBoolean("bo"));
        assertEquals(Date.valueOf("1976-10-16"), rs.getDate("da"));
        assertEquals(Time.valueOf("14:15:23"), rs.getTime("tim"));
        assertEquals(Timestamp.valueOf("2012-12-25 13:05:13"), rs.getTimestamp("tis"));
        assertEquals("hello", rs.getString("va"));
        assertEquals(15L, rs.getLong("seq"));

        rs = stmt.executeQuery("select * from B order by b_id");
        assertTrue(rs.next());
        assertEquals(1L, rs.getLong("b_id"));
        assertEquals(1L, rs.getLong("a_id"));
        assertEquals("foo", rs.getString("va"));

        assertTrue(rs.next());
        assertEquals(2L, rs.getLong("b_id"));
        assertEquals(2L, rs.getLong("a_id"));
        assertEquals("BAR", rs.getString("va"));
    }

    @Test
    public void testWithoutMetadata() throws SQLException {
        Insert insertA =
            Insert.into("A")
                  .columns("a_id", "nu", "bo", "da", "tim", "tis")
                  .values(TestEnum.BAT, "13.6", false, "1976-10-16", "14:15:23", "2012-12-25 13:05:13")
                  .withDefaultValue("va", "hello")
                  .useMetadata(false)
                  .build();
        try {
            new DbSetup(Database.DESTINATION, insertA).launch();
            fail("expected an exception due to the metadata not being used");
        }
        catch (DbSetupRuntimeException e) {
            // expected
        }

        insertA =
            Insert.into("A")
                  .columns("a_id", "nu", "bo", "da", "tim", "tis")
                  .values("2", "13.6", false, "1976-10-16", "14:15:23", "2012-12-25 13:05:13")
                  .withDefaultValue("va", "hello")
                  .useMetadata(false)
                  .withBinder(Binders.integerBinder(), "a_id")
                  .withBinder(Binders.decimalBinder(), "nu")
                  .withBinder(Binders.dateBinder(), "da")
                  .withBinder(Binders.timeBinder(), "tim")
                  .withBinder(Binders.timestampBinder(), "tis")
                  .build();
        new DbSetup(Database.DESTINATION, insertA).launch();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from A order by a_id");
        assertTrue(rs.next());
        assertEquals(2L, rs.getLong("a_id"));
        assertTrue(new BigDecimal("13.6").compareTo(rs.getBigDecimal("nu")) == 0);
        assertEquals(false, rs.getBoolean("bo"));
        assertEquals(Date.valueOf("1976-10-16"), rs.getDate("da"));
        assertEquals(Time.valueOf("14:15:23"), rs.getTime("tim"));
        assertEquals(Timestamp.valueOf("2012-12-25 13:05:13"), rs.getTimestamp("tis"));
        assertEquals("hello", rs.getString("va"));
    }

    private static class Foo {
        private String label;

        public Foo(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static class FooBinder implements Binder {

        @Override
        public void bind(PreparedStatement statement, int param, Object value) throws SQLException {
            statement.setString(param, ((Foo) value).getLabel());
        }

    }

    private enum TestEnum {
        BAR,
        BAZ,
        BAT;
    }
}
