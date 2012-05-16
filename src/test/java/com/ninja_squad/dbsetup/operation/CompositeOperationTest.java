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
    public void sequenceOfWorksWhenNop() throws SQLException {
        testNoArgOpWorks(CompositeOperation.sequenceOf());
        testNoArgOpWorks(CompositeOperation.sequenceOf(Collections.<Operation>emptyList()));
        testNoArgOpWorks(Operations.sequenceOf());
        testNoArgOpWorks(Operations.sequenceOf(Collections.<Operation>emptyList()));
    }

    private void testNoArgOpWorks(Operation nop) throws SQLException {
        Connection connection = mock(Connection.class);
        nop.execute(connection, DefaultBinderConfiguration.INSTANCE);
        assertEquals("NOP", nop.toString());
    }

    @Test
    public void sequenceOfWorksWhenSingleArg() throws SQLException {
        Operation a = mock(Operation.class);

        assertSame(a, CompositeOperation.sequenceOf(a));
        assertSame(a, CompositeOperation.sequenceOf(Collections.singletonList(a)));
        assertSame(a, Operations.sequenceOf(a));
        assertSame(a, Operations.sequenceOf(Collections.singletonList(a)));
    }

    @Test
    public void sequenceOfWorksWhenSeveralArgs() throws SQLException {
        Operation a = mock(Operation.class);
        Operation b = mock(Operation.class);

        testSequenceOfWorksWhenMultipleArgs(CompositeOperation.sequenceOf(a, b), a, b);
        testSequenceOfWorksWhenMultipleArgs(CompositeOperation.sequenceOf(Arrays.asList(a, b)), a, b);
        testSequenceOfWorksWhenMultipleArgs(Operations.sequenceOf(a, b), a, b);
        testSequenceOfWorksWhenMultipleArgs(Operations.sequenceOf(Arrays.asList(a, b)), a, b);
    }

    private void testSequenceOfWorksWhenMultipleArgs(Operation composite, Operation a, Operation b) throws SQLException {
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

        Operation c1 = CompositeOperation.sequenceOf(a, b);
        Operation c2 = CompositeOperation.sequenceOf(a, b);

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
        assertEquals("a\nb", CompositeOperation.sequenceOf(a, b).toString());
    }
}
