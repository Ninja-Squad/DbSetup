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

package com.ninja_squad.dbsetup;

import java.util.List;

import javax.annotation.Nonnull;

import com.ninja_squad.dbsetup.operation.CompositeOperation;
import com.ninja_squad.dbsetup.operation.Delete;
import com.ninja_squad.dbsetup.operation.DeleteAll;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;
import com.ninja_squad.dbsetup.operation.SqlOperation;
import com.ninja_squad.dbsetup.operation.Truncate;

/**
 * A static factory class for operations. Static import of this class can help make the code more readable.
 * @author JB Nizet
 */
public final class Operations {
    private Operations() {
    }

    /**
     * Creates a <code>delete from table</code> operation.
     * @param table the table to delete all from
     * @see DeleteAll
     */
    public static DeleteAll deleteAllFrom(@Nonnull String table) {
        return DeleteAll.from(table);
    }

    /**
     * Creates a sequence of <code>delete from table</code> operations.
     * @param tables the tables to delete all from
     * @see DeleteAll
     */
    public static Operation deleteAllFrom(@Nonnull String... tables) {
        return DeleteAll.from(tables);
    }

    /**
     * Creates a sequence of <code>delete from ...</code> operations.
     * @param tables the tables to delete all from
     * @see DeleteAll
     */
    public static Operation deleteAllFrom(@Nonnull List<String> tables) {
        return DeleteAll.from(tables);
    }

    /**
     * Creates a sequence of <code>delete from ... where ...</code> operations.
     * @param insertOperation the insertOperation used to identify what to delete (table and rows) 
     * @param pkColumn the name of the column used to match rows to delete (ususally a column with 
     *          an unicity constraint such as the primary key column) 
     * @return the created Delete Operation
     * @see Delete
     */
    public static Operation deleteFrom(@Nonnull Insert insertOperation, @Nonnull String pkColumn) {
        return Delete.from(insertOperation, pkColumn);
    }

    /**
     * Creates a <code>truncate table ...</code> operation.
     * @param table the table to truncate
     * @see Truncate
     */
    public static Truncate truncate(@Nonnull String table) {
        return Truncate.table(table);
    }

    /**
     * Creates a sequence of <code>truncate table ...</code> operations.
     * @param tables the tables to truncate
     * @see Truncate
     */
    public static Operation truncate(@Nonnull String... tables) {
        return Truncate.tables(tables);
    }

    /**
     * Creates a sequence of <code>truncate table ...</code> operations.
     * @param tables the tables to truncate
     * @see Truncate
     */
    public static Operation truncate(@Nonnull List<String> tables) {
        return Truncate.tables(tables);
    }

    /**
     * Creates a SQL operation.
     * @param sqlStatement the SQL statement to execute (using {@link java.sql.Statement#executeUpdate(String)})
     * @see SqlOperation
     */
    public static SqlOperation sql(@Nonnull String sqlStatement) {
        return SqlOperation.of(sqlStatement);
    }

    /**
     * Creates a sequence of SQL operations.
     * @param sqlStatements the SQL statements to execute (using {@link java.sql.Statement#executeUpdate(String)})
     * @see SqlOperation
     */
    public static Operation sql(@Nonnull String... sqlStatements) {
        return SqlOperation.of(sqlStatements);
    }

    /**
     * Creates a sequence of SQL operations.
     * @param sqlStatements the SQL statements to execute (using {@link java.sql.Statement#executeUpdate(String)})
     * @see SqlOperation
     */
    public static Operation sql(@Nonnull List<String> sqlStatements) {
        return SqlOperation.of(sqlStatements);
    }

    /**
     * Creates a builder for a sequence of insert operations.
     * @param table the table to insert into
     * @see Insert
     */
    public static Insert.Builder insertInto(@Nonnull String table) {
        return Insert.into(table);
    }

    /**
     * Creates a sequence of operations.
     * @param operations the operations to put in a sequence
     * @see CompositeOperation
     */
    public static Operation sequenceOf(@Nonnull Operation... operations) {
        return CompositeOperation.sequenceOf(operations);
    }

    /**
     * Creates a sequence of operations.
     * @param operations the operations to put in a sequence
     * @see CompositeOperation
     */
    public static Operation sequenceOf(@Nonnull List<? extends Operation> operations) {
        return CompositeOperation.sequenceOf(operations);
    }
}
