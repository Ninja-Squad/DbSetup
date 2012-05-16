package com.ninja_squad.dbsetup.operation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.ninja_squad.dbsetup.bind.BinderConfiguration;

/**
 * A composite operation or, in other words, an operation which consists in executing a sequence of other operations.
 * @author JB
 */
@Immutable
public final class CompositeOperation implements Operation {

    private static final Operation NOP = new Operation() {

        @Override
        public void execute(Connection connection, BinderConfiguration configuration) {
        }

        @Override
        public String toString() {
            return "NOP";
        }
    };

    private final List<Operation> operations;

    private CompositeOperation(List<? extends Operation> operations) {
        this.operations = new ArrayList<Operation>(operations);
    }

    /**
     * Creates a new Operation containing all the given operations
     * @param operations the sequence of operations
     */
    public static Operation sequenceOf(@Nonnull Operation... operations) {
        return sequenceOf(Arrays.asList(operations));
    }

    /**
     * Creates a new Operation containing all the given operations
     * @param operations the sequence of operations
     */
    public static Operation sequenceOf(@Nonnull List<? extends Operation> operations) {
        if (operations.isEmpty()) {
            return NOP;
        }
        else if (operations.size() == 1) {
            return operations.get(0);
        }
        return new CompositeOperation(operations);
    }

    /**
     * Executes the sequence of operations
     * @throws SQLException as soon as one of the operations in the sequence throws a SQLException
     */
    @Override
    public void execute(Connection connection, BinderConfiguration configuration) throws SQLException {
        for (Operation operation : operations) {
            operation.execute(connection, configuration);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Operation operation : operations) {
            if (!first) {
                builder.append("\n");
            }
            else {
                first = false;
            }
            builder.append(operation);
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return operations.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        CompositeOperation other = (CompositeOperation) o;
        return this.operations.equals(other.operations);
    }
}
