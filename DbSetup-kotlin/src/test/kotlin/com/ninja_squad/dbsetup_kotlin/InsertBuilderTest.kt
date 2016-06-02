package com.ninja_squad.dbsetup_kotlin

import com.ninja_squad.dbsetup.generator.ValueGenerators
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author JB Nizet
 */
class InsertBuilderTest {
    @Test
    fun `should construct an insert with mapped values`() {
        val insert = insertInto("user") {
            mappedValues("id" to 1L, "name" to "John")
        }

        val expected = insertInto("user") {
            values(mapOf("id" to 1L, "name" to "John"))
        }

        assertEquals(expected, insert);
    }

    @Test
    fun `should construct an insert with repeating mapped values`() {
        val insert = insertInto("user") {
            withGeneratedValue("id", ValueGenerators.sequence())
            repeatingMappedValues("name" to "John").times(10)
        }

        val expected = insertInto("user") {
            withGeneratedValue("id", ValueGenerators.sequence())
            repeatingValues(mapOf("name" to "John")).times(10)
        }

        assertEquals(expected, insert);
    }
}
