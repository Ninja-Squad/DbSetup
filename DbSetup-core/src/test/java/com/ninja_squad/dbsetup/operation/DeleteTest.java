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

package com.ninja_squad.dbsetup.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import com.ninja_squad.dbsetup.Operations;

/**
 * @author R. Flores, E. Kimmel
 */
public class DeleteTest {

    private final Insert insert= Insert.into("A")
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
            .build();

    @Test
    public void fromWorksWithString() throws IOException {
        Delete d = Delete.from(insert, "a");
        assertNotNull(d);
        String query = d.toString();
        assertNotNull(query);
        assertEquals(query,"delete from A where a in (a1, a2, a3, a4)");
    }

    @Test
    public void fromWorksWithLong() throws IOException {
        Insert insert = Insert.into("A")
                .columns("a", "b")
                .values(1L, "b1")
                .values(2L, "b2")
                .row().column("b", "b3")
                      .column("a", 3L)
                      .end()
                .row().column("a", 4L)
                      .end()
                .withDefaultValue("c", "c3")
                .withDefaultValue("d", "d4")
                .build();
        Delete d = Delete.from(insert, "a");
        assertNotNull(d);
        String query = d.toString();
        assertNotNull(query);
        assertEquals(query,"delete from A where a in (1, 2, 3, 4)");
    }

    @Test
    public void fromWorksWithUUID() throws IOException {
        final UUID UUID1 = UUID.randomUUID();
        final UUID UUID2 = UUID.randomUUID();
        final UUID UUID3 = UUID.randomUUID();
        final UUID UUID4 = UUID.randomUUID();
        Insert insert = Insert.into("A")
                .columns("a", "b")
                .values(UUID1, "b1")
                .values(UUID2,"b2")
                .row().column("b", "b3")
                      .column("a", UUID3)
                      .end()
                .row().column("a",UUID4)
                      .end()
                .withDefaultValue("c", "c3")
                .withDefaultValue("d", "d4")
                .build();
        Delete d = Delete.from(insert, "a");
        assertNotNull(d);
        String query = d.toString();
        assertNotNull(query);
        final String actual = "delete from A where a in (" + UUID1 + ", " + UUID2 + ", " + UUID3 + ", " + UUID4 + ")";
        assertEquals(query, actual);
    }

    @Test
    public void fromWorksWithStringifiedUUID() throws IOException {
        final UUID UUID1 = UUID.randomUUID();
        final UUID UUID2 = UUID.randomUUID();
        final UUID UUID3 = UUID.randomUUID();
        final UUID UUID4 = UUID.randomUUID();
        Insert insert = Insert.into("A")
                .columns("a", "b")
                .values("\""+UUID1.toString()+"\"", "b1")
                .values("\""+UUID2.toString()+"\"", "b2")
                .row().column("b", "b3")
                      .column("a", "\""+UUID3.toString()+"\"")
                      .end()
                .row().column("a", "\""+UUID4.toString()+"\"")
                      .end()
                .withDefaultValue("c", "c3")
                .withDefaultValue("d", "d4")
                .build();
        Delete d = Delete.from(insert, "a");
        assertNotNull(d);
        String query = d.toString();
        assertNotNull(query);
        final String actual = "delete from A where a in (\"" + UUID1.toString() + "\", \"" + UUID2.toString() + "\", \""
                + UUID3.toString() + "\", \"" + UUID4.toString() + "\")";
        assertEquals(query, actual);
    }

    @Test(expected=NullPointerException.class)
    public void fromNullInsert() {
        Delete.from(null,"");
    }

    @Test(expected=NullPointerException.class)
    public void fromNullColumn() {
        Delete.from(insert,null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromIllegalArgumentMissingColumn() {
        Delete.from(insert,"unknownColumn");
    }

    @Test(expected=IllegalStateException.class)
    public void fromIllegalStateMissingRows() {
        Delete.from(Insert.into("B").columns("test").build(), "test");
    }

    @Test
    public void equalsAndHashCodeWork() {
        assertEquals(Delete.from(insert, "a"), Operations.deleteFrom(insert, "a"));
        assertEquals(Delete.from(insert, "a").hashCode(), Delete.from(insert, "a").hashCode());
        assertFalse(Delete.from(insert, "a").equals(Delete.from(insert,"b")));
        assertFalse(Delete.from(insert, "a").equals(null));
        assertFalse(Delete.from(insert, "a").equals("hello"));
        Delete a = Delete.from(insert, "a");
        assertEquals(a, a);
    }

    @Test
    public void toStringWorks() {
        assertEquals(Delete.from(insert, "a").toString(), "delete from A where a in (a1, a2, a3, a4)");
    }
}
