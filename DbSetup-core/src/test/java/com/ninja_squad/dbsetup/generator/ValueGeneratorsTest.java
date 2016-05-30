package com.ninja_squad.dbsetup.generator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author JB
 */
public class ValueGeneratorsTest {

    @Test
    public void contantShouldReturnGeneratorWhichGeneratesAConstantValue() {
        ValueGenerator<String> constantGenerator = ValueGenerators.constant("hello");
        for (int i = 0; i < 3; i++) {
            assertEquals("hello", constantGenerator.nextValue());
        }
    }

    // other methods are tested by the test for the class returned by the method
}
