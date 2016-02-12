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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ninja_squad.dbsetup.bind.Binder;
import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.bind.Binders;
import com.ninja_squad.dbsetup.generator.ValueGenerator;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * Operation which inserts one or several rows into a table. Example usage:
 * <pre>
 *   Insert insert =
 *       Insert.into("CLIENT")
 *             .columns("CLIENT_ID", "FIRST_NAME", "LAST_NAME", "DATE_OF_BIRTH", "CLIENT_TYPE")
 *             .values(1L, "John", "Doe", "1975-07-19", ClientType.NORMAL)
 *             .values(2L, "Jack", "Smith", "1969-08-22", ClientType.HIGH_PRIORITY)
 *             .withDefaultValue("DELETED", false)
 *             .withDefaultValue("VERSION", 1)
 *             .withBinder(new ClientTypeBinder(), "CLIENT_TYPE")
 *             .build();
 * </pre>
 *
 * The above operation will insert two rows inside the CLIENT table. For each row, the column DELETED will be set to
 * <code>false</code> and the column VERSION will be set to 1. For the column CLIENT_TYPE, instead of using the
 * {@link Binder} associated to the type of the column found in the metadata of the table, a custom binder will be used.
 * <p>
 * Instead of specifying values as an ordered sequence which must match the sequence of column names, some might prefer
 * passing a map of column/value associations. This makes things more verbose, but can be more readable in some cases,
 * when the number of columns is high. This also allows not specifying any value for columns that must stay null.
 * The map can be constructed like any other map and passed to the builder, or it can be added using a fluent builder.
 * The following snippet:
 *
 * <pre>
 *   Insert insert =
 *       Insert.into("CLIENT")
 *             .columns("CLIENT_ID", "FIRST_NAME", "LAST_NAME", "DATE_OF_BIRTH", "CLIENT_TYPE")
 *             .row().column("CLIENT_ID", 1L)
 *                   .column("FIRST_NAME", "John")
 *                   .column("LAST_NAME", "Doe")
 *                   .column("DATE_OF_BIRTH", "1975-07-19")
 *                   .end()
 *             .row().column("CLIENT_ID", 2L)
 *                   .column("FIRST_NAME", "Jack")
 *                   .column("LAST_NAME", "Smith")
 *                   .end() // null date of birth, because it's not in the row
 *             .build();
 * </pre>
 *
 * is thus equivalent to:
 *
 * <pre>
 *   Map&lt;String, Object&gt; johnDoe = new HashMap&lt;String, Object&gt;();
 *   johnDoe.put("CLIENT_ID", 1L);
 *   johnDoe.put("FIRST_NAME", "John");
 *   johnDoe.put("LAST_NAME", "Doe");
 *   johnDoe.put("DATE_OF_BIRTH", "1975-07-19");
 *
 *   Map&lt;String, Object&gt; jackSmith = new HashMap&lt;String, Object&gt;();
 *   jackSmith.put("CLIENT_ID", 2L);
 *   jackSmith.put("FIRST_NAME", "Jack");
 *   jackSmith.put("LAST_NAME", "Smith");
 *
 *   Insert insert =
 *       Insert.into("CLIENT")
 *             .columns("CLIENT_ID", "FIRST_NAME", "LAST_NAME", "DATE_OF_BIRTH", "CLIENT_TYPE")
 *             .values(johnDoe)
 *             .values(jackSmith)
 *             .build();
 * </pre>
 *
 * When building the Insert using column/value associations, it might seem redundant to specify the set of column names
 * before inserting the rows. Remember, though, that all the rows of an Insert are inserted using the same
 * parameterized SQL query. We thus need a robust and easy way to know all the columns to insert for every row of the
 * insert. To be able to spot errors easily and early, and to avoid complex rules, the rule is thus simple: the set of
 * columns (excluding the generated ones) is specified either by columns(), or by the columns of the first row. All the
 * subsequent rows may not have additional columns. And <code>null</code> is inserted for all the absent columns of the
 * subsequent rows. The above example can thus be written as
 *
 * <pre>
 *   Insert insert =
 *       Insert.into("CLIENT")
 *             .row().column("CLIENT_ID", 1L)
 *                   .column("FIRST_NAME", "John")
 *                   .column("LAST_NAME", "Doe")
 *                   .column("DATE_OF_BIRTH", "1975-07-19")
 *                   .end()
 *             .row().column("CLIENT_ID", 2L)
 *                   .column("FIRST_NAME", "Jack")
 *                   .column("LAST_NAME", "Smith")
 *                   .end() // null date of birth, because it's not in the row
 *             .build();
 * </pre>
 *
 * but the following will throw an exception, because the DATE_OF_BIRTH column is not part of the first row:
 *
 * <pre>
 *   Insert insert =
 *       Insert.into("CLIENT")
 *             .row().column("CLIENT_ID", 2L)
 *                   .column("FIRST_NAME", "Jack")
 *                   .column("LAST_NAME", "Smith")
 *                   .column("CLIENT_TYPE", ClientType.HIGH_PRIORITY)
 *                   .end()
 *             .row().column("CLIENT_ID", 1L)
 *                   .column("FIRST_NAME", "John")
 *                   .column("LAST_NAME", "Doe")
 *                   .column("DATE_OF_BIRTH", "1975-07-19")
 *                   .column("CLIENT_TYPE", ClientType.NORMAL)
 *                   .end()
 *             .build();
 * </pre>
 *
 * @author JB Nizet
 */
@Immutable
public final class Insert implements Operation {
    private final String table;
    private final List<String> columnNames;
    private final Map<String, List<Object>> generatedValues;
    private final List<List<?>> rows;
    private final boolean metadataUsed;

    private final Map<String, Binder> binders;

    private Insert(Builder builder) {
        this.table = builder.table;
        this.columnNames = builder.columnNames;
        this.rows = builder.rows;
        this.generatedValues = generateValues(builder.valueGenerators, rows.size());
        this.binders = builder.binders;
        this.metadataUsed = builder.metadataUsed;
    }

    private Map<String, List<Object>> generateValues(Map<String, ValueGenerator<?>> valueGenerators,
                                                      int count) {
        Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>();
        for (Map.Entry<String, ValueGenerator<?>> entry : valueGenerators.entrySet()) {
            result.put(entry.getKey(), generateValues(entry.getValue(), count));
        }
        return result;
    }

    private List<Object> generateValues(ValueGenerator<?> valueGenerator, int count) {
        List<Object> result = new ArrayList<Object>(count);
        for (int i = 0; i < count; i++) {
            result.add(valueGenerator.nextValue());
        }
        return result;
    }

    /**
     * Inserts the values and generated values in the table. Unless <code>useMetadata</code> has been set to
     * <code>false</code>, the given configuration is used to get the appropriate binder. Nevertheless, if a binder
     * has explicitly been associated to a given column, this binder will always be used for this column.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
        value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
        justification = "The point here is precisely to compose a SQL String from column names coming from the user")
    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        List<String> allColumnNames = new ArrayList<String>(columnNames);
        allColumnNames.addAll(generatedValues.keySet());

        String query = generateSqlQuery(allColumnNames);

        PreparedStatement stmt = connection.prepareStatement(query);

        try {
            Map<String, Binder> usedBinders = initializeBinders(stmt, allColumnNames, configuration);

            int rowIndex = 0;
            for (List<?> row : rows) {
                int i = 0;
                for (Object value : row) {
                    String columnName = columnNames.get(i);
                    Binder binder = usedBinders.get(columnName);
                    binder.bind(stmt, i + 1, value);
                    i++;
                }
                for (Map.Entry<String, List<Object>> entry : generatedValues.entrySet()) {
                    String columnName = entry.getKey();
                    List<Object> rowValues = entry.getValue();
                    Binder binder = usedBinders.get(columnName);
                    binder.bind(stmt, i + 1, rowValues.get(rowIndex));
                    i++;
                }

                stmt.executeUpdate();
                rowIndex++;
            }
        }
        finally {
            stmt.close();
        }
    }

    /**
     * Gets the number of rows that are inserted in the database table when this insert operation is executed.
     */
    public int getRowCount() {
        return rows.size();
    }

    private String generateSqlQuery(List<String> allColumnNames) {
        StringBuilder sql = new StringBuilder("insert into ").append(table).append(" (");
        for (Iterator<String> it = allColumnNames.iterator(); it.hasNext(); ) {
            String columnName = it.next();
            sql.append(columnName);
            if (it.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(") values (");
        for (Iterator<String> it = allColumnNames.iterator(); it.hasNext(); ) {
            it.next();
            sql.append('?');
            if (it.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(')');

        return sql.toString();
    }

    private Map<String, Binder> initializeBinders(PreparedStatement stmt,
                                                  List<String> allColumnNames,
                                                  BinderConfiguration configuration) throws SQLException {
        Map<String, Binder> result = new HashMap<String, Binder>();
        ParameterMetaData metadata = null;
        if (metadataUsed) {
            try {
                metadata = stmt.getParameterMetaData();
            }
            catch (SQLException e) {
                metadata = null;
                // the parameter metadata are probably not supported by the database. Pass null to the configuration.
                // The default configuration will return the default binder, just as if useMetadata(false) had been used
            }
        }
        int i = 1;
        for (String columnName : allColumnNames) {
            Binder binder = this.binders.get(columnName);
            if (binder == null) {
                binder = configuration.getBinder(metadata, i);
                if (binder == null) {
                    throw new IllegalStateException("null binder returned from configuration "
                                                    + configuration.getClass());
                }
            }
            result.put(columnName, binder);
            i++;
        }
        return result;
    }

    @Override
    public String toString() {
        return "insert into "
               + table
               + " [columns="
               + columnNames
               + ", generatedValues="
               + generatedValues
               + ", rows="
               + rows
               + ", metadataUsed="
               + metadataUsed
               + ", binders="
               + binders
               + "]";

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + binders.hashCode();
        result = prime * result + columnNames.hashCode();
        result = prime * result + generatedValues.hashCode();
        result = prime * result + Boolean.valueOf(metadataUsed).hashCode();
        result = prime * result + rows.hashCode();
        result = prime * result + table.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Insert other = (Insert) obj;

        return binders.equals(other.binders)
               && columnNames.equals(other.columnNames)
               && generatedValues.equals(other.generatedValues)
               && metadataUsed == other.metadataUsed
               && rows.equals(other.rows)
               && table.equals(other.table);
    }

    /**
     * Creates a new Builder instance, in order to build an Insert operation into the given table
     * @param table the name of the table to insert into
     * @return the created Builder
     */
    public static Builder into(@Nonnull String table) {
        Preconditions.checkNotNull(table, "table may not be null");
        return new Builder(table);
    }

    /**
     * A builder used to create an Insert operation. Such a builder may only be used once. Once it has built its Insert
     * operation, all its methods throw an {@link IllegalStateException}.
     * @see Insert
     * @see Insert#into(String)
     * @author JB Nizet
     */
    public static final class Builder {
        private final String table;
        private final List<String> columnNames = new ArrayList<String>();
        private final Map<String, ValueGenerator<?>> valueGenerators = new LinkedHashMap<String, ValueGenerator<?>>();
        private final List<List<?>> rows = new ArrayList<List<?>>();

        private boolean metadataUsed = true;
        private final Map<String, Binder> binders = new HashMap<String, Binder>();

        private boolean built;

        private Builder(String table) {
            this.table = table;
        }

        /**
         * Specifies the list of columns into which values will be inserted. The values must the be specified, after,
         * using the {@link #values(Object...)} method, or with the {@link #values(java.util.Map)} method, or by adding
         * a row with named columns fluently using {@link #row()}.
         * @param columns the names of the columns to insert into.
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if this method has already been
         * called, or if one of the given columns is also specified as one of the generated value columns, or if the
         * set of columns has already been defined by adding a first row to the builder.
         */
        public Builder columns(@Nonnull String... columns) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkState(columnNames.isEmpty(), "columns have already been specified");
            for (String column : columns) {
                Preconditions.checkNotNull(column, "column may not be null");
                Preconditions.checkState(!valueGenerators.containsKey(column),
                                         "column "
                                             + column
                                             + " has already been specified as generated value column");
            }
            columnNames.addAll(Arrays.asList(columns));
            return this;
        }

        /**
         * Adds a row of values to insert.
         * @param values the values to insert.
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if the number of values doesn't match
         * the number of columns.
         */
        public Builder values(@Nonnull Object... values) {
            return addRepeatingValues(Arrays.asList(values), 1);
        }

        /**
         * Allows adding many rows with the same non-generated values to insert.
         * @param values the values to insert.
         * @return A RowRepeater, allowing to choose how many similar rows to add.
         * @throws IllegalStateException if the Insert has already been built, or if the number of values doesn't match
         * the number of columns.
         */
        public RowRepeater repeatingValues(@Nonnull Object... values) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkArgument(values.length == columnNames.size(),
                                        "The number of values doesn't match the number of columns");
            return new ListRowRepeater(this, Arrays.asList(values));
        }

        /**
         * Starts building a new row with named columns to insert. If the row is the first one being added and the
         * columns haven't been set yet by calling <code>columns()</code>, then the columns of this row constitute the
         * column names (excluding the generated ones) of the Insert being built
         * @return a {@link RowBuilder} instance, which, when built, will add a row (or several ones) to this insert
         * builder.
         * @throws IllegalStateException if the Insert has already been built.
         * @see RowBuilder
         */
        public RowBuilder row() {
            Preconditions.checkState(!built, "The insert has already been built");
            return new RowBuilder(this);
        }

        /**
         * Adds a row to this builder. If no row has been added yet and the columns haven't been set yet by calling
         * <code>columns()</code>, then the keys of this map constitute the column names (excluding the generated ones)
         * of the Insert being built, in the order of the keys in the map (which is arbitrary unless an ordered or
         * sorted map is used).
         * @param row the row to add. The keys of the map are the column names, which must match with
         * the column names specified in the call to {@link #columns(String...)}, or with the column names of the first
         * added row. If a column name is not present in the map, null is inserted for this column.
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built.
         * @throws IllegalArgumentException if a column name of the map doesn't match with any of the column names
         * specified with {@link #columns(String...)}
         */
        public Builder values(@Nonnull Map<String, ?> row) {
            return addRepeatingValues(row, 1);
        }

        /**
         * Allows adding many rows with the same non-generated values to insert.
         * @return A RowRepeater, allowing to choose how many similar rows to add.
         * @throws IllegalStateException if the Insert has already been built.
         * @see #values(Map)
         */
        public RowRepeater repeatingValues(@Nonnull Map<String, ?> row) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkNotNull(row, "The row may not be null");
            return new MapRowRepeater(this, row);
        }

        /**
         * Associates a Binder to one or several columns.
         * @param binder the binder to use, regardless of the metadata, for the given columns
         * @param columns the name of the columns to associate with the given Binder
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built,
         * @throws IllegalArgumentException if any of the given columns is not
         * part of the columns or "generated value" columns.
         */
        public Builder withBinder(@Nonnull Binder binder, @Nonnull String... columns) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkNotNull(binder, "binder may not be null");
            for (String columnName : columns) {
                Preconditions.checkArgument(this.columnNames.contains(columnName)
                                            || this.valueGenerators.containsKey(columnName),
                                            "column "
                                                + columnName
                                                + " is not one of the registered column names");
                binders.put(columnName, binder);
            }
            return this;
        }

        /**
         * Specifies a default value to be inserted in a column for all the rows inserted by the Insert operation.
         * Calling this method is equivalent to calling
         * <code>withGeneratedValue(column, ValueGenerators.constant(value))</code>
         * @param column the name of the column
         * @param value the default value to insert into the column
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if the given column is part
         * of the columns to insert.
         */
        public Builder withDefaultValue(@Nonnull String column, Object value) {
            return withGeneratedValue(column, ValueGenerators.constant(value));
        }

        /**
         * Allows the given column to be populated by a value generator, which will be called for every row of the
         * Insert operation being built.
         * @param column the name of the column
         * @param valueGenerator the generator generating values for the given column of every row
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if the given column is part
         * of the columns to insert.
         */
        public Builder withGeneratedValue(@Nonnull String column, @Nonnull ValueGenerator<?> valueGenerator) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkNotNull(column, "column may not be null");
            Preconditions.checkNotNull(valueGenerator, "valueGenerator may not be null");
            Preconditions.checkArgument(!columnNames.contains(column),
                                        "column "
                                        + column
                                        + " is already listed in the list of column names");
            valueGenerators.put(column, valueGenerator);
            return this;
        }

        /**
         * Determines if the metadata must be used to get the appropriate binder for each inserted column (except
         * the ones which have been associated explicitly with a Binder). The default is <code>true</code>. The insert
         * can be faster if set to <code>false</code>, but in this case, the binder used will be the one returned
         * by the {@link BinderConfiguration} for a null metadata (which is, by default, the
         * {@link Binders#defaultBinder() default binder}), except the ones which have been associated explicitly with
         * a Binder.<br>
         * Before version 1.3.0, a SQLException was thrown if the database doesn't support parameter metadata and
         * <code>useMetadata(false)</code> wasn't called. Since version 1.3.0, if <code>useMetadata</code> is true
         * (the default) but the database doesn't support metadata, then the default binder configuration returns the
         * default binder. Using this method is thus normally unnecessary as of 1.3.0.
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built.
         */
        public Builder useMetadata(boolean useMetadata) {
            Preconditions.checkState(!built, "The insert has already been built");
            this.metadataUsed = useMetadata;
            return this;
        }

        /**
         * Builds the Insert operation.
         * @return the created Insert operation.
         * @throws IllegalStateException if the Insert has already been built, or if no column and no generated value
         * column has been specified.
         */
        public Insert build() {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkState(!this.columnNames.isEmpty() || !this.valueGenerators.isEmpty(),
                                     "no column and no generated value column has been specified");
            built = true;
            return new Insert(this);
        }

        @Override
        public String toString() {
            return "insert into "
                + table
                + " [columns="
                + columnNames
                + ", rows="
                + rows
                + ", valueGenerators="
                + valueGenerators
                + ", metadataUsed="
                + metadataUsed
                + ", binders="
                + binders
                + ", built="
                + built
                + "]";
        }

        private Builder addRepeatingValues(List<?> values, int times) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkArgument(values.size() == columnNames.size(),
                                        "The number of values doesn't match the number of columns");

            List<Object> row = new ArrayList<Object>(values);
            for (int i = 0; i < times; i++) {
                rows.add(row);
            }
            return this;
        }

        private Builder addRepeatingValues(@Nonnull Map<String, ?> row, int times) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkNotNull(row, "The row may not be null");

            List<Object> values = mapToRow(row);
            for (int i = 0; i < times; i++) {
                rows.add(values);
            }
            return this;
        }

        private List<Object> mapToRow(@Nonnull Map<String, ?> row) {
            boolean setColumns = rows.isEmpty() && columnNames.isEmpty();
            if (setColumns) {
                columns(row.keySet().toArray(new String[row.size()]));
            }
            else {
                Set<String> rowColumnNames = new HashSet<String>(row.keySet());
                rowColumnNames.removeAll(columnNames);
                if (!rowColumnNames.isEmpty()) {
                    throw new IllegalArgumentException(
                        "The following columns of the row don't match with any column name: " + rowColumnNames);
                }
            }

            List<Object> values = new ArrayList<Object>(columnNames.size());
            for (String columnName : columnNames) {
                values.add(row.get(columnName));
            }
            return values;
        }
    }

    /**
     * A row builder, constructed with {@link com.ninja_squad.dbsetup.operation.Insert.Builder#row()}. This builder
     * allows adding a row with named columns to an Insert:
     *
     * <pre>
     *   Insert insert =
     *       Insert.into("CLIENT")
     *             .columns("CLIENT_ID", "FIRST_NAME", "LAST_NAME", "DATE_OF_BIRTH", "CLIENT_TYPE")
     *             .row().column("CLIENT_ID", 1L)
     *                   .column("FIRST_NAME", "John")
     *                   .column("LAST_NAME", "Doe")
     *                   .column("DATE_OF_BIRTH", "1975-07-19")
     *                   .column("CLIENT_TYPE", ClientType.NORMAL)
     *                   .end()
     *             .row().column("CLIENT_ID", 2L)
     *                   .column("FIRST_NAME", "Jack")
     *                   .column("LAST_NAME", "Smith")
     *                   .column("DATE_OF_BIRTH", "1969-08-22")
     *                   .column("CLIENT_TYPE", ClientType.HIGH_PRIORITY)
     *                   .end()
     *             .build();
     * </pre>
     *
     * You may omit the call to <code>columns()</code>. In that case, the columns of the Insert will be the columns
     * specified in the first added row.
     */
    public static final class RowBuilder {
        private final Builder builder;
        private final Map<String, Object> row;
        private boolean ended;

        private RowBuilder(Builder builder) {
            this.builder = builder;
            // note: very important to use a LinkedHashMap here, to guarantee the ordering of the columns.
            this.row = new LinkedHashMap<String, Object>();
        }

        /**
         * Adds a new named column to the row. If a previous value has already been added for the same column, it's
         * replaced by this new value.
         * @param name the name of the column, which must match with a column name defined in the Insert Builder
         * @param value the value of the column for the constructed row
         * @return this builder, for chaining
         * @throws IllegalArgumentException if the given name is not the name of one of the columns to insert
         */
        public RowBuilder column(@Nonnull String name, Object value) {
            Preconditions.checkState(!ended, "The row has already been ended and added to the Insert Builder");
            if (!builder.columnNames.isEmpty()) {
                Preconditions.checkNotNull(name, "the column name may not be null");
                Preconditions.checkArgument(builder.columnNames.contains(name),
                                            "column " + name + " is not one of the registered column names");
            }
            row.put(name, value);
            return this;
        }

        /**
         * Ends the row, adds it to the Insert Builder and returns it, for chaining.
         * @return the Insert Builder
         */
        public Builder end() {
            Preconditions.checkState(!ended, "The row has already been ended and added to the Insert Builder");
            ended = true;
            return builder.values(row);
        }

        /**
         * Ends the row, adds it to the Insert Builder the given amount of times, and returns it, for chaining.
         * @param times the number of rows to add. Must be &gt;= 0. If zero, no row is added.
         * @return the Insert Builder
         */
        public Builder times(int times) {
            Preconditions.checkArgument(times >= 0, "the number of repeating values must be >= 0");
            Preconditions.checkState(!ended, "The row has already been ended and added to the Insert Builder");
            ended = true;
            return builder.addRepeatingValues(row, times);
        }
    }

    /**
     * Allows inserting the same list of non-generated values several times.
     */
    public interface RowRepeater {
        /**
         * Adds several rows with the same non-generated values to the insert. This method can only be called once.
         * @param times the number of rows to add. Must be &gt;= 0. If zero, no row is added.
         * @return the Insert Builder, for chaining
         * @throws IllegalStateException if the rows have already been added
         */
        Builder times(int times);
    }

    /**
     * Base abstract class for row repeaters.
     */
    private abstract static class AbstractRowRepeater implements RowRepeater {
        protected final Builder builder;
        private boolean ended;

        public AbstractRowRepeater(Builder builder) {
            this.builder = builder;
        }

        protected abstract Builder doTimes(int times);

        @Override
        public Builder times(int times) {
            Preconditions.checkArgument(times >= 0, "the number of repeating values must be >= 0");
            Preconditions.checkState(!ended, "The rows have already been ended and added to the Insert Builder");
            ended = true;
            return doTimes(times);
        }
    }

    /**
     * Allows inserting the same list of non-generated values as list several times.
     */
    private static final class ListRowRepeater extends AbstractRowRepeater {
        private final List<Object> values;

        private ListRowRepeater(Builder builder, List<Object> values) {
            super(builder);
            this.values = values;
        }

        @Override
        public Builder doTimes(int times) {
            return builder.addRepeatingValues(values, times);
        }
    }

    /**
     * Allows inserting the same list of non-generated values as map several times.
     */
    private static final class MapRowRepeater extends AbstractRowRepeater {
        private final Map<String, ?> values;

        private MapRowRepeater(Builder builder, Map<String, ?> values) {
            super(builder);
            this.values = values;
        }

        @Override
        public Builder doTimes(int times) {
            return builder.addRepeatingValues(values, times);
        }
    }
}
