package com.ninja_squad.dbsetup.destination;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.sql.DataSource;

import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * A destination which wraps a DataSource and gets its connection from the wrapped DataSource
 * @author JB
 */
@Immutable
public final class DataSourceDestination implements Destination {
    private final DataSource dataSource;

    /**
     * Constructor
     * @param dataSource the wrapped DataSource
     */
    public DataSourceDestination(@Nonnull DataSource dataSource) {
        Preconditions.checkNotNull(dataSource, "dataSource may not be null");
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public String toString() {
        return "DataSourceDestination [dataSource=" + dataSource + "]";
    }

    @Override
    public int hashCode() {
        return dataSource.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataSourceDestination other = (DataSourceDestination) obj;
        return dataSource.equals(other.dataSource);
    }
}
