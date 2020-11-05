package org.openmbee.mms.twc.metadata;

import org.junit.Test;

import static org.junit.Assert.*;

public class TwcMetadataTest {

    @Test
    public void testIsCompleteNullHost() {
        TwcMetadata twcMetadata = new TwcMetadata();
        assertFalse(twcMetadata.isComplete());
    }

    @Test
    public void testIsCompleteEmptyHost() {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("");
        assertFalse(twcMetadata.isComplete());
    }

    @Test
    public void testIsCompleteNullWorkspace() {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        assertFalse(twcMetadata.isComplete());
    }

    @Test
    public void testIsCompleteEmptyWorkspace() {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("");
        assertFalse(twcMetadata.isComplete());
    }

    @Test
    public void testIsCompleteNullResource() {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        assertFalse(twcMetadata.isComplete());
    }

    @Test
    public void testIsCompleteEmptyResource() {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        twcMetadata.setResourceId("");
        assertFalse(twcMetadata.isComplete());
    }

    @Test
    public void testIsComplete_complete() {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        twcMetadata.setResourceId("resource");
        assertTrue(twcMetadata.isComplete());
    }
}