/*
 * The MIT License
 *
 * Copyright (c) 2013, Ninja Squad
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

package com.ninja_squad.dbsetup.generator;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * @author JB
 */
public class DateSequenceValueGeneratorTest {
    @Test
    public void startsAtToday() {
        ZonedDateTime date = ValueGenerators.dateSequence().nextValue();
        assertEquals(LocalDate.now().atStartOfDay(ZoneId.systemDefault()), date);
    }

    @Test
    public void incrementsByOneDay() throws ParseException {
        DateSequenceValueGenerator sequence = ValueGenerators.dateSequence().startingAt(july19Of2013AtMidnight());
        sequence.nextValue();
        ZonedDateTime date = sequence.nextValue();
        assertEquals(LocalDateTime.parse("2013-07-20T00:00:00.000").atZone(ZoneId.systemDefault()), date);
    }

    @Test
    public void allowsSettingNewStartAsDate() throws ParseException {
        DateSequenceValueGenerator sequence = ValueGenerators.dateSequence().startingAt(july19Of2013AtMidnight());
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        sequence.startingAt(july19Of1975AtMidnight());
        assertEquals("1975-07-19T00:00:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewStartAsDateWithTimeZone() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnightInParisTimeZone(), TimeZone.getTimeZone("UTC"));
        assertEquals("2013-07-18T22:00:00.000", toLongStringInUTC(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewStartAsString() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt("2013-07-19");
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewStartAsCalendar() throws ParseException {
        Calendar start = Calendar.getInstance();
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence().startingAt(start);
        assertEquals(start.getTime().getTime(), sequence.nextValue().toInstant().toEpochMilli());
    }

    @Test
    public void allowsSettingNewStartAsLocalDate() throws ParseException {
        LocalDate start = LocalDate.parse("2000-01-01");
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence().startingAt(start);
        assertEquals(start.atStartOfDay(ZoneId.systemDefault()), sequence.nextValue());
    }

    @Test
    public void allowsSettingNewStartAsLocalDateTime() throws ParseException {
        LocalDateTime start = LocalDateTime.parse("2000-01-01T01:02:03.000");
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence().startingAt(start);
        assertEquals(start.atZone(ZoneId.systemDefault()), sequence.nextValue());
    }

    @Test
    public void allowsSettingNewStartAsZonedDateTime() throws ParseException {
        ZonedDateTime start = ZonedDateTime.parse("2000-01-01T01:02:03.000Z");
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence().startingAt(start);
        assertEquals(start, sequence.nextValue());
    }

    @Test
    public void allowsSettingNewIncrement() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(2, DateSequenceValueGenerator.CalendarField.DAY);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2013-07-21T00:00:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInYears() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.YEAR);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2014-07-19T00:00:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInMonths() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.MONTH);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2013-08-19T00:00:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInHours() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.HOUR);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19T01:00:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInMinutes() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.MINUTE);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19T00:01:00.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInSeconds() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.SECOND);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19T00:00:01.000", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInMilliseconds() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.MILLISECOND);
        assertEquals("2013-07-19T00:00:00.000", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19T00:00:00.001", toLongString(sequence.nextValue()));
    }

    private String toLongString(ZonedDateTime date) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);
    }

    private String toLongStringInUTC(ZonedDateTime date) {
        return toLongString(date.withZoneSameInstant(ZoneOffset.UTC));
    }

    private Date july19Of2013AtMidnight() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse("2013-07-19");
    }

    private Date july19Of1975AtMidnight() throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse("1975-07-19");
    }

    // offset is +02:00 in Paris at this date
    private Date july19Of2013AtMidnightInParisTimeZone() throws ParseException {
        TimeZone zone = TimeZone.getTimeZone("Europe/Paris");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(zone);
        return simpleDateFormat.parse("2013-07-19");
    }
}
