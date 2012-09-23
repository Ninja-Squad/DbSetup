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

import javax.annotation.Nonnull;


/**
 * <p>
 * This class allows speeding up test execution, by avoiding re-executing the same sequence of database operations
 * before each test method even if each of these test methods leaves the database as it is (and only performs read-only
 * operations, which is the most frequent case).
 * </p>
 * <p>Example usage:</p>
 * <pre>
 * // the tracker is static because JUnit uses a separate Test instance for every test method.
 * private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
 *
 * &#064;Before
 * public void setUp() throws Exception {
 *     Operation operation =
 *         Operations.sequenceOf(
 *             CommonOperations.DELETE_ALL,
 *             CommonOperations.INSERT_REFERENCE_DATA,
 *             Operations.insertInto("CLIENT")
 *                       .columns("CLIENT_ID", "FIRST_NAME", "LAST_NAME", "DATE_OF_BIRTH", "COUNTRY_ID")
 *                       .values(1L, "John", "Doe", "1975-07-19", 1L)
 *                       .values(2L, "Jack", "Smith", "1969-08-22", 2L)
 *                       .build());
 *     DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
 *     dbSetupTracker.launchIfNecessary(dbSetup);
 * }
 *
 * &#064;Test
 * public void readOnlyTest1() {
 *     dbSetupTracker.ignoreNextLaunch();
 *     ...
 * }
 *
 * &#064;Test
 * public void readOnlyTest2() {
 *     dbSetupTracker.ignoreNextLaunch();
 *     ...
 * }
 *
 * &#064;Test
 * public void readOnlyTest3() {
 *     dbSetupTracker.ignoreNextLaunch();
 *     ...
 * }
 *
 * &#064;Test
 * public void readWriteTest1() {
 *     // No call to dbSetupTracker.ignoreNextLaunch();
 *     ...
 * }
 * </pre>
 * @author JB Nizet
 */
public final class DbSetupTracker {
    private DbSetup lastSetupLaunched;
    private boolean nextLaunchIgnored;

    /**
     * Executes the given DbSetup unless all the following conditions are <code>true</code>:
     * <ul>
     *   <li>{@link #ignoreNextLaunch()} has been called since the last call to this method;</li>
     *   <li>the given <code>dbSetup</code> is equals to the last DbSetup launched by this method</li>
     * </ul>
     * This method resets the <code>ignoreNextLaunch</code> flag to <code>false</code>.
     * @param dbSetup the DbSetup to execute (or ignore)
     */
    public void launchIfNecessary(@Nonnull DbSetup dbSetup) {
        boolean ignoreLaunch = nextLaunchIgnored && dbSetup.equals(lastSetupLaunched);
        nextLaunchIgnored = false;
        if (ignoreLaunch) {
            return;
        }
        dbSetup.launch();
        lastSetupLaunched = dbSetup;
    }

    /**
     * Marks the current test method as read-only, and thus the need for the next test method to re-execute the same
     * sequence of database setup operations.
     */
    public void ignoreNextLaunch() {
        this.nextLaunchIgnored = true;
    }

    @Override
    public String toString() {
        return "DbSetupTracker [lastSetupLaunched="
                + lastSetupLaunched
                + ", nextLaunchIgnored="
                + nextLaunchIgnored
                + "]";
    }
}
