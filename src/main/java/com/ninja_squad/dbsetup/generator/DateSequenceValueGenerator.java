/*
 * The MIT License
 *
 * Copyright (c) 2013-2016, Ninja Squad
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

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * A {@link ValueGenerator} that returns a sequence of dates, starting at a given zoned date time and incremented by a
 * given time, specified as an increment and a temporal unit.
 * @author JB
 */
public final class DateSequenceValueGenerator implements ValueGenerator<ZonedDateTime> {

    // the number of chars in yyyy-mm-dd hh:mm:ss
    private static final int MIN_NUMBER_OF_CHARS_FOR_TIMESTAMP = 19;

    /**
     * The available units for the increment of this sequence
     * @deprecated use ChronoField instead. This enum is only kept to maintain backward compatibility
     */
    @Deprecated
    public enum CalendarField {
        YEAR(ChronoUnit.YEARS),
        MONTH(ChronoUnit.MONTHS),
        DAY(ChronoUnit.DAYS),
        HOUR(ChronoUnit.HOURS),
        MINUTE(ChronoUnit.MINUTES),
        SECOND(ChronoUnit.SECONDS),
        MILLISECOND(ChronoUnit.MILLIS);

        private TemporalUnit unit;

        CalendarField(TemporalUnit unit) {
            this.unit = unit;
        }

        private TemporalUnit toTemporalUnit() {
            return unit;
        }
    }

    private ZonedDateTime next;
    private int increment;
    private TemporalUnit unit;

    DateSequenceValueGenerator() {
        this(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()), 1, ChronoUnit.DAYS);
    }

    private DateSequenceValueGenerator(ZonedDateTime next, int increment, TemporalUnit unit) {
        this.next = next;
        this.increment = increment;
        this.unit = unit;
    }

    /**
     * Restarts the sequence at the given date, in the given time zone
     * @return this instance, for chaining
     * @deprecated use one of the other <code>startingAt()</code> methods taking java.time types as argument
     */
    @Deprecated
    public DateSequenceValueGenerator startingAt(@Nonnull Date startDate, @Nonnull TimeZone timeZone) {
        Preconditions.checkNotNull(startDate, "startDate may not be null");
        Preconditions.checkNotNull(timeZone, "timeZone may not be null");
        next = startDate.toInstant().atZone(timeZone.toZoneId());
        return this;
    }

    /**
     * Restarts the sequence at the given date, in the default time zone
     * @return this instance, for chaining
     * @deprecated use one of the other <code>startingAt()</code> methods taking java.time types as argument
     */
    @Deprecated
    public DateSequenceValueGenerator startingAt(@Nonnull Date startDate) {
        return startingAt(startDate, TimeZone.getDefault());
    }

    /**
     * Restarts the sequence at the given date
     * @return this instance, for chaining
     * @deprecated use one of the other <code>startingAt()</code> methods taking java.time types as argument
     */
    @Deprecated
    public DateSequenceValueGenerator startingAt(@Nonnull Calendar startDate) {
        Preconditions.checkNotNull(startDate, "startDate may not be null");
        next = startDate.toInstant().atZone(startDate.getTimeZone().toZoneId());
        return this;
    }

    /**
     * Restarts the sequence at the given date, in the default time zone
     * @param startDate the starting date, as a String. The supported formats are the same as the ones supported by
     * {@link com.ninja_squad.dbsetup.bind.Binders#timestampBinder()}, i.e. the formats supported by
     * <code>java.sql.Timestamp.valueOf()</code> and <code>java.sql.Date.valueOf()</code>
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull String startDate) {
        Preconditions.checkNotNull(startDate, "startDate may not be null");
        if (startDate.length() >= MIN_NUMBER_OF_CHARS_FOR_TIMESTAMP) {
            return startingAt(new Date(Timestamp.valueOf(startDate).getTime()));
        }
        else {
            return startingAt(new Date(java.sql.Date.valueOf(startDate).getTime()));
        }
    }

    /**
     * Restarts the sequence at the given local date, in the default time zone
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull LocalDate startDate) {
        return startingAt(startDate.atStartOfDay());
    }

    /**
     * Restarts the sequence at the given local date time, in the default time zone
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull LocalDateTime startDate) {
        return startingAt(startDate.atZone(ZoneId.systemDefault()));
    }

    /**
     * Restarts the sequence at the given zoned date time
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull ZonedDateTime startDate) {
        next = startDate;
        return this;
    }

    /**
     * Increments the date by the given increment of the given unit.
     * @return this instance, for chaining
     * @deprecated use the other {@link #incrementingBy(int, TemporalUnit)} method
     */
    public DateSequenceValueGenerator incrementingBy(int increment, @Nonnull CalendarField unit) {
        Preconditions.checkNotNull(unit, "unit may not be null");
        return incrementingBy(increment, unit.toTemporalUnit());
    }

    /**
     * Increments the date by the given increment of the given unit. One of the constants of ChronoField is typically
     * used for the unit.
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator incrementingBy(int increment, @Nonnull TemporalUnit unit) {
        Preconditions.checkNotNull(unit, "unit may not be null");
        this.increment = increment;
        this.unit = unit;
        return this;
    }

    @Override
    public ZonedDateTime nextValue() {
        ZonedDateTime result = next;
        next = next.plus(increment, unit);
        return result;
    }

    @Override
    public String toString() {
        return "DateSequenceValueGenerator["
               + "next=" + next
               + ", increment=" + increment
               + ", unit=" + unit
               + "]";
    }
}
