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
package com.ninja_squad.dbsetup.destination;

import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionDestinationTest {

    @Test
    public void getConnectionWorks() throws SQLException {
        Connection connection = mock(Connection.class);
        assertSame(connection, new ConnectionDestination(connection).getConnection());
    }

    @Test
    public void equalsAndHashCodeWork() throws SQLException {
        Connection connection1 = mock(Connection.class);
        Destination dest1 = new ConnectionDestination(connection1);
        Destination dest1bis = new ConnectionDestination(connection1);
        Connection connection2 = mock(Connection.class);
        Destination dest2 = new ConnectionDestination(connection2);

        assertEquals(dest1, dest1);
        assertEquals(dest1, dest1bis);
        assertEquals(dest1.hashCode(), dest1bis.hashCode());
        assertFalse(dest1.equals(dest2));
        assertFalse(dest1.equals(null));
        assertFalse(dest1.equals("hello"));
    }

    @Test
    public void toStringWorks() {
        Connection connection1 = mock(Connection.class);
        when(connection1.toString()).thenReturn("connection1");
        assertEquals("ConnectionDestination [dataSource=connection1]",
                new ConnectionDestination(connection1).toString());
    }
}
