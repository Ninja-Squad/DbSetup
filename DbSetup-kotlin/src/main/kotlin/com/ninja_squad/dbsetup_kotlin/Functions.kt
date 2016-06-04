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
import com.ninja_squad.dbsetup.bind.BinderConfiguration
import com.ninja_squad.dbsetup.bind.DefaultBinderConfiguration
import com.ninja_squad.dbsetup.destination.DataSourceDestination
import com.ninja_squad.dbsetup.destination.Destination
import com.ninja_squad.dbsetup.operation.Insert
import javax.sql.DataSource

/**
 * Top-level function allowing to create a DbSetup by passing a destination and a lambda expression used to configure
 * the operations that must be made.
 *
 * Example usage:
 *
 * ```
 * val setup = dbSetup(to = DriverManagerDestination(url, user, password)) {
 *     deleteAllFrom("user", "country")
 *     insertInto("country") {
 *         ...
 *     }
 *     insertInto("user") {
 *         ...
 *     }
 *     sql(...)
 * }
 * ```
 *
 * @param to the destination of the DbSetup
 * @param binderConfiguration a custom binder configuration. The default one is used if not specified
 * @param configure the function used to configure the DbSetup
 * @return the created DbSetup
 *
 * @author JB Nizet
 */
fun dbSetup(to: Destination,
            binderConfiguration: BinderConfiguration = DefaultBinderConfiguration.INSTANCE,
            configure: DbSetupBuilder.() -> Unit): DbSetup {
    val builder = DbSetupBuilder(to, binderConfiguration)
    builder.configure()
    return builder.build()
}

/**
 * Top-level function allowing to create a DbSetup by passing a DataSource and a lambda expression used to configure
 * the operations that must be made.
 *
 * Example usage:
 *
 * ```
 * val setup = dbSetup(to = dataSource) {
 *     deleteAllFrom("user", "country")
 *     insertInto("country") {
 *         ...
 *     }
 *     insertInto("user") {
 *         ...
 *     }
 *     sql(...)
 * }
 * ```
 *
 * @param to the destination of the DbSetup
 * @param binderConfiguration a custom binder configuration. The default one is used if not specified
 * @param configure the function used to configure the DbSetup
 * @return the created DbSetup
 *
 * @author JB Nizet
 */
fun dbSetup(to: DataSource,
            binderConfiguration: BinderConfiguration = DefaultBinderConfiguration.INSTANCE,
            configure: DbSetupBuilder.() -> Unit) = dbSetup(DataSourceDestination(to), binderConfiguration, configure)
/**
 * Top-level function allowing to create an Insert operation by passing a lambda expression configuring it.
 *
 * Example usage:
 *
 * ```
 * val insertUsers = insertInto("user") {
 *     columns("id", "name")
 *     values(1, "John Doe")
 *     values(2, "Jane Doe")
 * }
 * ```
 * @param table the name of the table to insert into
 * @param configure the function used to configure the Insert
 * @return the created Insert operation
 *
 * @author JB Nizet
 */
inline fun insertInto(table: String, configure: Insert.Builder.() -> Unit): Insert {
    val builder = Insert.into(table)
    builder.configure()
    return builder.build()
}
