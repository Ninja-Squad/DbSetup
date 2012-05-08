package com.ninja_squad.dbsetup.operation;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.ninja_squad.dbsetup.bind.Binder;
import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.bind.Binders;
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
 *
 * @author JB
 */
@Immutable
public final class Insert implements Operation {
    private final String table;
    private final List<String> columnNames;
    private final Map<String, Object> defaultValues;
    private final List<List<?>> rows;
    private final boolean metadataUsed;

    private final Map<String, Binder> binders;

    private Insert(Builder builder) {
        this.table = builder.table;
        this.columnNames = builder.columnNames;
        this.defaultValues = builder.defaultValues;
        this.rows = builder.rows;
        this.binders = builder.binders;
        this.metadataUsed = builder.metadataUsed;
    }

    /**
     * Inserts the values and default values in the table. Unless <code>useMetadata</code> has been set to
     * <code>false</code>, the given configuration is used to get the appropriate binder. Nevertheless, if a binder
     * has explicitely been associated to a given column, this binder will always be used for this column.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
        value = "SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING",
        justification = "The point here is precisely to compose a SQL String from column names coming from the user")
    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        StringBuilder sql = new StringBuilder("insert into ").append(table).append(" (");

        List<String> allColumnNames = new ArrayList<String>(columnNames);
        allColumnNames.addAll(defaultValues.keySet());

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
            sql.append("?");
            if (it.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(")");

        PreparedStatement stmt = connection.prepareStatement(sql.toString());

        try {
            Map<String, Binder> metadataBinders = new HashMap<String, Binder>();
            if (metadataUsed) {
                initializeBinders(stmt, allColumnNames, configuration, metadataBinders);
            }

            for (List<?> row : rows) {
                int i = 0;
                for (Object value : row) {
                    String columnName = columnNames.get(i);
                    Binder binder = getBinder(columnName, metadataBinders);
                    binder.bind(stmt, i + 1, value);
                    i++;
                }
                for (Map.Entry<String, Object> defaultValue : defaultValues.entrySet()) {
                    String columnName = defaultValue.getKey();
                    Binder binder = getBinder(columnName, metadataBinders);
                    binder.bind(stmt, i + 1, defaultValue.getValue());
                    i++;
                }

                stmt.executeUpdate();
            }
        }
        finally {
            stmt.close();
        }
    }

    private void initializeBinders(PreparedStatement stmt,
                                   List<String> allColumnNames,
                                   BinderConfiguration configuration,
                                   Map<String, Binder> metadataBinders) throws SQLException {
        ParameterMetaData metadata = stmt.getParameterMetaData();
        int i = 1;
        for (String columnName : allColumnNames) {
            if (!this.binders.containsKey(columnName)) {
                metadataBinders.put(columnName, configuration.getBinder(metadata, i));
            }
            i++;
        }
    }

    private Binder getBinder(String columnName, Map<String, Binder> metadataBinders) {
        Binder result = binders.get(columnName);
        if (result == null) {
            result = metadataBinders.get(columnName);
        }
        if (result == null) {
            result = Binders.defaultBinder();
        }
        return result;
    }

    @Override
    public String toString() {
        return "insert into "
               + table
               + " [columns="
               + columnNames
               + ", defaultValues="
               + defaultValues
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
        result = prime * result + defaultValues.hashCode();
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
               && defaultValues.equals(other.defaultValues)
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
     * @see Insert#into(String)
     * @author JB
     */
    public static final class Builder {
        private final String table;
        private final List<String> columnNames = new ArrayList<String>();
        private final Map<String, Object> defaultValues = new LinkedHashMap<String, Object>();
        private final List<List<?>> rows = new ArrayList<List<?>>();

        private boolean metadataUsed = true;
        private final Map<String, Binder> binders = new HashMap<String, Binder>();

        private boolean built;

        private Builder(String table) {
            this.table = table;
        }

        /**
         * Specifies the list of columns into which values wil be inserted. The values must the be specifed, after,
         * using the {@link #values(Object...)} method.
         * @param columns the names of the columns to insert into.
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if this method has already been
         * called, or if one of the given columns is also specified as one of the "default value" columns.
         */
        public Builder columns(@Nonnull String... columns) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkState(columnNames.isEmpty(), "columns have already been specified");
            for (String column : columns) {
                Preconditions.checkNotNull(column, "column may not be null");
                Preconditions.checkState(!defaultValues.containsKey(column),
                                         "column "
                                             + column
                                             + " has already been specified as default value column");
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
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkArgument(values.length == columnNames.size(),
                                        "The number of values doesn't match the number of columns");
            rows.add(new ArrayList<Object>(Arrays.asList(values)));
            return this;
        }

        /**
         * Associates a Binder to one or several columns.
         * @param binder the binder to use, regardless of the metadata, for the given columns
         * @param columns the name of the columns to associate with the given Binder
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if any of the given columns is not
         * part of the columns or "default value" columns.
         */
        public Builder withBinder(@Nonnull Binder binder, @Nonnull String... columns) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkNotNull(binder, "binder may not be null");
            for (String columnName : columns) {
                Preconditions.checkArgument(this.columnNames.contains(columnName)
                                            || this.defaultValues.containsKey(columnName),
                                            "column "
                                                + columnName
                                                + " is not one of the registered column names");
                binders.put(columnName, binder);
            }
            return this;
        }

        /**
         * Specifies a default value to be inserted in a column for all the rows inserted by the Insert operation.
         * @param column the name of the column
         * @param value the default value to insert into the column
         * @return this Builder instance, for chaining.
         * @throws IllegalStateException if the Insert has already been built, or if the given column is part
         * of the columns to insert.
         */
        public Builder withDefaultValue(@Nonnull String column, Object value) {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkNotNull(column, "column may not be null");
            if (columnNames.contains(column)) {
                Preconditions.checkArgument(!columnNames.contains(column),
                                            "column "
                                                + column
                                                + " is already listed in the list of column names");
            }
            defaultValues.put(column, value);
            return this;
        }

        /**
         * Determines if the metadata must be used to get the appropriate binder for each inserted column (except
         * the ones which have been associated explicitely with a Binder). The default is <code>true</code>. The insert
         * can be faster if set to <code>false</code>, but in this case, the {@link Binders#defaultBinder() default
         * binder} will be used for all the columns (except the ones which have been associated explicitely with a
         * Binder).
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
         * @throws IllegalStateException if the Insert has already been built, or if no column and no default value
         * column has been specified.
         */
        public Insert build() {
            Preconditions.checkState(!built, "The insert has already been built");
            Preconditions.checkState(!this.columnNames.isEmpty() || !this.defaultValues.isEmpty(),
                                     "no column and no default value column has been specified");
            built = true;
            return new Insert(this);
        }
    }
}
