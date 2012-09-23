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
 * @author JB Nizet
 */
@Immutable
public final class CompositeOperation implements Operation {

    private static final Operation NOP = new Operation() {

        @Override
        public void execute(Connection connection, BinderConfiguration configuration) {
            // does nothing since it's a NOP
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
