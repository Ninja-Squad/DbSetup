/*
 * The MIT License
 *
 * Copyright (c) 2016, Ninja Squad
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
package com.ninja_squad.dbsetup_kotlin

import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.Operations
import com.ninja_squad.dbsetup.bind.BinderConfiguration
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration
import com.ninja_squad.dbsetup.destination.Destination
import com.ninja_squad.dbsetup.operation.Insert
import com.ninja_squad.dbsetup.operation.Operation

/**
 * A builder allowing to configure a DbSetup from a lambda expression whose receiver type is this builder.
 * The intended usage is to use the [dbSetup] top level function.
 */
class DbSetupBuilder {

    /**
     * The destination of the DbSetup. It is mandatory
     */
    var destination: Destination? = null

    /**
     * The binder configuration of the DbSetup. It not set, the default configuration is used
     */
    var binderConfiguration: BinderConfiguration = DefaultBinderConfiguration.INSTANCE

    private val operations = mutableListOf<Operation>()

    /**
     * Adds an Insert operation to the DbSetup, using a lambda expression to configure it.
     *
     * Example usage:
     *
     * ```
     * dbSetup {
     *     insertInto("user") {
     *         columns("id", "name")
     *         values(1, "John Doe")
     *         ...
     *     }
     * }
     * ```
     *
     * @param table the name of the table to insert into
     * @param configure the function used to configure the insert.
     */
    inline fun insertInto(table: String, configure: Insert.Builder.() -> Unit) {
        val builder = Insert.into(table)
        builder.configure()
        execute(builder.build())
    }

    /**
     * Adds a DeleteAll operation to the DbSetup
     * @param table the table to delete from
     */
    fun deleteAllFrom(table: String) {
        execute(Operations.deleteAllFrom(table))
    }

    /**
     * Adds DeleteAll operations to the DbSetup
     * @param tables the tables to delete from
     */
    fun deleteAllFrom(vararg tables: String) {
        execute(Operations.deleteAllFrom(*tables))
    }

    /**
     * Adds DeleteAll operations to the DbSetup
     * @param tables the tables to delete from
     */
    fun deleteAllFrom(tables: List<String>) {
        execute(Operations.deleteAllFrom(tables))
    }

    /**
     * Adds a Truncate operation to the DbSetup
     * @param table the table to truncate
     */
    fun truncate(table: String) {
        execute(Operations.truncate(table))
    }

    /**
     * Adds Truncate operations to the DbSetup
     * @param tables the tables to delete from
     */
    fun truncate(vararg tables: String) {
        execute(Operations.truncate(*tables))
    }

    /**
     * Adds Truncate operations to the DbSetup
     * @param tables the tables to truncate
     */
    fun truncate(tables: List<String>) {
        execute(Operations.truncate(tables))
    }

    /**
     * Adds a SqlOperation to the DbSetup
     * @param statement the SQL statement to execute
     */
    fun sql(statement: String) {
        execute(Operations.sql(statement))
    }

    /**
     * Adds SqlOperations to the DbSetup
     * @param statements the SQL statements to execute
     */
    fun sql(vararg statements: String) {
        execute(Operations.sql(*statements))
    }

    /**
     * Adds SqlOperations to the DbSetup
     * @param statements the SQL statements to execute
     */
    fun sql(statements: List<String>) {
        execute(Operations.sql(statements))
    }

    /**
     * Adds an operation to the DbSetup. Custom extension functions typically delegate to this method to
     * add the operation they want.
     * @param operation the operation to add
     */
    fun execute(operation: Operation) {
        operations.add(operation)
    }

    /**
     * Builds the DbSetup. This method is called by the dbSetup function after the builder has been configured.
     * @throws IllegalStateException if the destination has not been set
     */
    internal fun build(): DbSetup {
        return DbSetup(destination ?: throw IllegalStateException("destination hasn't been set"),
                       Operations.sequenceOf(operations),
                       binderConfiguration)
    }
}
