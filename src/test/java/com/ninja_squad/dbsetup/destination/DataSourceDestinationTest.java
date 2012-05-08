package com.ninja_squad.dbsetup.destination;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
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
        Destination dest1bis = new DataSourceDestination(dataSource1);
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
