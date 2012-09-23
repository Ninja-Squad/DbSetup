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

package com.ninja_squad.dbsetup.bind;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class BindersTest {

    private PreparedStatement stmt;

    @Before
    public void prepare() {
        stmt = mock(PreparedStatement.class);
    }

    @Test
    public void defaultBinderBindsObject() throws SQLException {
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, Boolean.TRUE);
        verify(stmt).setObject(1, Boolean.TRUE);
    }

    @Test
    public void defaultBinderBindsNull() throws SQLException {
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    @Test
    public void stringBinderBindsString() throws SQLException {
        Binder binder = Binders.stringBinder();
        binder.bind(stmt, 1, "hello");
        verify(stmt).setString(1, "hello");
    }

    @Test
    public void stringBinderBindsEnum() throws SQLException {
        Binder binder = Binders.stringBinder();
        binder.bind(stmt, 1, TestEnum.BAR);
        verify(stmt).setString(1, "BAR");
    }

    @Test
    public void stringBinderBindsObject() throws SQLException {
        Binder binder = Binders.stringBinder();
        binder.bind(stmt, 1, new Foo());
        verify(stmt).setString(1, "foo");
    }

    @Test
    public void stringBinderBindsNull() throws SQLException {
        Binder binder = Binders.stringBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    @Test
    public void dateBinderBindsSqlDate() throws SQLException {
        Date date = Date.valueOf("1975-07-19");
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, date);
        verify(stmt).setDate(1, date);
    }

    @Test
    public void dateBinderBindsUtilDate() throws SQLException {
        java.util.Date date = new java.util.Date(Date.valueOf("1975-07-19").getTime());
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, date);
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"));
    }

    @Test
    public void dateBinderBindsCalendar() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Date.valueOf("1975-07-19").getTime());
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, calendar);
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"));
    }

    @Test
    public void dateBinderBindsString() throws SQLException {
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, "1975-07-19");
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"));
    }

    @Test
    public void dateBinderBindsNull() throws SQLException {
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    @Test
    public void dateBinderBindsObject() throws SQLException {
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, Boolean.TRUE);
        verify(stmt).setObject(1, Boolean.TRUE);
    }

    @Test
    public void timestampBinderBindsTimestamp() throws SQLException {
        Timestamp ts = Timestamp.valueOf("1975-07-19 13:14:15");
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, ts);
        verify(stmt).setTimestamp(1, ts);
    }

    @Test
    public void timestampBinderBindsUtilDate() throws SQLException {
        Timestamp ts = Timestamp.valueOf("1975-07-19 13:14:15");
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, new java.util.Date(ts.getTime()));
        verify(stmt).setTimestamp(1, ts);
    }

    @Test
    public void timestampBinderBindsCalendar() throws SQLException {
        Timestamp ts = Timestamp.valueOf("1975-07-19 13:14:15");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts.getTime());
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, calendar);
        verify(stmt).setTimestamp(1, ts);
    }

    @Test
    public void timestampBinderBindsStringWithTimestampFormat() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, "1975-07-19 13:14:15");
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 13:14:15"));
    }

    @Test
    public void timestampBinderBindsStringWithDateFormat() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, "1975-07-19");
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 00:00:00"));
    }

    @Test
    public void timestampBinderBindsNull() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    @Test
    public void timestampBinderBindsObject() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, Boolean.TRUE);
        verify(stmt).setObject(1, Boolean.TRUE);
    }

    @Test
    public void timeBinderBindsTime() throws SQLException {
        Time time = Time.valueOf("13:14:15");
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, time);
        verify(stmt).setTime(1, time);
    }

    @Test
    public void timeBinderBindsUtilDate() throws SQLException {
        Time time = Time.valueOf("13:14:15");
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, new java.util.Date(time.getTime()));
        verify(stmt).setTime(1, time);
    }

    @Test
    public void timeBinderBindsCalendar() throws SQLException {
        Time time = Time.valueOf("13:14:15");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.getTime());
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, calendar);
        verify(stmt).setTime(1, time);
    }

    @Test
    public void timeBinderBindsString() throws SQLException {
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, "13:14:15");
        verify(stmt).setTime(1, Time.valueOf("13:14:15"));
    }

    @Test
    public void timeBinderBindsNull() throws SQLException {
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    @Test
    public void timeBinderBindsObject() throws SQLException {
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, Boolean.TRUE);
        verify(stmt).setObject(1, Boolean.TRUE);
    }

    @Test
    public void decimalBinderBindsString() throws SQLException {
        Binder binder = Binders.decimalBinder();
        binder.bind(stmt, 1, "12.45");
        verify(stmt).setBigDecimal(1, new BigDecimal("12.45"));
    }

    @Test
    public void decimalBinderBindsObject() throws SQLException {
        Binder binder = Binders.decimalBinder();
        binder.bind(stmt, 1, 12.45);
        verify(stmt).setObject(1, 12.45);
    }

    @Test
    public void decimalBinderBindsNull() throws SQLException {
        Binder binder = Binders.decimalBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    @Test
    public void integerBinderBindsBigInteger() throws SQLException {
        Binder binder = Binders.integerBinder();
        binder.bind(stmt, 1, new BigInteger("12"));
        verify(stmt).setObject(1, new BigInteger("12"), Types.BIGINT);
    }

    @Test
    public void integerBinderBindsString() throws SQLException {
        Binder binder = Binders.integerBinder();
        binder.bind(stmt, 1, "12");
        verify(stmt).setObject(1, new BigInteger("12"), Types.BIGINT);
    }

    @Test
    public void integerBinderBindsEnum() throws SQLException {
        Binder binder = Binders.integerBinder();
        binder.bind(stmt, 1, TestEnum.FOO);
        verify(stmt).setInt(1, 0);
    }

    @Test
    public void integerBinderBindsObject() throws SQLException {
        Binder binder = Binders.integerBinder();
        binder.bind(stmt, 1, 27);
        verify(stmt).setObject(1, 27);
    }

    @Test
    public void integerBinderBindsNull() throws SQLException {
        Binder binder = Binders.integerBinder();
        binder.bind(stmt, 1, null);
        verify(stmt).setObject(1, null);
    }

    private enum TestEnum {
        FOO, BAR;
    }

    private static class Foo {
        @Override
        public String toString() {
            return "foo";
        }
    }
}
