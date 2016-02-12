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

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
    public void defaultBinderBindsEnum() throws SQLException {
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, TestEnum.BAR);
        verify(stmt).setString(1, TestEnum.BAR.name());
    }

    @Test
    public void defaultBinderBindsUtilDate() throws SQLException {
        java.util.Date date = new java.util.Date(Date.valueOf("1975-07-19").getTime());
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, date);
        verify(stmt).setTimestamp(1, new Timestamp(date.getTime()));
    }

    @Test
    public void defaultBinderBindsCalendar() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Date.valueOf("1975-07-19").getTime());
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, calendar);
        verify(stmt).setTimestamp(1, new Timestamp(calendar.getTime().getTime()), calendar);
    }

    @Test
    public void defaultBinderBindsLocalDate() throws SQLException {
        LocalDate localDate = LocalDate.parse("1975-07-19");
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, localDate);
        verify(stmt).setDate(1, Date.valueOf(localDate));
    }

    @Test
    public void defaultBinderBindsLocalTime() throws SQLException {
        LocalTime localTime = LocalTime.parse("01:02:03.000");
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, localTime);
        verify(stmt).setTime(1, Time.valueOf("01:02:03"));
    }

    @Test
    public void defaultBinderBindsLocalDateTime() throws SQLException {
        LocalDateTime localDateTime = LocalDateTime.parse("1975-07-19T01:02:03.000");
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, localDateTime);
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 01:02:03"));
    }

    @Test
    public void defaultBinderBindsInstant() throws SQLException {
        Instant instant = LocalDateTime.parse("1975-07-19T01:02:03.000").atZone(ZoneId.systemDefault()).toInstant();
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, instant);
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 01:02:03"));
    }

    @Test
    public void defaultBinderBindsZonedDateTime() throws SQLException {
        ZonedDateTime zonedDateTime = LocalDateTime.parse("1975-07-19T01:02:03.000").atZone(ZoneOffset.UTC);
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, zonedDateTime);
        verify(stmt).setTimestamp(eq(1),
                                  eq(Timestamp.from(zonedDateTime.toInstant())),
                                  calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
    }

    @Test
    public void defaultBinderBindsOffsetDateTime() throws SQLException {
        OffsetDateTime offsetDateTime = LocalDateTime.parse("1975-07-19T01:02:03.000").atOffset(ZoneOffset.UTC);
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, offsetDateTime);
        verify(stmt).setTimestamp(eq(1),
                                  eq(Timestamp.from(offsetDateTime.toInstant())),
                                  calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
    }

    @Test
    public void defaultBinderBindsOffsetTime() throws SQLException {
        OffsetTime offsetTime = LocalTime.parse("01:02:03.000").atOffset(ZoneOffset.UTC);
        Binder binder = Binders.defaultBinder();
        binder.bind(stmt, 1, offsetTime);
        verify(stmt).setTime(eq(1),
                             eq(Time.valueOf(offsetTime.toLocalTime())),
                             calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
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
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"), calendar);
    }

    @Test
    public void dateBinderBindsString() throws SQLException {
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, "1975-07-19");
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"));
    }

    @Test
    public void dateBinderBindsLocalDate() throws SQLException {
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, LocalDate.parse("1975-07-19"));
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"));
    }

    @Test
    public void dateBinderBindsLocalDateTime() throws SQLException {
        Binder binder = Binders.dateBinder();
        binder.bind(stmt, 1, LocalDateTime.parse("1975-07-19T01:02:03.000"));
        verify(stmt).setDate(1, Date.valueOf("1975-07-19"));
    }

    @Test
    public void dateBinderBindsInstant() throws SQLException {
        Binder binder = Binders.dateBinder();
        Instant value = LocalDateTime.parse("1975-07-19T01:02:03.000").atZone(ZoneId.systemDefault()).toInstant();
        binder.bind(stmt, 1, value);
        verify(stmt).setDate(1, new Date(value.toEpochMilli()));
    }

    @Test
    public void dateBinderBindsZonedDateTime() throws SQLException {
        Binder binder = Binders.dateBinder();
        ZonedDateTime value = LocalDateTime.parse("1975-07-19T01:02:03.000").atZone(ZoneOffset.UTC);
        binder.bind(stmt, 1, value);
        verify(stmt).setDate(eq(1),
                             eq(new Date(value.toInstant().toEpochMilli())),
                             calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
    }

    @Test
    public void dateBinderBindsOffsetDateTime() throws SQLException {
        Binder binder = Binders.dateBinder();
        OffsetDateTime value = LocalDateTime.parse("1975-07-19T01:02:03.000").atOffset(ZoneOffset.UTC);
        binder.bind(stmt, 1, value);
        verify(stmt).setDate(eq(1),
                             eq(new Date(value.toInstant().toEpochMilli())),
                             calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
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
        verify(stmt).setTimestamp(1, ts, calendar);
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
    public void timestampBinderBindsLocalDateTime() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, LocalDateTime.parse("1975-07-19T01:02:03.000"));
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 01:02:03"));
    }

    @Test
    public void timestampBinderBindsLocalDate() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, LocalDate.parse("1975-07-19"));
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 00:00:00"));
    }

    @Test
    public void timestampBinderBindsInstant() throws SQLException {
        Binder binder = Binders.timestampBinder();
        binder.bind(stmt, 1, LocalDateTime.parse("1975-07-19T01:02:03.000").atZone(ZoneId.systemDefault()).toInstant());
        verify(stmt).setTimestamp(1, Timestamp.valueOf("1975-07-19 01:02:03"));
    }

    @Test
    public void timestampBinderBindsZonedDateTime() throws SQLException {
        Binder binder = Binders.timestampBinder();
        ZonedDateTime value = LocalDateTime.parse("1975-07-19T01:02:03.000").atZone(ZoneOffset.UTC);
        binder.bind(stmt, 1, value);
        verify(stmt).setTimestamp(eq(1),
                                  eq(Timestamp.from(value.toInstant())),
                                  calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
    }

    @Test
    public void timestampBinderBindsOffsetDateTime() throws SQLException {
        Binder binder = Binders.timestampBinder();
        OffsetDateTime value = LocalDateTime.parse("1975-07-19T01:02:03.000").atOffset(ZoneOffset.UTC);
        binder.bind(stmt, 1, value);
        verify(stmt).setTimestamp(eq(1),
                                  eq(Timestamp.from(value.toInstant())),
                                  calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
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
        verify(stmt).setTime(1, time, calendar);
    }

    @Test
    public void timeBinderBindsString() throws SQLException {
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, "13:14:15");
        verify(stmt).setTime(1, Time.valueOf("13:14:15"));
    }

    @Test
    public void timeBinderBindsLocalTime() throws SQLException {
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, LocalTime.parse("01:02:03.000"));
        verify(stmt).setTime(1, Time.valueOf("01:02:03"));
    }

    @Test
    public void timeBinderBindsOffsetTime() throws SQLException {
        Binder binder = Binders.timeBinder();
        binder.bind(stmt, 1, OffsetTime.of(LocalTime.parse("01:02:03.000"), ZoneOffset.UTC));
        verify(stmt).setTime(eq(1),
                             eq(Time.valueOf("01:02:03")),
                             calendarWithTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)));
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
        verify(stmt).setObject(1, "12", Types.BIGINT);
    }

    @Test
    public void integerBinderBindsString() throws SQLException {
        Binder binder = Binders.integerBinder();
        binder.bind(stmt, 1, "12");
        verify(stmt).setObject(1, "12", Types.BIGINT);
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

    private static Calendar calendarWithTimeZone(TimeZone timeZone) {
        return argThat(new BaseMatcher<Calendar>() {
            @Override
            public boolean matches(Object item) {
                return (item instanceof Calendar) && ((Calendar) item).getTimeZone().equals(timeZone);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a calendar with timezone " + timeZone);
            }
        });
    }
}
