package com.ninja_squad.dbsetup.operation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.mockito.InOrder;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration;
public class CompositeOperationTest {
    @Test
    public void ofWorksWhenNop() throws SQLException {
        testNoArgOpWorks(CompositeOperation.of());
        testNoArgOpWorks(CompositeOperation.of(Collections.<Operation>emptyList()));
        testNoArgOpWorks(Operations.of());
        testNoArgOpWorks(Operations.of(Collections.<Operation>emptyList()));
    }

    private void testNoArgOpWorks(Operation nop) throws SQLException {
        Connection connection = mock(Connection.class);
        nop.execute(connection, DefaultBinderConfiguration.INSTANCE);
        assertEquals("NOP", nop.toString());
    }

    @Test
    public void ofWorksWhenSingleArg() throws SQLException {
        Operation a = mock(Operation.class);

        assertSame(a, CompositeOperation.of(a));
        assertSame(a, CompositeOperation.of(Collections.singletonList(a)));
        assertSame(a, Operations.of(a));
        assertSame(a, Operations.of(Collections.singletonList(a)));
    }

    @Test
    public void ofWorksWhenSeveralArgs() throws SQLException {
        Operation a = mock(Operation.class);
        Operation b = mock(Operation.class);

        testOfWorksWhenMultipleArgs(CompositeOperation.of(a, b), a, b);
        testOfWorksWhenMultipleArgs(CompositeOperation.of(Arrays.asList(a, b)), a, b);
        testOfWorksWhenMultipleArgs(Operations.of(a, b), a, b);
        testOfWorksWhenMultipleArgs(Operations.of(Arrays.asList(a, b)), a, b);
    }

    private void testOfWorksWhenMultipleArgs(Operation composite, Operation a, Operation b) throws SQLException {
        Connection connection = mock(Connection.class);
        composite.execute(connection, DefaultBinderConfiguration.INSTANCE);
        InOrder inOrder = inOrder(a, b);
        inOrder.verify(a).execute(connection, DefaultBinderConfiguration.INSTANCE);
        inOrder.verify(b).execute(connection, DefaultBinderConfiguration.INSTANCE);
    }

    @Test
    public void equalsAndHashCodeWork() {
        SqlOperation a = SqlOperation.of("A");
        SqlOperation b = SqlOperation.of("B");

        Operation c1 = CompositeOperation.of(a, b);
        Operation c2 = CompositeOperation.of(a, b);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals(c1, c1);
        assertFalse(c1.equals(null));
        assertFalse(c1.equals("hello"));
    }

    @Test
    public void toStringWorks() {
        Operation a = mock(Operation.class);
        when(a.toString()).thenReturn("a");
        Operation b = mock(Operation.class);
        when(b.toString()).thenReturn("b");
        assertEquals("a\nb", CompositeOperation.of(a, b).toString());
    }
}
