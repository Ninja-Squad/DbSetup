package com.ninja_squad.dbsetup.destination;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * A destination which uses the {@link DriverManager} to get a connection
 * @author JB Nizet
 */
@Immutable
public final class DriverManagerDestination implements Destination {

    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructor
     * @param url the URL of the database
     * @param user the user used to get a connection
     * @param password the password used to get a connection
     */
    public DriverManagerDestination(@Nonnull String url, String user, String password) {
        Preconditions.checkNotNull(url, "url may not be null");
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public String toString() {
        return "DriverManagerDestination [url="
               + url
               + ", user="
               + user
               + ", password="
               + password
               + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + url.hashCode();
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
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
        DriverManagerDestination other = (DriverManagerDestination) obj;
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        }
        else if (!password.equals(other.password)) {
            return false;
        }
        if (!url.equals(other.url)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        }
        else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }
}
