package org.openmbee.mms.twc;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class TeamworkCloudTest {

    @Test
    public void getKnownNames_basic() {

        TeamworkCloud twc = new TeamworkCloud();
        twc.setUrl("one");

        twc.setAliases(List.of("two", "three"));

        Set<String> knownNames = twc.getKnownNames();

        assertTrue(knownNames.contains("one"));
        assertTrue(knownNames.contains("two"));
        assertTrue(knownNames.contains("three"));
        assertEquals(3, knownNames.size());

        assertSame(knownNames, twc.getKnownNames());
    }

    @Test
    public void getKnownNames_noaliases() {

        TeamworkCloud twc = new TeamworkCloud();
        twc.setUrl("one");

        Set<String> knownNames = twc.getKnownNames();

        assertTrue(knownNames.contains("one"));
        assertEquals(1, knownNames.size());

        assertSame(knownNames, twc.getKnownNames());
    }
}