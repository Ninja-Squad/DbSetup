/*
 * The MIT License
 *
 * Copyright (c) 2012-2015, Ninja Squad
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

package com.ninja_squad.dbsetup.operation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ninja_squad.dbsetup.bind.Binder;
import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.bind.Binders;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import org.junit.Test;
import org.mockito.InOrder;

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
                              .row().column("b", "b3")
                                    .column("a", "a3")
                                    .end()
                              .row().column("a", "a4")
                                    .end()
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
        inOrder.verify(aBinder).bind(statement, 1, "a3");
        inOrder.verify(bBinder).bind(statement, 2, "b3");
        inOrder.verify(cBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(aBinder).bind(statement, 1, "a4");
        inOrder.verify(bBinder).bind(statement, 2, null);
        inOrder.verify(cBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).close();
    }

    @Test
    public void insertWorksWithoutMetadata() throws SQLException {
        Binder bBinder = mock(Binder.class);
        Binder dBinder = mock(Binder.class);

        Binder defaultBinder = mock(Binder.class);

        Connection connection = mock(Connection.class);
        BinderConfiguration config = mock(BinderConfiguration.class);
        when(config.getBinder(isNull(ParameterMetaData.class), anyInt())).thenReturn(defaultBinder);
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

        InOrder inOrder = inOrder(defaultBinder, bBinder, dBinder, statement);
        inOrder.verify(defaultBinder).bind(statement, 1, "a1");
        inOrder.verify(bBinder).bind(statement, 2, "b1");
        inOrder.verify(defaultBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(defaultBinder).bind(statement, 1, "a2");
        inOrder.verify(bBinder).bind(statement, 2, "b2");
        inOrder.verify(defaultBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).close();
    }

    @Test
    public void insertWorksWhenMetadataNotSupported() throws SQLException {
        Binder defaultBinder = mock(Binder.class);

        Connection connection = mock(Connection.class);
        BinderConfiguration config = mock(BinderConfiguration.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(config.getBinder(null, 1)).thenReturn(defaultBinder);
        when(config.getBinder(null, 2)).thenReturn(defaultBinder);

        when(connection.prepareStatement("insert into A (a, b) values (?, ?)")).thenReturn(statement);

        Insert insert = Insert.into("A")
                              .columns("a", "b")
                              .values("a1", "b1")
                              .build();
        insert.execute(connection, config);

        InOrder inOrder = inOrder(statement, defaultBinder);
        inOrder.verify(defaultBinder).bind(statement, 1, "a1");
        inOrder.verify(defaultBinder).bind(statement, 2, "b1");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).close();
    }

    @Test
    public void insertWorksWhenColumnsSpecifiedByFirstRow() throws SQLException {
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
                              .row().column("a", "a1")
                                    .column("b", "b1")
                                    .end()
                              .row().column("b", "b2")
                                    .end()
                              .values("a3", "b3")
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
        inOrder.verify(aBinder).bind(statement, 1, null);
        inOrder.verify(bBinder).bind(statement, 2, "b2");
        inOrder.verify(cBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(aBinder).bind(statement, 1, "a3");
        inOrder.verify(bBinder).bind(statement, 2, "b3");
        inOrder.verify(cBinder).bind(statement, 3, "c3");
        inOrder.verify(dBinder).bind(statement, 4, "d4");
        inOrder.verify(statement).executeUpdate();
        inOrder.verify(statement).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rowBuilderColumnFailsWhenMapContainsUnknownColumnName() {
        Insert.into("A")
              .columns("a", "b")
              .row().column("c", "value of c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void valuesFailsWhenMapContainsUnknownColumnName() {
        Map<String, Object> map1 = new LinkedHashMap<String, Object>();
        map1.put("c", "value of c");
        Map<String, Object> map2 = new LinkedHashMap<String, Object>();
        map2.put("b", "value of b");
        Insert.into("A").values(map1).values(map2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void endRowFailsWhenItContainsUnknownColumnNameAndColumnNamesSpecifiedByFirstRow() {
        Insert.into("A")
              .row().column("c", "value of c").end()
              .row().column("b", "value of b").end();
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
    public void builderToStringWorks() {
        Insert.Builder builder =  Insert.into("A");

        assertNotNull(builder.toString());

        builder.columns("a", "b");

        assertNotNull(builder.toString());

        builder.values("a1", "b1")
               .values("a2", "b2");

        assertNotNull(builder.toString());

        builder.withDefaultValue("c", "c3")
               .withDefaultValue("d", "d4");

        assertNotNull(builder.toString());

        builder.withBinder(Binders.decimalBinder(), "b")
               .withBinder(Binders.dateBinder(), "d");

        assertNotNull(builder.toString());

        builder.useMetadata(false);

        assertNotNull(builder.toString());

        builder.build();

        assertNotNull(builder.toString());
    }

    @Test
    public void equalsAndHashCodeWork() {
        Insert insertA = Insert.into("A")
                               .columns("a", "b")
                               .values("a1", "b1")
                               .values("a2", "b2")
                               .withDefaultValue("c", "c3")
                               .withGeneratedValue("d", ValueGenerators.sequence())
                               .withBinder(Binders.decimalBinder(), "b")
                               .useMetadata(false)
                               .build();
        Insert insertB = Insert.into("A")
                               .columns("a", "b")
                               .values("a1", "b1")
                               .values("a2", "b2")
                               .withDefaultValue("c", "c3")
                               .withGeneratedValue("d", ValueGenerators.sequence())
                               .withBinder(Binders.decimalBinder(), "b")
                               .useMetadata(false)
                               .build();
        assertEquals(insertA, insertA);
        assertEquals(insertA, insertB);
        assertEquals(insertA.hashCode(), insertB.hashCode());
        assertFalse(insertA.equals(null));
        assertFalse(insertA.equals("hello"));

        insertB = Insert.into("A")
                        .columns("e", "b")
                        .values("a1", "b1")
                        .values("a2", "b2")
                        .withDefaultValue("c", "c3")
                        .withGeneratedValue("d", ValueGenerators.sequence())
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b2")
                        .values("a2", "b2")
                        .withDefaultValue("c", "c3")
                        .withGeneratedValue("d", ValueGenerators.sequence())
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .values("a2", "b2")
                        .withDefaultValue("c", "c4")
                        .withGeneratedValue("d", ValueGenerators.sequence())
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .values("a2", "b2")
                        .withDefaultValue("c", "c3")
                        .withGeneratedValue("d", ValueGenerators.sequence())
                        .withBinder(Binders.integerBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .values("a2", "b2")
                        .withDefaultValue("c", "c3")
                        .withGeneratedValue("d", ValueGenerators.sequence())
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(true)
                        .build();
        assertFalse(insertA.equals(insertB));

        insertB = Insert.into("A")
                        .columns("a", "b")
                        .values("a1", "b1")
                        .values("a2", "b2")
                        .withDefaultValue("c", "c3")
                        .withGeneratedValue("d", ValueGenerators.sequence().startingAt(2L))
                        .withBinder(Binders.decimalBinder(), "b")
                        .useMetadata(false)
                        .build();
        assertFalse(insertA.equals(insertB));
    }

    @Test
    public void getRowCountWorks() {
        Insert insert = Insert.into("A")
                              .columns("a", "b")
                              .values("a1", "b1")
                              .values("a2", "b2")
                              .build();
        assertEquals(2, insert.getRowCount());
    }

    @Test
    public void should_use_object_to_populate_insert() throws Exception {
        //Given
        InsertTest.ValuesObject firstObject = new ValuesObject();
        firstObject.setA("a1");
        firstObject.setB(true);
        firstObject.setC(31);
        InsertTest.ValuesObject secondObject = new ValuesObject();
        firstObject.setA("a2");
        firstObject.setB(false);
        firstObject.setC(32);
        //When
        final Insert a_using_values = Insert.into("A")
                .columns("a", "b", "c")
                .values(firstObject.getA(), firstObject.isB(), firstObject.getC())
                .values(secondObject.getA(), secondObject.isB(), secondObject.getC())
                .values("a3", null, 33)
                .build();
        final Insert a_using_object = Insert.into("A")
                .columns("a", "b", "c")
                .object(firstObject)
                .object(secondObject)
                .values("a3", null, 33)
                .build();
        //Then
        assertEquals(a_using_object, a_using_values);
    }

    @Test(expected = IllegalStateException.class)
    public void unable_to_use_object_if_object_null() throws Exception {
        //Given

        //When
        Insert.into("A")
                .columns("a", "b", "c")
                .object(null)
                .build();
        //Then
    }

    @Test(expected = IllegalArgumentException.class)
    public void unable_to_use_object_if_object_dont_have_all_columns() throws Exception {
        //Given
        InsertTest.ValuesObject object = new ValuesObject();
        object.setA("a1");
        object.setB(true);
        object.setC(31);
        //When
        Insert.into("A")
                .columns("a", "b", "c", "d")
                .object(object)
                .build();
        //Then
    }

    @Test(expected = IllegalArgumentException.class)
    public void unable_to_use_object_if_object_dont_the_same_number_of_field_as_columns() throws Exception {
        //Given
        InsertTest.ValuesObject object = new ValuesObject();
        object.setA("a1");
        object.setB(true);
        object.setC(31);
        //When
        Insert.into("A")
                .columns("d", "e", "f")
                .object(object)
                .build();
        //Then
    }


    class ValuesObject {
        private String a;
        private boolean b;
        private int c;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }
    }
}
