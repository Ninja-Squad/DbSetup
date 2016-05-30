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
public class TruncateTest {

    @Test
    public void tablesWorks() throws SQLException {
        testFrom(Truncate.tables("A", "B"));
        testFrom(Truncate.tables(Arrays.asList("A", "B")));
        testFrom(Operations.truncate("A", "B"));
        testFrom(Operations.truncate(Arrays.asList("A", "B")));
    }

    @Test
    public void toStringWorks() {
        assertEquals("truncate table A", Truncate.tables("A").toString());
    }

    @Test
    public void equalsAndHashCodeWork() {
        assertEquals(Truncate.table("A"), Operations.truncate("A"));
        assertEquals(Truncate.table("A").hashCode(), Truncate.table("A").hashCode());
        assertFalse(Truncate.table("A").equals(Truncate.table("B")));
        assertFalse(Truncate.table("A").equals(null));
        assertFalse(Truncate.table("A").equals("hello"));
        Truncate a = Truncate.table("A");
        assertEquals(a, a);
    }

    private void testFrom(Operation truncateAandB) throws SQLException {
        Connection connection = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        when(connection.createStatement()).thenReturn(stmt);
        truncateAandB.execute(connection, DefaultBinderConfiguration.INSTANCE);
        InOrder inOrder = inOrder(stmt);
        inOrder.verify(stmt).executeUpdate("truncate table A");
        inOrder.verify(stmt).close();
        inOrder.verify(stmt).executeUpdate("truncate table B");
        inOrder.verify(stmt).close();
    }
}
