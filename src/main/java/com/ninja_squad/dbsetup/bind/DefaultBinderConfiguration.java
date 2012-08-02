package com.ninja_squad.dbsetup.bind;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.ninja_squad.dbsetup.DbSetup;

/**
 * Default implementation of {@link BinderConfiguration}, used by default by {@link DbSetup}.
 * @author JB
 */
public class DefaultBinderConfiguration implements BinderConfiguration {

    /**
     * A shareable, reusable instance of this class.
     */
    public static final DefaultBinderConfiguration INSTANCE = new DefaultBinderConfiguration();

    /**
     * Constructor. Protected because it doesn't make much sense to instantiate this class,
     * but extending it can be useful.
     */
    protected DefaultBinderConfiguration() {
    }

    /**
     * Uses the parameter type of the given parameter and returns the following Binders depending on the type
     * got from the metadata.
     * <ul>
     *   <li>VARCHAR, CHAR, LONGNVARCHAR, LONGVARCHAR, NCHAR, NVARCHAR :
     *       {@link Binders#stringBinder()}</li>
     *   <li>DATE : {@link Binders#dateBinder()}</li>
     *   <li>TIME : {@link Binders#timeBinder()}</li>
     *   <li>TIMESTAMP : {@link Binders#timestampBinder()}</li>
     *   <li>INTEGER, BIGINT, SMALLINT, TINYINT : {@link Binders#integerBinder()}</li>
     *   <li>DECIMAL, DOUBLE, FLOAT, NUMERIC, REAL : {@link Binders#decimalBinder()}</li>
     *   <li>other : {@link Binders#defaultBinder()}</li>
     */
    @Override
    public Binder getBinder(ParameterMetaData metadata, int param) throws SQLException {
        int sqlType = metadata.getParameterType(param);
        if (sqlType == Types.DATE) {
            return Binders.dateBinder();
        }
        if (sqlType == Types.TIME) {
            return Binders.timeBinder();
        }
        if (sqlType == Types.TIMESTAMP) {
            return Binders.timestampBinder();
        }
        if (sqlType == Types.BIGINT
            || sqlType == Types.INTEGER
            || sqlType == Types.SMALLINT
            || sqlType == Types.TINYINT) {
            return Binders.integerBinder();
        }
        if (sqlType == Types.DECIMAL
            || sqlType == Types.DOUBLE
            || sqlType == Types.FLOAT
            || sqlType == Types.NUMERIC
            || sqlType == Types.REAL) {
            return Binders.decimalBinder();
        }
        if (sqlType == Types.VARCHAR
            || sqlType == Types.CHAR
            || sqlType == Types.LONGNVARCHAR
            || sqlType == Types.LONGVARCHAR
            || sqlType == Types.NCHAR
            || sqlType == Types.NVARCHAR) {
            return Binders.stringBinder();
        }
        return Binders.defaultBinder();
    }

    @Override
    public String toString() {
        return "DefaultBinderConfiguration";
    }
}
