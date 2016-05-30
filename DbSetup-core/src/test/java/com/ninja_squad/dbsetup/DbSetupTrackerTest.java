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

package com.ninja_squad.dbsetup;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.bind.BinderConfiguration;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;

/**
 * @author JB Nizet
 */
public class DbSetupTrackerTest {

    private Operation operation1;
    private DbSetup dbSetup1;

    private Operation operation2;
    private DbSetup dbSetup2;

    @Before
    public void prepare() throws SQLException {
        Destination destination = mock(Destination.class);
        Connection connection = mock(Connection.class);
        when(destination.getConnection()).thenReturn(connection);
        operation1 = mock(Operation.class);
        operation2 = mock(Operation.class);

        dbSetup1 = new DbSetup(destination, operation1);
        dbSetup2 = new DbSetup(destination, operation2);
    }

    @Test
    public void launchIfNecessaryLaunchesTheFirstTime() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryLaunchesIfNotSkipped() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1, times(2)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryDoesntLaunchIfSkipped() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.skipNextLaunch();
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1, times(1)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryResetsTheSkipFlag() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.skipNextLaunch();
        tracker.launchIfNecessary(dbSetup1);
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1, times(2)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryDoesntLaunchIfDifferentSetup() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.skipNextLaunch();
        tracker.launchIfNecessary(dbSetup2);
        verify(operation1, times(1)).execute(any(Connection.class), any(BinderConfiguration.class));
        verify(operation2, times(1)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void toStringWorks() {
        DbSetupTracker tracker = new DbSetupTracker();
        assertEquals("DbSetupTracker [lastSetupLaunched=null, nextLaunchSkipped=false]", tracker.toString());
    }
}
