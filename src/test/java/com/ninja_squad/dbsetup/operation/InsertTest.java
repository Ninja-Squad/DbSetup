package com.ninja_squad.dbsetup.operation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;
import org.mockito.InOrder;

import com.ninja_squad.dbsetup.bind.Binder;
import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.bind.Binders;

/**
 * @author JB Nizet
 */
public class InsertTest {

    @Test
    public void insertWorks() throws SQLException {
        Binder aBinder = mock(Binder.class);
        Binder bBinder = mock(Binder.class);
        Binder cBinder = mock(Binder.class);
        Binder dBinder = mock(Binder.class);

        Connection connection = mock(Connection.class);
        BinderConfiguration config = mock(BinderConfiguration.class);
        ParameterMetaData metadata = mock(ParameterMetaData.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("insert into A (a, b, c, d) values (?, ?, ?, ?)")).thenReturn(statement);
        when(statement.getParameterMetaData()).thenReturn(metadata);
        when(config.getBinder(metadata, 1)).thenReturn(aBinder);
        when(config.getBinder(metadata, 3)).thenReturn(cBinder);

        Insert insert = Insert.into("A")
                              .columns("a", "b")
                              .values("a1", "b1")
                              .values("a2", "b2")
                              .withDefaultValue("c", "c3")
                              .withDefaultValue("d", "d4")
                              .withBinder(bBinder, "b")
                              .withBinder(dBinder, "d")
                              .build();
        insert.execute(connection, config);

        InOrder inOrder = inOrder(aBinder, bBinder, cBinder, dBinder, statement);
        inOrder.verify(aBinder).bind(statement, 1, "a1");
        inOrder.verify(bBinder).bind(statement, 2, "b1");
        inOrder.verify(cBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(aBinder).bind(statement, 1, "a2");
        inOrder.verify(bBinder).bind(statement, 2, "b2");
        inOrder.verify(cBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).close();
    }

    @Test
    public void insertWorksWithoutMetadata() throws SQLException {
        Binder bBinder = mock(Binder.class);
        Binder dBinder = mock(Binder.class);

        Connection connection = mock(Connection.class);
        BinderConfiguration config = mock(BinderConfiguration.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement("insert into A (a, b, c, d) values (?, ?, ?, ?)")).thenReturn(statement);

        Insert insert = Insert.into("A")
                              .columns("a", "b")
                              .values("a1", "b1")
                              .values("a2", "b2")
                              .withDefaultValue("c", "c3")
                              .withDefaultValue("d", "d4")
                              .withBinder(bBinder, "b")
                              .withBinder(dBinder, "d")
                              .useMetadata(false)
                              .build();
        insert.execute(connection, config);

        InOrder inOrder = inOrder(bBinder, dBinder, statement);
        inOrder.verify(statement).setObject(1, "a1");
        inOrder.verify(bBinder).bind(statement, 2, "b1");
        inOrder.verify(statement).setObject(3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).setObject(1, "a2");
        inOrder.verify(bBinder).bind(statement, 2, "b2");
        inOrder.verify(statement).setObject(3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).close();
    }

    @Test
    public void toStringWorks() {
        Insert insert = Insert.into("A")
                              .columns("a", "b")
                              .values("a1", "b1")
                              .values("a2", "b2")
                              .withDefaultValue("c", "c3")
                              .withDefaultValue("d", "d4")
                              .withBinder(Binders.decimalBinder(), "b")
                              .withBinder(Binders.dateBinder(), "d")
                              .useMetadata(false)
                              .build();
        assertNotNull(insert.toString());
    }

    @Test
    public void equalsAndHashCodeWork() {
        Insert insertA = Insert.into("A")
                               .columns("a", "b")
                               .values("a1", "b1")
                               .withDefaultValue("c", "c3")
                               .withBinder(Binders.decimalBinder(), "b")
                               .useMetadata(false)
                               .build();
        Insert insertB = Insert.into("A")
                               .columns("a", "b")
                               .values("a1", "b1")
                               .withDefaultValue("c", "c3")
                               .withBinder(Binders.decimalBinder(), "b")
                               .useMetadata(false)
                               .build();
        assertEquals(insertA, insertA);
        assertEquals(insertA, insertB);
        assertEquals(insertA.hashCode(), insertB.hashCode());
        assertFalse(insertA.equals(null));
        assertFalse(insertA.equals("hello"));

        insertB = Insert.into("A")
                        .columns("d", "b")
                        .values("a1", "b1")
                        .withDefaultValue("c", "c3")
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b2")
                        .withDefaultValue("c", "c3")
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .withDefaultValue("c", "c4")
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .withDefaultValue("c", "c3")
                        .withBinder(Binders.integerBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .withDefaultValue("c", "c3")
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(true)
                        .build();
        assertFalse(insertA.equals(insertB));
    }
}
