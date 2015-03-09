/*
 * The MIT License
 *
 * Copyright (c) 2012, Ninja Squad
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

package com.ninja_squad.dbsetup.destination;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import com.ninja_squad.dbsetup.util.Preconditions;

/**
 * A destination which wraps a Connection directly 
 * @author  Abner Oliveira (https://github.com/abner)
 */
public class ConnectionDestination implements Destination {

	private final Connection connection;
	
	/***
	 * Constructor
	 * @param the wrapped connection
	 */
	public ConnectionDestination(@Nonnull Connection connection) {
		 Preconditions.checkNotNull(connection, "connection may not be null");
		this.connection = connection;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return this.connection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((connection == null) ? 0 : connection.hashCode());
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
		ConnectionDestination other = (ConnectionDestination) obj;
		if (connection == null) {
			if (other.connection != null) {
				return false;
			}
		} else if (!connection.equals(other.connection)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionDestination [connection=" + connection + "]";
	}
	
}
