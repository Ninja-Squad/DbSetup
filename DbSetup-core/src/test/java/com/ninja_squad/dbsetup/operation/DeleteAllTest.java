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
public class DeleteAllTest {

    @Test
    public void fromWorks() throws SQLException {
        testFrom(DeleteAll.from("A", "B"));
        testFrom(DeleteAll.from(Arrays.asList("A", "B")));
        testFrom(Operations.deleteAllFrom("A", "B"));
        testFrom(Operations.deleteAllFrom(Arrays.asList("A", "B")));
    }

    @Test
    public void toStringWorks() {
        assertEquals("delete from A", DeleteAll.from("A").toString());
    }

    @Test
    public void equalsAndHashCodeWork() {
        assertEquals(DeleteAll.from("A"), Operations.deleteAllFrom("A"));
        assertEquals(DeleteAll.from("A").hashCode(), DeleteAll.from("A").hashCode());
        assertFalse(DeleteAll.from("A").equals(DeleteAll.from("B")));
        assertFalse(DeleteAll.from("A").equals(null));
        assertFalse(DeleteAll.from("A").equals("hello"));
        DeleteAll a = DeleteAll.from("A");
        assertEquals(a, a);
    }

    private void testFrom(Operation deleteAllFromAandB) throws SQLException {
        Connection connection = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        when(connection.createStatement()).thenReturn(stmt);
        deleteAllFromAandB.execute(connection, DefaultBinderConfiguration.INSTANCE);
        InOrder inOrder = inOrder(stmt);
        inOrder.verify(stmt).executeUpdate("delete from A");
        inOrder.verify(stmt).close();
        inOrder.verify(stmt).executeUpdate("delete from B");
        inOrder.verify(stmt).close();
    }
}
