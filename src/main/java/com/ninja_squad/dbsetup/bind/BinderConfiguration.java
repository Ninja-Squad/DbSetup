package com.ninja_squad.dbsetup.bind;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;

/**
 * An object which returns the appropriate {@link Binder} based on the metadata of the prepared statement.
 * The default instance of this interface is {@link DefaultBinderConfiguration}. If the binders returned by this
 * default configuration don't fit for the particular database you're using, or if you would like the binders
 * returned by the configuration to support additional data types, you might want to provide a diferent implementation
 * of this interface to the {@link DbSetup}.
 * <p>
 * It's advised to make implementations of this interface immutable, and to make them implement equals and hashCode
 * in order for {@link DbSetupTracker} to function properly, or to make them singletons.
 * @author JB Nizet
 */
public interface BinderConfiguration {

    /**
     * Returns the appropriate {@link Binder} for the given parameter, based on the given metadata.
     * @param metadata the metadata allowing to decide which Binder to return
     * @param param the param for which a binder is requested
     * @return the binder for the given param and its metadata
     * @throws SQLException if a SQLException occurs while using the metadata
     */
    Binder getBinder(ParameterMetaData metadata, int param) throws SQLException;
}
