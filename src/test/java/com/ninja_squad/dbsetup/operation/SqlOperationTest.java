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
