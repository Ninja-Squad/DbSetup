/*
 * The MIT License
 *
 * Copyright (c) 2016, Ninja Squad
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
package com.ninja_squad.dbsetup_kotlin

import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.DbSetupTracker

/**
 * Extension function of DbSetup allowing to launch it with a tracker. This allows launching the setup in an easier way.
 *
 * Instead of doing
 *
 * ```
 * val theSetup = dbSetup {
 *     ...
 * }
 * tracker.launchIfNecessary(theSetup)
 * ```
 *
 * you can simply do
 *
 * ```
 * dbSetup {
 *     ...
 * }.launchWith(tracker)
 * ```
 *
 * @param tracker the tracker used to launch the DbSetup, if necessary.
 *
 * @author JB Nizet
 */
fun DbSetup.launchWith(tracker: DbSetupTracker) {
    tracker.launchIfNecessary(this)
}
