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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author JB Nizet
 */
public class DriverManagerDestinationTest {
    @Test
    public void equalsAndHashCodeWork() {
        Destination dest1 = new DriverManagerDestination("url", "user", "password");
        Destination dest1bis = DriverManagerDestination.with("url", "user", "password");

        assertEquals(dest1, dest1);
        assertEquals(dest1, dest1bis);
        assertEquals(dest1.hashCode(), dest1bis.hashCode());
        assertFalse(dest1.equals(new DriverManagerDestination("url2", "user", "password")));
        assertFalse(dest1.equals(new DriverManagerDestination("url", "user2", "password")));
        assertFalse(dest1.equals(new DriverManagerDestination("url", "user", "password2")));
        assertFalse(dest1.equals(new DriverManagerDestination("url", null, "password")));
        assertFalse(dest1.equals(new DriverManagerDestination("url", "user", null)));
        assertFalse(new DriverManagerDestination("url", null, "password").equals(dest1));
        assertFalse(new DriverManagerDestination("url", "user", null).equals(dest1));
        assertFalse(dest1.equals(null));
        assertFalse(dest1.equals("hello"));

        assertEquals(new DriverManagerDestination("url", null, null), new DriverManagerDestination("url", null, null));
        assertEquals(new DriverManagerDestination("url", null, null).hashCode(),
                     new DriverManagerDestination("url", null, null).hashCode());
    }

    @Test
    public void toStringWorks() {
        assertEquals("DriverManagerDestination [url=theUrl, user=theUser, password=thePassword]",
                     new DriverManagerDestination("theUrl", "theUser", "thePassword").toString());
        assertEquals("DriverManagerDestination [url=theUrl, user=null, password=null]",
                     new DriverManagerDestination("theUrl", null, null).toString());
    }
}
