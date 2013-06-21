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

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * @author JB
 */
public class DateSequenceValueGeneratorTest {
    @Test
    public void startsAtToday() {
        Date date = ValueGenerators.dateSequence().nextValue();
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertEquals(now.get(Calendar.YEAR), calendar.get(Calendar.YEAR));
        assertEquals(now.get(Calendar.MONTH), calendar.get(Calendar.MONTH));
        assertEquals(now.get(Calendar.DATE), calendar.get(Calendar.DATE));
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        assertEquals(0, calendar.get(Calendar.SECOND));
        assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void incrementsByOneDay() throws ParseException {
        DateSequenceValueGenerator sequence = ValueGenerators.dateSequence().startingAt(july19Of2013AtMidnight());
        sequence.nextValue();
        Date date = sequence.nextValue();
        assertEquals("2013-07-20 00:00:00 0", toLongString(date));
    }

    @Test
    public void allowsSettingNewStartAsDate() throws ParseException {
        DateSequenceValueGenerator sequence = ValueGenerators.dateSequence().startingAt(july19Of2013AtMidnight());
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        sequence.startingAt(july19Of1975AtMidnight());
        assertEquals("1975-07-19 00:00:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewStartAsDateWithTimeZone() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnightInParisTimeZone(), TimeZone.getTimeZone("UTC"));
        assertEquals("2013-07-18 22:00:00 0", toLongStringInUTC(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewStartAsString() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt("2013-07-19");
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewStartAsCalendar() throws ParseException {
        Calendar start = Calendar.getInstance();
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence().startingAt(start);
        assertEquals(start.getTime(), sequence.nextValue());
    }

    @Test
    public void allowsSettingNewIncrement() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(2, DateSequenceValueGenerator.CalendarField.DAY);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2013-07-21 00:00:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInYears() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.YEAR);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2014-07-19 00:00:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInMonths() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.MONTH);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2013-08-19 00:00:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInHours() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.HOUR);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19 01:00:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInMinutes() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.MINUTE);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19 00:01:00 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInSeconds() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.SECOND);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19 00:00:01 0", toLongString(sequence.nextValue()));
    }

    @Test
    public void allowsSettingNewIncrementInMilliseconds() throws ParseException {
        DateSequenceValueGenerator sequence =
            ValueGenerators.dateSequence()
                           .startingAt(july19Of2013AtMidnight())
                           .incrementingBy(1, DateSequenceValueGenerator.CalendarField.MILLISECOND);
        assertEquals("2013-07-19 00:00:00 0", toLongString(sequence.nextValue()));
        assertEquals("2013-07-19 00:00:00 1", toLongString(sequence.nextValue()));
    }

    private String toLongString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S").format(date);
    }

    private String toLongStringInUTC(Date date) {
        TimeZone zone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        simpleDateFormat.setTimeZone(zone);
        return simpleDateFormat.format(date);
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
