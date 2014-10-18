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

package com.ninja_squad.dbsetup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.mockito.InOrder;

import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author JB Nizet
 */
public class DbSetupTest {

    @Test
    public void launchWorks() throws SQLException {
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        when(destination.getConnection()).thenReturn(connection);
        Operation operation = mock(Operation.class);

        DbSetup setup = new DbSetup(destination, operation);
        setup.launch();
        InOrder inOrder = inOrder(destination, connection, operation);
        inOrder.verify(destination).getConnection();
        inOrder.verify(operation).execute(connection, DefaultBinderConfiguration.INSTANCE);
        inOrder.verify(connection).commit();
    }
    
    @Test
    public void launchWorksWithConnectionDestination() throws SQLException {
        Connection connection = mock(Connection.class);
        Operation operation = mock(Operation.class);

        DbSetup setup = new DbSetup(connection, operation);
        setup.launch();
        InOrder inOrder = inOrder(connection, operation);
        inOrder.verify(operation).execute(connection, DefaultBinderConfiguration.INSTANCE);
        inOrder.verify(connection).commit();
    }


    @Test
    public void launchWorksWithCustomConfiguration() throws SQLException {
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        when(destination.getConnection()).thenReturn(connection);
        Operation operation = mock(Operation.class);
        BinderConfiguration config = mock(BinderConfiguration.class);

        DbSetup setup = new DbSetup(destination, operation, config);
        setup.launch();
        InOrder inOrder = inOrder(destination, connection, operation);
        inOrder.verify(destination).getConnection();
        inOrder.verify(operation).execute(connection, config);
        inOrder.verify(connection).commit();
    }

    @Test
    public void launchRollbacksIfSQLException() throws SQLException {
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        when(destination.getConnection()).thenReturn(connection);
        Operation operation = mock(Operation.class);
        doThrow(new SQLException()).when(operation).execute(connection, DefaultBinderConfiguration.INSTANCE);

        DbSetup setup = new DbSetup(destination, operation);
        try {
            setup.launch();
            fail("Expected a DbSetupRuntimeException");
        }
        catch (DbSetupRuntimeException e) {
            // expected
        }
        InOrder inOrder = inOrder(destination, connection, operation);
        inOrder.verify(destination).getConnection();
        inOrder.verify(operation).execute(connection, DefaultBinderConfiguration.INSTANCE);
        inOrder.verify(connection).rollback();
    }

    @Test
    public void launchRollbacksIfOtherException() throws SQLException {
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        when(destination.getConnection()).thenReturn(connection);
        Operation operation = mock(Operation.class);
        doThrow(new NullPointerException()).when(operation).execute(connection, DefaultBinderConfiguration.INSTANCE);

        DbSetup setup = new DbSetup(destination, operation);
        try {
            setup.launch();
            fail("Expected a DbSetupRuntimeException");
        }
        catch (NullPointerException e) {
            // expected
        }
        InOrder inOrder = inOrder(destination, connection, operation);
        inOrder.verify(destination).getConnection();
        inOrder.verify(operation).execute(connection, DefaultBinderConfiguration.INSTANCE);
        inOrder.verify(connection).rollback();
    }

    @Test
    public void equalsAndHashCodeWork() throws SQLException {
        Destination destination1 = mock(Destination.class);
        Operation operation1 = mock(Operation.class);
        BinderConfiguration config1 = DefaultBinderConfiguration.INSTANCE;

        Destination destination2 = mock(Destination.class);
        Operation operation2 = mock(Operation.class);
        BinderConfiguration config2 = mock(BinderConfiguration.class);

        DbSetup setup1 = new DbSetup(destination1, operation1, config1);
        assertEquals(setup1, setup1);
        assertEquals(setup1, new DbSetup(destination1, operation1, config1));
        assertEquals(setup1.hashCode(), new DbSetup(destination1, operation1, config1).hashCode());

        assertFalse(setup1.equals(null));
        assertFalse(setup1.equals("hello"));
        assertFalse(setup1.equals(new DbSetup(destination2, operation1, config1)));
        assertFalse(setup1.equals(new DbSetup(destination1, operation2, config1)));
        assertFalse(setup1.equals(new DbSetup(destination1, operation1, config2)));
    }

    @Test
    public void toStringWorks() throws SQLException {
        Destination destination1 = mock(Destination.class);
        when(destination1.toString()).thenReturn("destination1");
        Operation operation1 = mock(Operation.class);
        when(operation1.toString()).thenReturn("operation1");
        BinderConfiguration config1 = mock(BinderConfiguration.class);
        when(config1.toString()).thenReturn("config1");

        DbSetup setup1 = new DbSetup(destination1, operation1, config1);
        assertEquals("DbSetup [destination=destination1, operation=operation1, binderConfiguration=config1]",
                     setup1.toString());
    }
}
