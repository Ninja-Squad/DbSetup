package com.ninja_squad.dbsetup.generator;

import com.ninja_squad.dbsetup.util.Preconditions;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A {@link ValueGenerator} that returns a sequence of dates, starting at a given date and incremented by a given
 * time, specified as an increment and a calendar field.
 * @author JB
 */
public final class DateSequenceValueGenerator implements ValueGenerator<Date> {

    // the number of chars in yyyy-mm-dd hh:mm:ss
    private static final int MIN_NUMBER_OF_CHARS_FOR_TIMESTAMP = 19;

    /**
     * The available units for the increment of this sequence
     */
    public enum CalendarField {
        YEAR(Calendar.YEAR),
        MONTH(Calendar.MONTH),
        DAY(Calendar.DATE),
        HOUR(Calendar.HOUR),
        MINUTE(Calendar.MINUTE),
        SECOND(Calendar.SECOND),
        MILLISECOND(Calendar.MILLISECOND);

        private int field;

        CalendarField(int field) {
            this.field = field;
        }

        private int getField() {
            return field;
        }
    }

    private Calendar next;
    private int increment;
    private CalendarField unit;

    DateSequenceValueGenerator() {
        this(today(), 1, CalendarField.DAY);
    }

    private DateSequenceValueGenerator(Calendar next, int increment, CalendarField unit) {
        this.next = next;
        this.increment = increment;
        this.unit = unit;
    }

    private static Calendar today() {
        Calendar result = Calendar.getInstance();
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    /**
     * Restarts the sequence at the given date, in the given time zone
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull Date startDate, @Nonnull TimeZone timeZone) {
        Preconditions.checkNotNull(startDate, "startDate may not be null");
        Preconditions.checkNotNull(timeZone, "timeZone may not be null");
        next = Calendar.getInstance(timeZone);
        next.setTime(startDate);
        return this;
    }

    /**
     * Restarts the sequence at the given date, in the default time zone
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull Date startDate) {
        return startingAt(startDate, TimeZone.getDefault());
    }

    /**
     * Restarts the sequence at the given date
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator startingAt(@Nonnull Calendar startDate) {
        Preconditions.checkNotNull(startDate, "startDate may not be null");
        next = (Calendar) startDate.clone();
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
            return startingAt(Timestamp.valueOf(startDate));
        }
        else {
            return startingAt(java.sql.Date.valueOf(startDate));
        }
    }

    /**
     * Increments the date by the given increment of the given unit.
     * @return this instance, for chaining
     */
    public DateSequenceValueGenerator incrementingBy(int increment, @Nonnull CalendarField unit) {
        Preconditions.checkNotNull(unit, "unit may not be null");
        this.increment = increment;
        this.unit = unit;
        return this;
    }

    @Override
    public Date nextValue() {
        Date result = next.getTime();
        next.add(unit.getField(), increment);
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
