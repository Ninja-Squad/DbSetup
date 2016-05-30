package com.ninja_squad.dbsetup.bind;

import org.junit.Test;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultBinderConfigurationTest {

    @Test
    public void shouldReturnDefaultBinderIfNoParameterMetadata() throws SQLException {
        assertEquals(Binders.defaultBinder(), DefaultBinderConfiguration.INSTANCE.getBinder(null, 1));
    }

    @Test
    public void shouldReturnDefaultBinderIfParameterTypeCantBeObtained() throws SQLException {
        ParameterMetaData mockMetaData = mock(ParameterMetaData.class);
        when(mockMetaData.getParameterType(1)).thenThrow(new SQLException());
        assertEquals(Binders.defaultBinder(), DefaultBinderConfiguration.INSTANCE.getBinder(mockMetaData, 1));
    }
}
