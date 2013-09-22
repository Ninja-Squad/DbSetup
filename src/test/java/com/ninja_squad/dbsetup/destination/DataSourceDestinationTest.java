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

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author JB Nizet
 */
public class DataSourceDestinationTest {
    @Test
    public void getConnectionWorks() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        assertSame(connection, new DataSourceDestination(dataSource).getConnection());
    }

    @Test
    public void equalsAndHashCodeWork() throws SQLException {
        DataSource dataSource1 = mock(DataSource.class);
        DataSource dataSource2 = mock(DataSource.class);
        Destination dest1 = new DataSourceDestination(dataSource1);
        Destination dest1bis = DataSourceDestination.with(dataSource1);
        Destination dest2 = new DataSourceDestination(dataSource2);

        assertEquals(dest1, dest1);
        assertEquals(dest1, dest1bis);
        assertEquals(dest1.hashCode(), dest1bis.hashCode());
        assertFalse(dest1.equals(dest2));
        assertFalse(dest1.equals(null));
        assertFalse(dest1.equals("hello"));
    }

    @Test
    public void toStringWorks() {
        DataSource dataSource1 = mock(DataSource.class);
        when(dataSource1.toString()).thenReturn("dataSource1");
        assertEquals("DataSourceDestination [dataSource=dataSource1]",
                     new DataSourceDestination(dataSource1).toString());
    }
}
