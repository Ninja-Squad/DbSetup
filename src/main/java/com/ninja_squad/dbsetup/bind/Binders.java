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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Utility class allowing to get various kinds of binders. The {@link DefaultBinderConfiguration} uses binders
 * returned by this class, based on the type of the parameter.
 * @author JB Nizet
 */
public final class Binders {

    private static final Binder DEFAULT_BINDER = new DefaultBinder();
    private static final Binder DATE_BINDER = new DateBinder();
    private static final Binder TIMESTAMP_BINDER = new TimestampBinder();
    private static final Binder DECIMAL_BINDER = new DecimalBinder();
    private static final Binder INTEGER_BINDER = new IntegerBinder();
    private static final Binder TIME_BINDER = new TimeBinder();
    private static final Binder STRING_BINDER = new StringBinder();

    private Binders() {
    }

    /**
     * Returns the default binder. This binder is normally used for columns of a type that is not handled by the other
     * binders. It is also used when the metadata are not used and the Insert thus doesn't know the type of the column.
     * It simply uses <code>stmt.setObject()</code> to bind the parameter, except if the value being bound is of some
     * some well-known type not handled by JDBC:
     * <ul>
     *     <li><code>enum</code>: the name of the enum is bound</li>
     *     <li><code>java.util.Date</code>: the date is transformed to a <code>java.sql.Timestamp</code></li>
     *     <li><code>java.util.Calendar</code>: the calendar is transformed to a <code>java.sql.Timestamp</code>,
     *         and is passed as third argument of
     *         <code>PreparedStatement.setTimestampt()</code> to pass the timezone</li>
     *     <li><code>java.time.LocalDate</code>: transformed to a <code>java.sql.Date</code></li>
     *     <li><code>java.time.LocalTime</code>: transformed to a <code>java.sql.Time</code></li>
     *     <li><code>java.time.LocalDateTime</code>: transformed to a <code>java.sql.Timestamp</code></li>
     *     <li><code>java.time.Instant</code>: transformed to a <code>java.sql.Timestamp</code></li>
     *     <li><code>java.time.ZonedDateTime</code> and <code>OffsetDateTime</code>: transformed to a
     *         <code>java.sql.Timestamp</code>. The time zone is also used to create a Calendar passed as third
     *         argument of <code>PreparedStatement.setTimestamp()</code> to pass the timezone</li>
     *     <li><code>java.time.OffsetTime</code>: transformed to a
     *         <code>java.sql.Time</code>. The time zone is also used to create a Calendar passed as third
     *         argument of <code>PreparedStatement.setTime()</code> to pass the timezone</li>
     * </ul>
     */
    public static Binder defaultBinder() {
        return DEFAULT_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type CHAR and VARCHAR. The returned binder supports values of type
     * <ul>
     *   <li><code>String</code></li>
     *   <li><code>enum</code>: the name of the enum is used as bound value</li>
     *   <li><code>Object</code>: the <code>toString()</code> of the object is used as bound value</li>
     * </ul>
     */
    public static Binder stringBinder() {
        return STRING_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type DATE. The returned binder supports values of type
     * <ul>
     *   <li><code>java.sql.Date</code></li>
     *   <li><code>java.util.Date</code>: the milliseconds of the date are used to construct a
     *       <code>java.sql.Date</code>.</li>
     *   <li><code>java.util.Calendar</code>: the milliseconds of the calendar are used to construct a
     *       <code>java.sql.Date</code>, and the calendar is passed as third argument of
     *       <code>PreparedStatement.setDate()</code> to pass the timezone
     *   </li>
     *   <li><code>String</code>: the string is transformed to a java.sql.Date using the <code>Date.valueOf()</code>
     *       method</li>
     *   <li><code>java.time.LocalDate</code>: transformed to a <code>java.sql.Date</code> using
     *       <code>Date.valueOf()</code></li>
     *   <li><code>java.time.LocalDateTime</code>: transformed to a LocalDate (and thus ignoring the time),
     *       and then transformed to a <code>java.sql.Date</code> using <code>Date.valueOf()</code></li>
     *   <li><code>java.time.Instant</code>the milliseconds of the instant are used to construct a
     *       <code>java.sql.Date</code>.</li>
     *   <li><code>java.time.ZonedDateTime</code> and <code>java.time.OffsetDateTime</code>: transformed to an Instant
     *       and then to a <code>java.sql.Date</code>. The time zone is also used to create a Calendar passed as third
     *       argument of <code>PreparedStatement.setDate()</code> to pass the timezone</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder dateBinder() {
        return DATE_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type TIMESTAMP and TIMESTAMP WITh TIMEZONE. The returned binder
     * supports values of type
     * <ul>
     *   <li><code>java.sql.Timestamp</code></li>
     *   <li><code>java.util.Date</code>: the milliseconds of the date are used to construct a
     *       <code>java.sql.Timestamp</code></li>
     *   <li><code>java.util.Calendar</code>: the milliseconds of the calendar are used to construct a
     *       <code>java.sql.Timestamp</code>, and the calendar is passed as third argument of
     *       <code>PreparedStatement.setTimestamp()</code> to pass the timezone</li>
     *   <li><code>String</code>: the string is transformed to a <code>java.sql.Timestamp</code> using the
     *       <code>Timestamp.valueOf()</code> method, or using the <code>java.sql.Date.valueOf()</code> method if the
     *       string has less than 19 characters</li>
     *   <li><code>java.time.LocalDateTime</code>: transformed to a <code>java.sql.Timestamp</code> using
     *       <code>Timestamp.valueOf()</code></li>
     *   <li><code>java.time.LocalDate</code>: transformed to a LocalDateTime with the time at start of day,
     *       and then transformed to a <code>java.sql.Timestamp</code> using <code>Timestamp.valueOf()</code></li>
     *   <li><code>java.time.Instant</code>: transformed to a <code>java.sql.Timestamp</code> using
     *       <code>Timestamp.from()</code></li>
     *   <li><code>java.time.ZonedDateTime</code> and <code>java.time.OffsetDateTime</code>: transformed to an Instant
     *       and then to a <code>java.sql.Timestamp</code> using <code>Timestamp.from()</code>. The time zone is also
     *       used to create a Calendar passed as third argument of
     *       <code>PreparedStatement.setTimestamp()</code> to pass the timezone</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder timestampBinder() {
        return TIMESTAMP_BINDER;
    }

    /**
     * Returns a binder suitable for columns of type TIME or TIME WITH TIME ZONE. The returned binder supports values
     * of type
     * <ul>
     *   <li><code>java.sql.Time</code></li>
     *   <li><code>java.util.Date</code>: the milliseconds of the date are used to construct a
     *      <code>java.sql.Time</code></li>
     *   <li><code>java.util.Calendar</code>: the milliseconds of the calendar are used to construct a
     *      <code>java.sql.Time</code>, and the calendar is passed as third argument of
     *      <code>PreparedStatement.setTimestamp()</code> to pass the timezone
     *   </li>
     *   <li><code>String</code>: the string is transformed to a java.sql.Time using the
     *       <code>Time.valueOf()</code> method</li>
     *   <li><code>java.time.LocalTime</code>: transformed to a <code>java.sql.Time</code> using
     *       <code>Time.valueOf()</code></li>
     *   <li><code>java.time.OffsetTime</code>: transformed to a LocalTime and then to a <code>java.sql.Time</code>
     *       using <code>Time.valueOf()</code>. The time zone is also
     *       used to create a Calendar passed as third argument of
     *       <code>PreparedStatement.setTime()</code> to pass the timezone</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder timeBinder() {
        return TIME_BINDER;
    }

    /**
     * Returns a binder suitable for numeric, decimal columns. The returned binder supports values of type
     * <ul>
     *   <li><code>String</code>: the string is transformed to a java.math.BigDecimal using its constructor</li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder decimalBinder() {
        return DECIMAL_BINDER;
    }

    /**
     * Returns a binder suitable for numeric, integer columns. The returned binder supports values of type
     * <ul>
     *   <li><code>BigInteger</code>: the object is transformed to a String and bound using
     *       <code>stmt.setObject()</code>, with <code>BIGINT</code> as target type.
     *   </li>
     *   <li><code>enum</code>: the enum is transformed into an integer by taking its ordinal</li>
     *   <li><code>String</code>: the string is bound using <code>stmt.setObject()</code>, with <code>BIGINT</code> as
     *       target type.
     *   </li>
     * </ul>
     * If the value is none of these types, <code>stmt.setObject()</code> is used to bind the value.
     */
    public static Binder integerBinder() {
        return INTEGER_BINDER;
    }

    /**
     * The implementation for {@link Binders#stringBinder()}
     * @author JB Nizet
     */
    private static final class StringBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof String) {
                stmt.setString(param, (String) value);
            }
            else if (value instanceof Enum<?>) {
                stmt.setString(param, ((Enum<?>) value).name());
            }
            else if (value == null) {
                stmt.setObject(param, null);
            }
            else {
                stmt.setString(param, value.toString());
            }
        }

        @Override
        public String toString() {
            return "Binders.stringBinder";
        }
    }

    /**
     * The implementation for {@link Binders#timeBinder()}
     * @author JB Nizet
     */
    private static final class TimeBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Time) {
                stmt.setTime(param, (Time) value);
            }
            else if (value instanceof java.util.Date) {
                stmt.setTime(param, new Time(((java.util.Date) value).getTime()));
            }
            else if (value instanceof Calendar) {
                Calendar calendar = (Calendar) value;
                stmt.setTime(param, new Time(calendar.getTimeInMillis()), calendar);
            }
            else if (value instanceof String) {
                stmt.setTime(param, Time.valueOf((String) value));
            }
            else if (value instanceof LocalTime) {
                stmt.setTime(param, Time.valueOf((LocalTime) value));
            }
            else if (value instanceof OffsetTime) {
                OffsetTime offsetTime = (OffsetTime) value;
                stmt.setTime(param,
                             Time.valueOf(offsetTime.toLocalTime()),
                             Calendar.getInstance(TimeZone.getTimeZone(offsetTime.getOffset())));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.timeBinder";
        }
    }

    /**
     * The implementation for {@link Binders#integerBinder()}
     * @author JB Nizet
     */
    private static final class IntegerBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof BigInteger) {
                stmt.setObject(param, value.toString(), Types.BIGINT);
            }
            else if (value instanceof Enum<?>) {
                stmt.setInt(param, ((Enum<?>) value).ordinal());
            }
            else if (value instanceof String) {
                stmt.setObject(param, value, Types.BIGINT);
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.integerBinder";
        }
    }

    /**
     * The implementation for {@link Binders#decimalBinder()}
     * @author JB Nizet
     */
    private static final class DecimalBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof String) {
                stmt.setBigDecimal(param, new BigDecimal((String) value));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.decimalBinder";
        }
    }

    /**
     * The implementation for {@link Binders#timestampBinder()}
     * @author JB Nizet
     */
    private static final class TimestampBinder implements Binder {
        // the number of chars in yyyy-mm-dd hh:mm:ss
        private static final int MIN_NUMBER_OF_CHARS_FOR_TIMESTAMP = 19;

        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Timestamp) {
                stmt.setTimestamp(param, (Timestamp) value);
            }
            else if (value instanceof java.util.Date) {
                stmt.setTimestamp(param, new Timestamp(((java.util.Date) value).getTime()));
            }
            else if (value instanceof Calendar) {
                stmt.setTimestamp(param, new Timestamp(((Calendar) value).getTimeInMillis()), (Calendar) value);
            }
            else if (value instanceof String) {
                String valueAsString = (String) value;
                if (valueAsString.length() >= MIN_NUMBER_OF_CHARS_FOR_TIMESTAMP) {
                    stmt.setTimestamp(param, Timestamp.valueOf(valueAsString));
                }
                else {
                    Date valueAsDate = Date.valueOf(valueAsString);
                    stmt.setTimestamp(param, new Timestamp(valueAsDate.getTime()));
                }
            }
            else if (value instanceof LocalDateTime) {
                LocalDateTime localDateTime = (LocalDateTime) value;
                stmt.setTimestamp(param, Timestamp.valueOf(localDateTime));
            }
            else if (value instanceof Instant) {
                Instant instant = (Instant) value;
                stmt.setTimestamp(param, Timestamp.from(instant));
            }
            else if (value instanceof ZonedDateTime) {
                ZonedDateTime zonedDateTime = (ZonedDateTime) value;
                stmt.setTimestamp(param,
                                  Timestamp.from(zonedDateTime.toInstant()),
                                  Calendar.getInstance(TimeZone.getTimeZone(zonedDateTime.getZone())));
            }
            else if (value instanceof OffsetDateTime) {
                OffsetDateTime offsetDateTime = (OffsetDateTime) value;
                stmt.setTimestamp(param,
                                  Timestamp.from(offsetDateTime.toInstant()),
                                  Calendar.getInstance(TimeZone.getTimeZone(offsetDateTime.getOffset())));
            }
            else if (value instanceof LocalDate) {
                LocalDate localDate = (LocalDate) value;
                stmt.setTimestamp(param, Timestamp.valueOf(localDate.atStartOfDay()));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.timestampBinder";
        }
    }

    /**
     * The implementation for {@link Binders#dateBinder()}
     * @author JB Nizet
     */
    private static final class DateBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Date) {
                stmt.setDate(param, (Date) value);
            }
            else if (value instanceof java.util.Date) {
                stmt.setDate(param, new Date(((java.util.Date) value).getTime()));
            }
            else if (value instanceof Calendar) {
                Calendar calendar = (Calendar) value;
                stmt.setDate(param, new Date(calendar.getTimeInMillis()), calendar);
            }
            else if (value instanceof String) {
                stmt.setDate(param, Date.valueOf((String) value));
            }
            else if (value instanceof LocalDate) {
                LocalDate localDate = (LocalDate) value;
                stmt.setDate(param, Date.valueOf(localDate));
            }
            else if (value instanceof LocalDateTime) {
                LocalDateTime localDateTime = (LocalDateTime) value;
                stmt.setDate(param, Date.valueOf(localDateTime.toLocalDate()));
            }
            else if (value instanceof Instant) {
                Instant instant = (Instant) value;
                stmt.setDate(param, new Date(instant.toEpochMilli()));
            }
            else if (value instanceof ZonedDateTime) {
                ZonedDateTime zonedDateTime = (ZonedDateTime) value;
                stmt.setDate(param,
                             new Date(zonedDateTime.toInstant().toEpochMilli()),
                             Calendar.getInstance(TimeZone.getTimeZone(zonedDateTime.getZone())));
            }
            else if (value instanceof OffsetDateTime) {
                OffsetDateTime offsetDateTime = (OffsetDateTime) value;
                stmt.setDate(param,
                             new Date(offsetDateTime.toInstant().toEpochMilli()),
                             Calendar.getInstance(TimeZone.getTimeZone(offsetDateTime.getOffset())));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.dateBinder";
        }
    }

    /**
     * The implementation for {@link Binders#defaultBinder()}
     * @author JB Nizet
     */
    private static final class DefaultBinder implements Binder {
        @Override
        public void bind(java.sql.PreparedStatement stmt, int param, Object value) throws java.sql.SQLException {
            if (value instanceof Enum) {
                stmt.setString(param, ((Enum) value).name());
            }
            else if (value instanceof java.util.Date) {
                stmt.setTimestamp(param, new Timestamp(((java.util.Date) value).getTime()));
            }
            else if (value instanceof Calendar) {
                Calendar calendar = (Calendar) value;
                stmt.setTimestamp(param, new Timestamp(calendar.getTime().getTime()), calendar);
            }
            else if (value instanceof LocalDate) {
                stmt.setDate(param, Date.valueOf((LocalDate) value));
            }
            else if (value instanceof LocalTime) {
                stmt.setTime(param, Time.valueOf((LocalTime) value));
            }
            else if (value instanceof LocalDateTime) {
                stmt.setTimestamp(param, Timestamp.valueOf((LocalDateTime) value));
            }
            else if (value instanceof Instant) {
                stmt.setTimestamp(param, Timestamp.from((Instant) value));
            }
            else if (value instanceof ZonedDateTime) {
                ZonedDateTime zonedDateTime = (ZonedDateTime) value;
                stmt.setTimestamp(param,
                                  Timestamp.from(zonedDateTime.toInstant()),
                                  Calendar.getInstance(TimeZone.getTimeZone(zonedDateTime.getZone())));
            }
            else if (value instanceof OffsetDateTime) {
                OffsetDateTime offsetDateTime = (OffsetDateTime) value;
                stmt.setTimestamp(param,
                                  Timestamp.from(offsetDateTime.toInstant()),
                                  Calendar.getInstance(TimeZone.getTimeZone(offsetDateTime.getOffset())));
            }
            else if (value instanceof OffsetTime) {
                OffsetTime offsetTime = (OffsetTime) value;
                stmt.setTime(param,
                             Time.valueOf(offsetTime.toLocalTime()),
                             Calendar.getInstance(TimeZone.getTimeZone(offsetTime.getOffset())));
            }
            else {
                stmt.setObject(param, value);
            }
        }

        @Override
        public String toString() {
            return "Binders.defaultBinder";
        }
    }
}
