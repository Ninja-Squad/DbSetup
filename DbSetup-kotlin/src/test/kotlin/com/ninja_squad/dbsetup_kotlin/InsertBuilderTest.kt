package com.ninja_squad.dbsetup_kotlin

import com.ninja_squad.dbsetup.generator.ValueGenerators
import com.ninja_squad.dbsetup.operation.Insert
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author JB Nizet
 */
class InsertBuilderTest {
    @Test
    fun `should construct an insert with mapped values`() {
        val insert = Insert.into("user")
                           .mappedValues("id" to 1L, "name" to "John")
                           .build();

        val expected = Insert.into("user")
                             .values(mapOf("id" to 1L, "name" to "John"))
                             .build();

        assertEquals(expected, insert);
    }

    @Test
    fun `should construct an insert with repeating mapped values`() {
        val insert = Insert.into("user")
                           .withGeneratedValue("id", ValueGenerators.sequence())
                           .repeatingMappedValues("name" to "John").times(10)
                           .build();

        val expected = Insert.into("user")
                             .withGeneratedValue("id", ValueGenerators.sequence())
                             .repeatingValues(mapOf("name" to "John")).times(10)
                             .build();

        assertEquals(expected, insert);
    }
}
