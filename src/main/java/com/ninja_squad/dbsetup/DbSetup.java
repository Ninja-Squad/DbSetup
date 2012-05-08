package com.ninja_squad.dbsetup;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;
import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * Allows executing a sequence of database operations. This object is reusable, and can thus be used several times
 * to launch the same sequence of database operations. Here's a typical usage scenario in a unit test:
 * <pre>
 * &#064;Before
 * public void setUp() throws Exception {
 *     Operation operation =
 *          Operations.of(
 *             CommonOperations.DELETE_ALL,
 *             CommonOperations.INSERT_REFERENCE_DATA,
 *             Operations.insertInto("CLIENT")
 *                       .columns("CLIENT_ID", "FIRST_NAME", "LAST_NAME", "DATE_OF_BIRTH", "COUNTRY_ID")
 *                       .values(1L, "John", "Doe", "1975-07-19", 1L)
 *                       .values(2L, "Jack", "Smith", "1969-08-22", 2L)
 *                       .build());
 *     DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
 *     dbSetup.launch();
 * }
 * </pre>
 * In the above code, <code>CommonOperations.DELETE_ALL</code> and <code>CommonOperations.INSERT_REFERENCE_DATA</code>
 * are operations shared by multiple test classes.
 * <p>
 * Note that, to speed up test executions, a {@link DbSetupTracker} can be used, at the price of a slightly
 * bigger complexity.
 * @author JB
 */
public final class DbSetup {
    private final Destination destination;
    private final Operation operation;
    private final BinderConfiguration binderConfiguration;

    /**
     * Constructor which uses the {@link DefaultBinderConfiguration#INSTANCE default binder configuration}.
     * @param destination the destination of the sequence of database operations
     * @param operation the operation to execute (most of the time, an instance of
     * {@link com.ninja_squad.dbsetup.operation.CompositeOperation}
     */
    public DbSetup(@Nonnull Destination destination, @Nonnull Operation operation) {
        this(destination, operation, DefaultBinderConfiguration.INSTANCE);
    }

    /**
     * Constructor allowing to use a custom {@link BinderConfiguration}.
     * @param destination the destination of the sequence of database operations
     * @param operation the operation to execute (most of the time, an instance of
     * {@link com.ninja_squad.dbsetup.operation.CompositeOperation}
     * @param binderConfiguration the binder configuration to use.
     */
    public DbSetup(@Nonnull Destination destination,
                   @Nonnull Operation operation,
                   @Nonnull BinderConfiguration binderConfiguration) {
        Preconditions.checkNotNull(destination, "destination may not be null");
        Preconditions.checkNotNull(operation, "operation may not be null");
        Preconditions.checkNotNull(binderConfiguration, "binderConfiguration may not be null");

        this.destination = destination;
        this.operation = operation;
        this.binderConfiguration = binderConfiguration;
    }

    /**
     * Executes the sequence of operations. All the operations use the same connection, and are grouped
     * in a single transaction. The transaction is rolled back if any exception occurs.
     */
    public void launch() {
        try {
            Connection connection = destination.getConnection();
            try {
                connection.setAutoCommit(false);
                operation.execute(connection, binderConfiguration);
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                throw e;
            }
            catch (RuntimeException e) {
                connection.rollback();
                throw e;
            }
            finally {
                connection.close();
            }
        }
        catch (SQLException e) {
            throw new DbSetupRuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "DbSetup [destination="
               + destination
               + ", operation="
               + operation
               + ", binderConfiguration="
               + binderConfiguration
               + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + binderConfiguration.hashCode();
        result = prime * result + destination.hashCode();
        result = prime * result + operation.hashCode();
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
        DbSetup other = (DbSetup) obj;
        return binderConfiguration.equals(other.binderConfiguration)
               && destination.equals(other.destination)
               && operation.equals(other.operation);
    }
}
