package com.ninja_squad.dbsetup;

import java.util.List;

import javax.annotation.Nonnull;

import com.ninja_squad.dbsetup.operation.CompositeOperation;
import com.ninja_squad.dbsetup.operation.DeleteAll;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;
import com.ninja_squad.dbsetup.operation.SqlOperation;
import com.ninja_squad.dbsetup.operation.Truncate;

/**
 * A static factory class for operations. Static import of this class can help make the code more readable.
 * @author JB
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
