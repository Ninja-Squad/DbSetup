package com.ninja_squad.dbsetup.destination;

import static org.junit.Assert.*;

import org.junit.Test;

public class DriverManagerDestinationTest {
    @Test
    public void equalsAndHashCodeWork() {
        Destination dest1 = new DriverManagerDestination("url", "user", "password");
        Destination dest1bis = new DriverManagerDestination("url", "user", "password");

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
