package com.ninja_squad.dbsetup.bind;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class DefaultBinderConfigurationTest {

    @Test
    public void shouldReturnDefaultBinderIfNoParameterMetadata() throws SQLException {
        assertEquals(Binders.defaultBinder(), DefaultBinderConfiguration.INSTANCE.getBinder(null, 1));
    }
}
