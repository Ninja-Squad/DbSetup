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

package com.ninja_squad.dbsetup.operation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.InOrder;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration;

/**
 * @author JB Nizet
 */
public class SqlOperationTest {

    @Test
    public void ofWorks() throws SQLException {
        testOf(SqlOperation.of("A", "B"));
        testOf(SqlOperation.of(Arrays.asList("A", "B")));
        testOf(Operations.sql("A", "B"));
        testOf(Operations.sql(Arrays.asList("A", "B")));
    }

    @Test
    public void toStringWorks() {
        assertEquals("A", SqlOperation.of("A").toString());
    }

    @Test
    public void equalsAndHashCodeWork() {
        assertEquals(SqlOperation.of("A"), Operations.sql("A"));
        assertEquals(SqlOperation.of("A").hashCode(), SqlOperation.of("A").hashCode());
        assertFalse(SqlOperation.of("A").equals(SqlOperation.of("B")));
        assertFalse(SqlOperation.of("A").equals(null));
        assertFalse(SqlOperation.of("A").equals("hello"));
        SqlOperation a = SqlOperation.of("A");
        assertEquals(a, a);
    }

    private void testOf(Operation aAndB) throws SQLException {
        Connection connection = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        when(connection.createStatement()).thenReturn(stmt);
        aAndB.execute(connection, DefaultBinderConfiguration.INSTANCE);
        InOrder inOrder = inOrder(stmt);
        inOrder.verify(stmt).executeUpdate("A");
        inOrder.verify(stmt).close();
        inOrder.verify(stmt).executeUpdate("B");
        inOrder.verify(stmt).close();
    }
}
