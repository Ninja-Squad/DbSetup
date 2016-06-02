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

import com.ninja_squad.dbsetup.operation.Insert

/**
 * Extension function of InsertBuilder allowing to specify values as columns/value pairs instead of a Map.
 *
 * Example usage:
 *
 * ```
 * insertInto("user") {
 *     columns("id", "name", "birthDate")
 *     mappedValues("id" to 1, "name" to "John Doe")
 * }
 * ```
 *
 * This is equivalent to
 *
 * ```
 * insertInto("user") {
 *     columns("id", "name", "birthDate")
 *     values(mapOf("id" to 1, "name" to "John Doe"))
 * }
 * ```
 *
 * but is a bit shorter. Beware to NOT use
 *
 * ```
 * insertInto("user") {
 *     columns("id", "name", "birthDate")
 *     values("id" to 1, "name" to "John Doe")
 * }
 * ```
 *
 * because that would try to insert the Pairs themselves into the table, rather than the values of the pairs.
 *
 * @param entries the column/value pairs of the row to insert
 * @return the Insert Builder for chaining (although that is usually not necessary with the Kotlin DSL)
 *
 * @author JB Nizet
 */
fun Insert.Builder.mappedValues(vararg entries: Pair<String, Any?>): Insert.Builder {
    return this.values(mapOf(*entries))
}

/**
 * Allows inserting the same values multiple times, by specifying them as colum/value pairs instead of a Map.
 *
 * Example usage:
 *
 * ```
 * insertInto("user") {
 *     columns("id", "firstName", "lastName")
 *     withGeneratedValue("id", ValueGenerators.sequence())
 *     repeatingMappedValues("firstName" to "John", "lastName" to "Doe").times(100)
 * }
 * ```
 *
 * This is equivalent to
 *
 * ```
 * insertInto("user") {
 *     columns("id", "firstName", "lastName")
 *     withGeneratedValue("id", ValueGenerators.sequence())
 *     repeatingValues(mapOf("firstName" to "John", "lastName" to "Doe")).times(100)
 * }
 * ```
 *
 * but is a bit shorter. Beware to NOT use
 *
 * ```
 * insertInto("user") {
 *     columns("id", "firstName", "lastName")
 *     withGeneratedValue("id", ValueGenerators.sequence())
 *     repeatingValues("firstName" to "John", "lastName" to "Doe").times(100)
 * }
 * ```
 *
 * because that would try to insert the Pairs themselves into the table, rather than the values of the pairs.
 *
 * @param entries the column/value pairs of the row to insert
 * @return the RowRepeater, on which you must call times(N) to specify how many similar rows to insert
 *
 * @author JB Nizet
 */
fun Insert.Builder.repeatingMappedValues(vararg entries: Pair<String, Any?>): Insert.RowRepeater {
    return this.repeatingValues(mapOf(*entries))
}
