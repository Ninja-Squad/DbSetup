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
