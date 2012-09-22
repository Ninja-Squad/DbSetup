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
    public void launchIfNecessaryLaunchesIfNotIgnored() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1, times(2)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryDoesntLaunchIfIgnored() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.ignoreNextLaunch();
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1, times(1)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryResetsTheIgnoreFlag() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.ignoreNextLaunch();
        tracker.launchIfNecessary(dbSetup1);
        tracker.launchIfNecessary(dbSetup1);
        verify(operation1, times(2)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void launchIfNecessaryDoesntLaunchIfDifferentSetup() throws SQLException {
        DbSetupTracker tracker = new DbSetupTracker();
        tracker.launchIfNecessary(dbSetup1);
        tracker.ignoreNextLaunch();
        tracker.launchIfNecessary(dbSetup2);
        verify(operation1, times(1)).execute(any(Connection.class), any(BinderConfiguration.class));
        verify(operation2, times(1)).execute(any(Connection.class), any(BinderConfiguration.class));
    }

    @Test
    public void toStringWorks() {
        DbSetupTracker tracker = new DbSetupTracker();
        assertEquals("DbSetupTracker [lastSetupLaunched=null, nextLaunchIgnored=false]", tracker.toString());
    }
}
