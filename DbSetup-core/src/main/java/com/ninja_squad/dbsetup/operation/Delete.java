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

import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.util.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An operation which deletes every rows from an existing {@link Insert Insert Operation }.
 * 
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
 *   Delete d = Delete.from(insert, "CLIENT_ID");
 * <pre>
 * 
 * SQL query: 
 * <pre>delete from CLIENT where CLIENT_ID in (1L, 2L)</pre>
 * 
 * Since the given Insert has already been built, all the rows, even the generated will be correctly deleted.
 * 
 * @author R. Flores, E. Kimmel
 */
@Immutable
public final class Delete implements Operation {

    private final Insert insertOperation;
    
    private final String pkColumn;
    
    private final String sqlQuery;

    private Delete(Insert insertOperation, String pkColumn) {
        Preconditions.checkNotNull(insertOperation, "insertOperation may not be null");
        Preconditions.checkNotNull(pkColumn, "pkColumn may not be null");
        Preconditions.checkArgument(insertOperation.getColumnNames().contains(pkColumn),
                "insertOperation should contain a column named '" + pkColumn + "'");
        Preconditions.checkState(insertOperation.getRowCount()>0,
                "insertOperation should contain at least one row to delete");
        this.insertOperation = insertOperation;
        this.pkColumn = pkColumn;
        this.sqlQuery = generateSqlQuery();
    }

    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            stmt.executeUpdate(sqlQuery);
        }
        finally {
            stmt.close();
        }
    }
    
    private String generateSqlQuery() {
        StringBuilder sql = new StringBuilder("delete from ").append(insertOperation.getTable()).
                append(" where ").append(pkColumn).append(" in (");
        List<Object> primaryKeys = extractIdentifiers();
        for (Iterator<Object> it = primaryKeys.iterator(); it.hasNext(); ) {
            Object pk = it.next();
            sql.append(pk);
            if (it.hasNext()) {
                sql.append(", ");
            }
        }
        sql.append(')');
        return sql.toString();
    }

    private List<Object> extractIdentifiers() {
        int pkIndex = insertOperation.getColumnNames().indexOf(pkColumn);
        List<Object> primaryKeys = new ArrayList<Object>();
        for (List<?> row : insertOperation.getRows()) {
            Object pk = row.get(pkIndex);
            primaryKeys.add(pk);
        }
        return primaryKeys;
    }

    /**
     * Returns an operation which deletes all the rows from the given
     * insertOperation using the given pkColumn to restrict on tuples.
     * 
     * @param insertOperation
     *            the insertOperation used to identify what to delete (table and
     *            rows)
     * @param pkColumn
     *            the name of the column used to match rows to delete (ususally
     *            a column with an unicity constraint)
     * @return the created Delete Operation
     */
    public static Delete from(@Nonnull Insert insertOperation, @Nonnull String pkColumn) {
        return new Delete(insertOperation, pkColumn);
    }

    @Override
    public String toString() {
        return sqlQuery;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + insertOperation.hashCode();
        result = prime * result + pkColumn.hashCode();
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
        Delete other = (Delete) obj;
        return this.insertOperation.equals(other.insertOperation) && this.pkColumn.equals(other.pkColumn);
    }

}
