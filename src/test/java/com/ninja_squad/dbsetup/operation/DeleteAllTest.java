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
