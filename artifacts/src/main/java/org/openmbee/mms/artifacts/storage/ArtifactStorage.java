package org.openmbee.mms.artifacts.storage;

import org.openmbee.mms.json.ElementJson;

public interface ArtifactStorage {

    byte[] get(String location, ElementJson element, String mimetype);
    //returns location
    String store(byte[] data, ElementJson element, String mimetype);
}
